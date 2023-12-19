package com.messenger.messenge.models;

import java.io.Serializable;
import java.util.List;

public class LobbyModel extends BaseModel implements Serializable {
    private String usernameLobby;
    private List<MessageModel> messages;

    /**
     * Конструктор для создания объекта LobbyModel с указанным именем лобби и списком сообщений чата в этом лобби.
     *
     * @param usernameLobby Имя лобби.
     * @param messages      Список объектов MessageModel, представляющих сообщения в чате лобби.
     */
    public LobbyModel(String usernameLobby, List<MessageModel> messages) {
        this.usernameLobby = usernameLobby;
        this.messages = messages;
    }

    /**
     * Получает имя пользователя, связанное с лобби.
     *
     * @return Имя пользователя, связанное с лобби.
     */
    public String getUsernameLobby() {
        return usernameLobby;
    }

    /**
     * Получает список сообщений, связанных с лобби.
     *
     * @return Список сообщений, связанных с лобби.
     */
    public List<MessageModel> getMessages() {
        return messages;
    }
}
