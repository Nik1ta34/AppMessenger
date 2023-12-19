package com.messenger.messenge.lobbies;

import com.messenger.messenge.models.LobbyModel;

import java.util.Set;

public interface LobbiesHandler {

    /**
     * Вызывается при успешном получении списка доступных лобби.
     *
     * @param lobbies Набор объектов LobbyModel, представляющих доступные лобби.
     */
    public void getLobbiesSuccess(Set<LobbyModel> lobbies);

    /**
     * Вызывается при возникновении ошибки в операциях, связанных с лобби.
     *
     * @param e Строка с описанием ошибки.
     */
    public void getLobbiesError(String e);

}
