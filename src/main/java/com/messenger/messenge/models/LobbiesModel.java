package com.messenger.messenge.models;

import java.io.Serializable;
import java.util.Set;

public class LobbiesModel extends BaseModel implements Serializable {
    private Set<LobbyModel> lobbies;

    /**
     * Получает набор лобби.
     *
     * @return Набор лобби.
     */
    public LobbiesModel(Set<LobbyModel> lobbies) {
        this.lobbies = lobbies;
    }

    /**
     * Возвращает множество объектов LobbyModel, представляющих текущий список доступных лобби.
     *
     * @return Множество объектов LobbyModel, представляющих доступные лобби.
     */
    public Set<LobbyModel> getLobbies() {
        return lobbies;
    }
}
