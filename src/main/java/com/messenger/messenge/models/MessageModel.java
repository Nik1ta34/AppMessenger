package com.messenger.messenge.models;

import java.io.Serializable;

public class MessageModel extends BaseModel implements Serializable {

    private String message;
    private String author;
    private String usernameApponent;

    /**
     * Конструктор для создания объекта MessageModel с указанным текстом сообщения и именем оппонента.
     *
     * @param message          Текст сообщения.
     * @param usernameApponent Имя оппонента, для которого предназначено сообщение.
     */
    public MessageModel(String message, String usernameApponent) {
        this.message = message;
        this.usernameApponent = usernameApponent;
    }

    /**
     * Получает текст сообщения.
     *
     * @return Текст сообщения.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Получает имя оппонента, для которого предназначено сообщение.
     *
     * @return Имя оппонента.
     */
    public String getUsernameApponent() {
        return usernameApponent;
    }

    /**
     * Получает имя автора сообщения.
     *
     * @return Имя автора сообщения.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Устанавливает имя автора сообщения.
     *
     * @param author Имя автора сообщения.
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Получает полное отформатированное сообщение, включая имя автора.
     *
     * @return Полное отформатированное сообщение.
     */
    public String getFullMessage() {
        String fullMessage = author + ">: " + getMessage();

        return fullMessage;
    }
}
