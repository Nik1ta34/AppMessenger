package com.messenger.messenge;

import com.google.gson.JsonSyntaxException;
import com.messenger.messenge.models.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Server  {
    private ServerSocket serverSocket;
    private Map<String, Socket> users = new ConcurrentHashMap<>();
    private Map<String, String> userCredentials = new ConcurrentHashMap<>();
    private Map<String, List<MessageModel>> userChatHistory = new ConcurrentHashMap<>();
    private Gson gson = new Gson().newBuilder().create();
    private static final Logger logger = LogManager.getLogger(Server.class.getName());
    /**
     * Создает новый экземпляр Server, прослушивающий указанный порт.
     *
     * @param port Порт, на котором будет слушать сервер.
     */
    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
            logger.info("Сервер запущен на порту " + port);
        } catch (IOException e) {
            logger.error("Ошибка при запуске сервера: " + e.getMessage(), e);
        }
    }

    /**
     * Запускает сервер и непрерывно принимает подключения клиентов.
     * Для каждого подключения клиента создается новый поток ClientHandler.
     */
    public void start() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                new ClientHandler(socket).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ClientHandler extends Thread {
        private DataInputStream dis;
        private DataOutputStream dos;
        private Socket socket;
        private String username;
        private Boolean stop;
        private static final Logger logger = LogManager.getLogger(ClientHandler.class.getName());

        /**
         * Создает новый объект ClientHandler для обработки связи с клиентом.
         *
         * @param socket Сокетное соединение клиента.
         */
        public ClientHandler(Socket socket) {
            this.socket = socket;
            this.stop = false;
        }

        /**
         * Основная логика выполнения для обработчика клиента.
         * Обрабатывает входящие сообщения, выполняет соответствующие действия в зависимости от типа сообщения.
         */
        @Override
        public void run() {
            try {
                dos = new DataOutputStream(socket.getOutputStream());
                dis = new DataInputStream(socket.getInputStream());

                while (!stop) {
                    String clientMessage = dis.readUTF();
                    if (!clientMessage.isEmpty()) {
                        try {
                            LogoutModel logoutModel = gson.fromJson(clientMessage, LogoutModel.class);
                            logoutUser(logoutModel);
                        } catch (Exception e){
                            logger.error("Исключение при выходе: " + e.getMessage(), e);
                        };
                        try {
                            RegistrationModel registrationModel = gson.fromJson(clientMessage, RegistrationModel.class);
                            registerUser(registrationModel);
                        } catch (Exception e){
                            logger.error("Исключение при выходе: " + e.getMessage(), e);
                        }
                        try {
                            LobbiesModel lobbiesModel = gson.fromJson(clientMessage, LobbiesModel.class);
                            getLobbies(lobbiesModel);
                        } catch (Exception e){
                            logger.error("Исключение при выходе: " + e.getMessage(), e);
                        }
                        try {
                            LobbyModel lobbyModel = gson.fromJson(clientMessage, LobbyModel.class);
                            getChat(lobbyModel);
                        } catch (Exception e){
                            logger.error("Исключение при выходе: " + e.getMessage(), e);
                        }
                        try {
                            MessageModel messageModel = gson.fromJson(clientMessage, MessageModel.class);
                            sendMessage(messageModel);
                        } catch (Exception e){
                            logger.error("Исключение при выходе: " + e.getMessage(), e);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("IOException в ClientHandler: " + e.getMessage(), e);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                logger.error("JsonSyntaxException в ClientHandler: " + e.getMessage(), e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.error("IOException при закрытии сокета: " + e.getMessage(), e);
                }
            }
        }

        /**
         * Регистрирует нового пользователя или выполняет вход существующего пользователя на основе предоставленной RegistrationModel.
         *
         * @param registrationModel RegistrationModel с учетными данными пользователя.
         */
        private void registerUser(RegistrationModel registrationModel) {
            try {
                if (registrationModel.getUsername() != null && registrationModel.getPassword() != null) {
                    if (!userCredentials.containsKey(registrationModel.getUsername())) {
                        this.username = registrationModel.getUsername();
                        userCredentials.put(registrationModel.getUsername(), registrationModel.getPassword());
                        users.put(registrationModel.getUsername(), socket);
                        userChatHistory.put(registrationModel.getUsername(), new ArrayList<>());
                        dos.writeUTF(gson.toJson(registrationModel));
                    } else {
                        if (Objects.equals(userCredentials.get(registrationModel.getUsername()), registrationModel.getPassword())) {
                            this.username = registrationModel.getUsername();
                            users.put(registrationModel.getUsername(), socket);
                            dos.writeUTF(gson.toJson(registrationModel));
                        } else {
                            registrationModel.setErrorMessage("Неверный пароль");
                            dos.writeUTF(gson.toJson(registrationModel));
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Получает список доступных лобби и отправляет его клиенту.
         *
         * @param lobbiesModel LobbiesModel с информацией о лобби.
         */
        private void getLobbies(LobbiesModel lobbiesModel) {
            try {
                if (lobbiesModel.getLobbies() != null) {
                    Set<LobbyModel> lobbySet = userCredentials
                            .keySet()
                            .stream()
                            .filter(user -> !Objects.equals(user, username))
                            .flatMap(user -> Stream.of(new LobbyModel(user, Collections.emptyList())))
                            .collect(Collectors.toSet());
                    LobbiesModel lobbies = new LobbiesModel(lobbySet);
                    if (lobbySet.isEmpty()) {
                        lobbies.setErrorMessage("Список лобби пуст");
                    }
                    String lobbiesJson = gson.toJson(lobbies);
                    dos.writeUTF(lobbiesJson);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Получает историю чата для определенного лобби и отправляет ее клиенту.
         *
         * @param lobbyModel LobbyModel с информацией о лобби и сообщениях.
         */
        private void getChat(LobbyModel lobbyModel) {
            try {
                if (lobbyModel.getMessages() != null && lobbyModel.getUsernameLobby() != null) {
                    List<MessageModel> chatHistory = userChatHistory.get(username)
                            .stream()
                            .filter(messageModel -> Objects.equals(lobbyModel.getUsernameLobby(), messageModel.getUsernameApponent()) || Objects.equals(lobbyModel.getUsernameLobby(), messageModel.getAuthor()))
                            .toList();
                    LobbyModel lobby = new LobbyModel(username, chatHistory);
                    dos.writeUTF(gson.toJson(lobby));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Отправляет сообщение указанному пользователю и обновляет историю чата для обоих пользователей.
         *
         * @param messageModel MessageModel с сообщением и информацией о получателе.
         */
        private void sendMessage(MessageModel messageModel) {
            try {
                if (messageModel.getMessage() != null && messageModel.getUsernameApponent() != null) {
                    messageModel.setAuthor(username);
                    userChatHistory.get(username).add(messageModel);
                    userChatHistory.get(messageModel.getUsernameApponent()).add(messageModel);
                    dos.writeUTF(gson.toJson(messageModel));

                    Socket apponentSocket = users.get(messageModel.getUsernameApponent());
                    if (apponentSocket.isConnected()) {
                        new DataOutputStream(apponentSocket.getOutputStream()).writeUTF(gson.toJson(messageModel));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Выходит из системы пользователя, удаляя его из списка активных пользователей.
         *
         * @param logoutModel LogoutModel с информацией о событии выхода из системы.
         */
        private void logoutUser(LogoutModel logoutModel) {
            if (logoutModel.getBar() != null) {
                users.remove(username);
            }
        }
    }

    /**
     * Основной метод для создания и запуска сервера на определенном порту.
     *
     * @param args Аргументы командной строки (не используются).
     */
    public static void main(String[] args) {
        logger.info("Сервер запущен.");

        Server server = new Server(12345);
        server.start();

    }
}
