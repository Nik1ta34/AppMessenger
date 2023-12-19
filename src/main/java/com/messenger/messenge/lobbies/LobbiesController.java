package com.messenger.messenge.lobbies;

import com.messenger.messenge.Client;
import com.messenger.messenge.NavigationHandler;
import com.messenger.messenge.models.LobbyModel;
import com.messenger.messenge.registration.RegistrationController;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Set;

public class LobbiesController implements LobbiesHandler, ChangeListener<String> {
    private static final Logger logger = LogManager.getLogger(LobbiesController.class.getName());

    private NavigationHandler navigationHandler;
    private Client client;

    /**
     * Контроллер для управления взаимодействием между пользовательским интерфейсом и клиентской логикой
     * в контексте лобби мессенджера. Отвечает за обновление списка доступных лобби и обработку событий,
     * связанных с лобби, через объект клиента типа Client.
     *
     * @param client             Объект клиента (типа Client), предоставляющий методы для взаимодействия с сервером.
     * @param navigationHandler Обработчик навигации (типа NavigationHandler), используемый для переключения между
     *                           различными экранами пользовательского интерфейса.
     */
    public LobbiesController(Client client, NavigationHandler navigationHandler) {
        this.client = client;
        this.navigationHandler = navigationHandler;
    }

    @FXML
    private ListView lobbiesList;

    @FXML
    private Text chatErrorView;

    @FXML
    private Button refreshButton;

    /**
     * Инициализирует LobbiesController после установки JavaFX stage.
     *
     * @param stage JavaFX stage для представления лобби.
     */
    public void postInitialize(Stage stage) {
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/com/messenger/messenge/lobbies-controller-view.fxml"));
        loader.setController(this);

        try {
            Parent root = loader.load();
            Platform.runLater(() -> {
                stage.setScene(new Scene(root, 400, 300));
                stage.getIcons().add(new Image(LobbiesController.class.getResourceAsStream("/icon.png")));
                stage.show();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        client.getLobbies(this);
    }

    /**
     * Обрабатывает изменение выбранного лобби в списке лобби.
     * Удаляет обработчик лобби и открывает представление чата для выбранного лобби.
     *
     * @param observableValue Наблюдаемое значение.
     * @param s Старое значение.
     * @param t1 Новое значение (имя пользователя выбранного лобби).
     */
    @Override
    public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
        logger.info("Выбрано лобби: {}", t1);

        client.removeLobbiesHandler();
        navigationHandler.openChat(t1);
    }

    /**
     * Callback-метод, вызываемый при успешном получении лобби.
     * Заполняет список лобби доступными лобби.
     *
     * @param lobbies Набор доступных лобби.
     */
    @Override
    public void getLobbiesSuccess(Set<LobbyModel> lobbies) {
        logger.info("Получен список лобби: {}", lobbies);

        ObservableList<Object> observableList = FXCollections.observableArrayList();
        observableList.addAll(lobbies.stream().map(LobbyModel::getUsernameLobby).toList());

        lobbiesList.setItems(observableList);
        lobbiesList.getSelectionModel().selectedItemProperty().addListener(this);
    }

    /**
     * Callback-метод, вызываемый при ошибке получения лобби.
     *
     * @param e Сообщение об ошибке для отображения.
     */
    @Override
    public void getLobbiesError(String e) {
        chatErrorView.setText(e);
        chatErrorView.setVisible(true);
        refreshButton.setVisible(true);
    }

    /**
     * Обрабатывает событие нажатия кнопки 'Назад', выходит из системы пользователя и открывает представление регистрации.
     */
    @FXML
    public void onBack() {
        client.logoutUser();
        navigationHandler.openRegistration();
    }

    /**
     * Обрабатывает событие нажатия кнопки 'Обновить', запускает обновление доступных лобби.
     */
    @FXML
    public void refreshLobbies() {
        client.getLobbies(this);
        chatErrorView.setVisible(false);
        refreshButton.setVisible(false);
    }
}
