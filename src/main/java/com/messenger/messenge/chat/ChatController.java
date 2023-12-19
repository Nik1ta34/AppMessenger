package com.messenger.messenge.chat;

import com.messenger.messenge.Client;
import com.messenger.messenge.NavigationHandler;
import com.messenger.messenge.models.MessageModel;
import com.messenger.messenge.registration.RegistrationController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

public class ChatController implements ChatHandler {

    private NavigationHandler navigationHandler;
    private Client client;
    private ObservableList observableList = FXCollections.observableArrayList();
    private String usernameApponent;
    @FXML
    private Text usernameApponentView;
    @FXML
    private TextField messageView;
    @FXML
    private ListView messagesView;
    @FXML
    private Text chatErrorView;
    private static final Logger logger = LogManager.getLogger(ChatController.class.getName());

    /**
     * Инициализирует ChatController после установки JavaFX stage.
     *
     * @param usernameApponent Имя пользователя для чата.
     * @param client Экземпляр клиента.
     * @param stage JavaFX stage для представления чата.
     * @param navigationHandler Обработчик навигации.
     */
    public void postInitialize(String usernameApponent, Client client, Stage stage, NavigationHandler navigationHandler) {
        logger.info("Открытие чата с пользователем: {}", usernameApponent);

        this.usernameApponent = usernameApponent;
        this.client = client;
        this.navigationHandler = navigationHandler;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/messenger/messenge/chat-controller-view.fxml"));
        loader.setController(this);
        try {
            Parent root = loader.load();
            Platform.runLater(() -> {
                stage.setScene(new Scene(root, 400, 300));
                stage.getIcons().add(new Image(ChatController.class.getResourceAsStream("/icon.png")));
                stage.show();
            });
        } catch (IOException e) {
            logger.error("Ошибка при открытии чата: " + e.getMessage(), e);
        }

        client.getChatHistory(usernameApponent, this);
    }

    /**
     * Обрабатывает событие нажатия кнопки 'Назад', удаляет обработчик чата и открывает представление лобби.
     */
    public void back() {
        client.removeChatHandler();
        navigationHandler.openLobbies();
    }

    /**
     * Обрабатывает событие нажатия кнопки 'Отправить', отправляет введенное сообщение оппоненту.
     * Очищает поле ввода сообщения.
     */
    public void sendMessage() {
        client.sendMessage(messageView.getText(), usernameApponent);
        messageView.setText("");
        chatErrorView.setVisible(false);
    }

    /**
     * Callback-метод, вызываемый при успешном получении истории чата.
     * Заполняет список сообщений чата полученными сообщениями.
     *
     * @param messages Список сообщений в истории чата.
     */
    @Override
    public void getMessagesSuccess(List<MessageModel> messages) {
        observableList.addAll(messages.stream().map(MessageModel::getFullMessage).toList());

        Platform.runLater(() -> {
            usernameApponentView.setText("Chat with user: " + usernameApponent);
            messagesView.setItems(observableList);
            messagesView.scrollTo(observableList.size());
        });
    }

    /**
     * Callback-метод, вызываемый при успешной отправке сообщения.
     * Добавляет новое сообщение в список сообщений чата.
     *
     * @param message Сообщение, отправленное в чат.
     */
    @Override
    public void sendMessageSuccess(MessageModel message) {
        Platform.runLater(() -> {
            observableList.add(message.getFullMessage());
            messagesView.scrollTo(observableList.size());
        });
    }

    /**
     * Callback-метод, вызываемый при ошибке.
     *
     * @param e Сообщение об ошибке для отображения.
     */
    @Override
    public void error(String e) {
        chatErrorView.setText(e);
        chatErrorView.setVisible(true);
    }
}
