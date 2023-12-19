package com.messenger.messenge;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.messenger.messenge.chat.ChatHandler;
import com.messenger.messenge.lobbies.LobbiesHandler;
import com.messenger.messenge.models.*;
import com.messenger.messenge.registration.RegistrationHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.Collections;

public class Client implements Serializable {
    private DataInputStream dis;
    private DataOutputStream dos;
    private ChatHandler chatHandler;
    private RegistrationHandler registrationHandler;
    private LobbiesHandler lobbiesHandler;
    private Thread thread;
    private Gson gson = new Gson().newBuilder().create();
    private static final Logger logger = LogManager.getLogger(Client.class.getName());

    /**
     * Конструктор для создания объекта Client с указанным адресом сервера и портом.
     *
     * @param serverAddress Адрес сервера для подключения.
     * @param serverPort    Порт сервера для подключения.
     */
    public Client(String serverAddress, int serverPort) {
        initializeClient(serverAddress, serverPort);
    }

    /**
     * Метод для инициализации клиента, устанавливающий соединение с сервером по указанному адресу и порту.
     * Инициализирует поток для получения сообщений от сервера и обновления состояния клиента в соответствии
     * с полученными данными.
     *
     * @param serverAddress Адрес сервера для подключения.
     * @param serverPort    Порт сервера для подключения.
     */
    private void initializeClient(String serverAddress, int serverPort) {
        try {
            Socket socket = new Socket(serverAddress, serverPort);
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());

            thread = new Thread(() -> {
                while (!thread.isInterrupted()) {
                    try {
                        String serverMessage = dis.readUTF();
                        if (!serverMessage.isEmpty()) {
                            updateState(serverMessage);
                        }
                    } catch (JsonSyntaxException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("IOException in Client constructor: " + e.getMessage());
        }
    }

    /**
     * Метод для обновления состояния клиента на основе полученного сообщения от сервера.
     * Распознает тип сообщения и вызывает соответствующие методы для обработки данных.
     *
     * @param serverMessage Сообщение от сервера в формате JSON.
     */
    private void updateState(String serverMessage) {;
        try {
            RegistrationModel registrationModel = gson.fromJson(serverMessage, RegistrationModel.class);
            updateRegistrationState(registrationModel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            LobbiesModel lobbiesModel = gson.fromJson(serverMessage, LobbiesModel.class);
            updateLobbiesState(lobbiesModel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            LobbyModel lobbyModel = gson.fromJson(serverMessage, LobbyModel.class);
            updateChatHistoryState(lobbyModel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            MessageModel messageModel = gson.fromJson(serverMessage, MessageModel.class);
            updateSendingMessageState(messageModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод для обновления состояния клиента на основе объекта RegistrationModel.
     * Вызывает соответствующие методы обработчика регистрации.
     *
     * @param registrationModel Объект RegistrationModel с информацией о регистрации.
     */
    private void updateRegistrationState(RegistrationModel registrationModel) {
        if (registrationModel.getUsername() != null && registrationModel.getPassword() != null) {
            if (registrationModel.getErrorMessage() == null) {
                registrationHandler.success();
            } else {
                registrationHandler.error(registrationModel.getErrorMessage());
            }
        }
    }

    /**
     * Метод для обновления состояния клиента на основе объекта LobbiesModel.
     * Вызывает соответствующие методы обработчика лобби.
     *
     * @param lobbiesModel Объект LobbiesModel с информацией о доступных лобби.
     */
    private void updateLobbiesState(LobbiesModel lobbiesModel) {
        if (lobbiesModel.getLobbies() != null) {
            if (lobbiesModel.getErrorMessage() == null) {
                lobbiesHandler.getLobbiesSuccess(lobbiesModel.getLobbies());
            } else {
                lobbiesHandler.getLobbiesError(lobbiesModel.getErrorMessage());
            }
        }
    }

    /**
     * Метод для обновления состояния клиента на основе объекта LobbyModel.
     * Вызывает соответствующие методы обработчика истории чата лобби.
     *
     * @param lobbyModel Объект LobbyModel с информацией о чате лобби.
     */
    private void updateChatHistoryState(LobbyModel lobbyModel) {
        if (lobbyModel.getMessages() != null && lobbyModel.getUsernameLobby() != null) {
            if (lobbyModel.getErrorMessage() == null) {
                chatHandler.getMessagesSuccess(lobbyModel.getMessages());
            } else {
                chatHandler.error(lobbyModel.getErrorMessage());
            }
        }
    }

    /**
     * Метод для обновления состояния клиента на основе объекта MessageModel.
     * Вызывает соответствующие методы обработчика отправки сообщения в чат.
     *
     * @param messageModel Объект MessageModel с информацией о сообщении.
     */
    private void updateSendingMessageState(MessageModel messageModel) {
        if (messageModel.getMessage() != null && messageModel.getUsernameApponent() != null) {
            if (messageModel.getErrorMessage() == null) {
                chatHandler.sendMessageSuccess(messageModel);
            } else {
                chatHandler.error(messageModel.getErrorMessage());
            }
        }
    }

    /**
     * Отправляет сообщение в чат оппоненту.
     *
     * @param message          Текст сообщения для отправки.
     * @param usernameApponent Имя оппонента, кому адресовано сообщение.
     */
    public void sendMessage(String message, String usernameApponent) {
        if (message.trim().isEmpty()) {
            return;
        }

        MessageModel messageModel = new MessageModel(message, usernameApponent);
        try {
            dos.writeUTF(gson.toJson(messageModel));
        } catch (IOException e) {
            if (chatHandler != null) {
                chatHandler.error(e.getMessage());
            }
        }
    }

    /**
     * Регистрирует нового пользователя на сервере.
     *
     * @param username             Имя пользователя для регистрации.
     * @param password             Пароль для регистрации.
     * @param registrationHandler Обработчик событий регистрации.
     */
    public void register(String username, String password, RegistrationHandler registrationHandler) {
        logger.info("Попытка регистрации пользователя: {}", username);
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            return;
        }

        this.registrationHandler = registrationHandler;

        try {
            RegistrationModel registrationModel = new RegistrationModel(username, password);
            dos.writeUTF(gson.toJson(registrationModel));

        } catch (IOException e) {
            registrationHandler.error(e.getMessage());
            System.err.println("IOException в Client: " + e.getMessage());
        }
    }

    /**
     * Получает список доступных лобби с сервера.
     *
     * @param lobbiesHandler Обработчик событий лобби.
     */
    public void getLobbies(LobbiesHandler lobbiesHandler) {
        logger.info("Запрос списка лобби.");
        this.lobbiesHandler = lobbiesHandler;
        try {
            dos.writeUTF(gson.toJson(new LobbiesModel(Collections.emptySet())));
        } catch (IOException e) {
            lobbiesHandler.getLobbiesError(e.getMessage());
        }
    }

    /**
     * Получает историю чата для указанного лобби с сервера.
     *
     * @param usernameLobby Имя лобби для получения истории чата.
     * @param chatHandler   Обработчик событий чата.
     */
    public void getChatHistory(String usernameLobby, ChatHandler chatHandler) {
        this.chatHandler = chatHandler;
        try {
            dos.writeUTF(gson.toJson(new LobbyModel(usernameLobby, Collections.emptyList())));
        } catch (IOException e) {
            chatHandler.error(e.getMessage());
        }
    }

    /**
     * Удаляет обработчик событий регистрации пользователя.
     */
    public void removeRegistrationHandler() {
        this.registrationHandler = null;
    }

    /**
     * Удаляет обработчик событий лобби.
     */
    public void removeLobbiesHandler() {
        this.lobbiesHandler = null;
    }

    /**
     * Удаляет обработчик событий чата.
     */
    public void removeChatHandler() {
        this.chatHandler = null;
    }

    /**
     * Выходит из учетной записи пользователя, отправляя запрос на сервер.
     */
    public void logoutUser() {
        try {
            dos.writeUTF(gson.toJson(new LogoutModel()));
            removeRegistrationHandler();
            removeLobbiesHandler();
            removeChatHandler();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
