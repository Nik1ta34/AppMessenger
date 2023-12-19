package com.messenger.messenge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Launch{
    private static final Logger logger = LogManager.getLogger(Launch.class.getName());

    /**
     * Точка входа приложения. Запускает метод main класса GraphClient.
     *
     * @param args Аргументы командной строки.
     */
    public static void main(String[] args) {
        logger.info("Приложение запущено.");

        GraphClient.main(args);
    }
}
