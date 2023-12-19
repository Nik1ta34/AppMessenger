package com.messenger.messenge.models;

import java.io.Serializable;

public class BaseModel implements Serializable {
    private String errorMessage;

    /**
     * Устанавливает сообщение об ошибке для модели.
     *
     * @param errorMessage Сообщение об ошибке для установки.
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Получает сообщение об ошибке из модели.
     *
     * @return Сообщение об ошибке.
     */
    public String getErrorMessage() {
        return errorMessage;
    }
}
