package com.messenger.messenge.models;

import java.io.Serializable;

public class RegistrationModel extends BaseModel implements Serializable {

    private String username;
    private String password;

    /**
     * Конструирует объект RegistrationModel с указанным именем пользователя и паролем.
     *
     * @param username Имя пользователя для регистрации.
     * @param password Пароль для регистрации.
     */
    public RegistrationModel(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Получает имя пользователя.
     *
     * @return Имя пользователя.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Получает пароль.
     *
     * @return Пароль.
     */
    public String getPassword() {
        return password;
    }
}
