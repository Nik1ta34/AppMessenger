package com.messenger.messenge.registration;

public interface RegistrationHandler {

    /**
     * Вызывается при успешной регистрации пользователя.
     */
    public void success();

    /**
     * Вызывается при возникновении ошибки в процессе регистрации пользователя.
     *
     * @param error Сообщение об ошибке при регистрации.
     */
    public void error(String error);
}
