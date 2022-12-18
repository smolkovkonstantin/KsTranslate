package org.ksTranslate.supportive;

public enum Command {
    STOP("\u274C"),
    EN_TO_RU("\uD83C\uDDEC\uD83C\uDDE7 \u2192 \uD83C\uDDF7\uD83C\uDDFA"),
    RU_TO_EN("\uD83C\uDDF7\uD83C\uDDFA \u2192 \uD83C\uDDEC\uD83C\uDDE7"),
    HELP("\u2753"),
    START("/start"),

    ADD_WORD("\uD83D\uDCCC"),

    CREATE_CARD("\uD83D\uDCDD"),

    REMOVE_CARD("\uD83D\uDDD1 \uD83D\uDCDD"),

    LEARNING_MODE("Режим обучения"),

    SHOW_ALL_CARDS("\uD83D\uDCC1"),

    START_LEARNING("\uD83D\uDCDA");

    public final String text;

    Command(String text) {
        this.text = text;
    }
}
