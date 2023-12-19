package com.messenger.messenge.registration;

import com.messenger.messenge.Client;
import com.messenger.messenge.NavigationHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class RegistrationController implements RegistrationHandler {

    private NavigationHandler navigationHandler;
    private Client client;
    private static final Logger logger = LogManager.getLogger(RegistrationController.class.getName());

    /**
     * Контроллер для управления взаимодействием между пользовательским интерфейсом и клиентской логикой
     * в контексте регистрации в мессенджере. Отвечает за обработку ввода пользователя, вызов методов
     * клиента для отправки запросов на сервер и обработку результатов операции.
     *
     * @param client             Объект клиента (типа Client), предоставляющий методы для взаимодействия с сервером.
     * @param navigationHandler Обработчик навигации (типа NavigationHandler), используемый для переключения между
     *                           различными экранами пользовательского интерфейса.
     */
    public RegistrationController(Client client, NavigationHandler navigationHandler) {
        this.client = client;
        this.navigationHandler = navigationHandler;
    }

    /**
     * Инициализирует RegistrationController после установки JavaFX stage.
     *
     * @param stage JavaFX stage для представления регистрации.
     */
    public void postInitialization(Stage stage) {
        logger.info("Начало процесса регистрации.");

        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/com/messenger/messenge/registration-controller-view.fxml"));
        loader.setController(this);
        try {
            Parent root = loader.load();
            stage.setScene(new Scene(root, 400, 300));
            stage.getIcons().add(new Image(RegistrationController.class.getResourceAsStream("/icon.png")));
            stage.show();
        } catch (IOException e) {
            logger.error("Ошибка при открытии представления регистрации: " + e.getMessage(), e);
        }
    }

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Text registrationErrorView;

    @FXML
    public void initialize() {
        logger.info("Контроллер инициализирован");
    }

    /**
     * Обрабатывает событие нажатия кнопки регистрации, запуская процесс регистрации клиента.
     */
    @FXML
    public void registrationButtonHandler() {
        logger.info("Нажата кнопка регистрации.");

        if (client != null) {
            registrationErrorView.setVisible(false);
            client.register(usernameField.getText(), passwordField.getText(), this);
            logger.info("Регистрация пользователя: {}", usernameField.getText());
        } else {
            logger.error("Client is null");
            error("Client is null");
        }
    }

    /**
     * Callback-метод, вызываемый при успешной регистрации.
     * Удаляет обработчик регистрации и открывает представление лобби.
     */
    @Override
    public void success() {
        logger.info("Регистрация прошла успешно.");

        client.removeRegistrationHandler();
        navigationHandler.openLobbies();
    }

    /**
     * Callback-метод, вызываемый при ошибке регистрации.
     *
     * @param e Сообщение об ошибке для отображения.
     */
    @Override
    public void error(String e) {
        registrationErrorView.setText(e);
        registrationErrorView.setVisible(true);
    }
}
