package com.messenger.messenge;

import com.messenger.messenge.chat.ChatController;
import com.messenger.messenge.lobbies.LobbiesController;
import com.messenger.messenge.registration.RegistrationController;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GraphClient extends Application implements NavigationHandler {

    static final Logger logger = LogManager.getLogger(GraphClient.class.getName());
    private Client client;
    private Stage stage;

    /**
     * Точка входа для запуска JavaFX-приложения.
     *
     * @param args Параметры командной строки.
     */
    public static void main(String[] args) {
        logger.info("Запуск GraphClient.");

        launch(args);
    }

    /**
     * Метод, вызываемый при запуске приложения. Открывает экран регистрации.
     *
     * @param primaryStage Основная сцена приложения.
     * @throws Exception В случае возникновения ошибок при запуске приложения.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;

        logger.info("Приложение запущено.");

        openRegistration();
    }

    /**
     * Метод, вызываемый при завершении приложения. Оповещает сервер о выходе пользователя.
     *
     * @throws Exception В случае возникновения ошибок при завершении приложения.
     */
    @Override
    public void stop() throws Exception {// Оповестить сервер о выходе
        logger.info("Приложение завершено.");

        client.logoutUser();
        super.stop();
    }

    /**
     * Открывает экран регистрации, инициализирует контроллер регистрации.
     */
    @Override
    public void openRegistration() {
        registerSocket();
        RegistrationController controller = new RegistrationController(client, this);
        controller.postInitialization(stage);
    }

    /**
     * Открывает экран лобби, инициализирует контроллер лобби.
     */
    @Override
    public void openLobbies() {
        LobbiesController lobbiesController = new LobbiesController(client, this);
        lobbiesController.postInitialize(stage);
    }

    /**
     * Открывает экран чата с указанным оппонентом, инициализирует контроллер чата.
     *
     * @param usernameApponent Имя оппонента для чата.
     */
    @Override
    public void openChat(String usernameApponent) {
        ChatController chatController = new ChatController();
        chatController.postInitialize(usernameApponent, client, stage, this);
    }

    /**
     * Регистрирует соединение с сервером при запуске клиента.
     */
    private void registerSocket() {
        try {
            client = new Client("127.0.0.1", 12345);

            logger.info("Сокет успешно зарегистрирован.");
        } catch (Exception e) {
            logger.error("Ошибка при запуске GraphClient: " + e.getMessage(), e);
        }
    }
}
