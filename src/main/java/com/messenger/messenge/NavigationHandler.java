package com.messenger.messenge;

public interface NavigationHandler {

    /**
     * Метод для открытия экрана регистрации.
     */
    void openRegistration();

    /**
     * Метод для открытия экрана списка лобби.
     */
    void openLobbies();

    /**
     * Метод для открытия экрана чата с определенным собеседником.
     *
     * @param usernameApponent Имя собеседника, с которым открывается чат.
     */
    void openChat(String usernameApponent);

}
