package com.messenger.messenge.chat;

import com.messenger.messenge.models.MessageModel;

import java.util.List;

public interface ChatHandler {

    /**
     * Вызывается при успешном получении списка сообщений чата.
     *
     * @param messages Список объектов MessageModel, представляющих сообщения чата.
     */
    void getMessagesSuccess(List<MessageModel> messages);

    /**
     * Вызывается при успешной отправке сообщения в чат.
     *
     * @param message Объект MessageModel, представляющий отправленное сообщение.
     */
    void sendMessageSuccess(MessageModel message);

    /**
     * Вызывается при возникновении ошибки в операциях, связанных с чатом.
     *
     * @param e Строка с описанием ошибки.
     */
    void error(String e);

}
