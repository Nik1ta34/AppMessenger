module com.messenger.messenge {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;
    requires com.google.gson;

    opens com.messenger.messenge.models to com.google.gson, javafx.fxml;
    opens com.messenger.messenge to javafx.fxml;
    opens com.messenger.messenge.chat to javafx.fxml;
    opens com.messenger.messenge.lobbies to javafx.fxml;
    opens com.messenger.messenge.registration to javafx.fxml;
    exports com.messenger.messenge;
}