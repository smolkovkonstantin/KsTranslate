package org.ksTranslate.supportive;

public enum Command {
    STOP("\u274C"),
    EN_TO_RU("\uD83C\uDDEC\uD83C\uDDE7 \u2192 \uD83C\uDDF7\uD83C\uDDFA"),
    RU_TO_EN("\uD83C\uDDF7\uD83C\uDDFA \u2192 \uD83C\uDDEC\uD83C\uDDE7"),
    HELP("\u2753"),
    START("/start"),

    ADD_WORD("добавить слово в карточку"),

    CREATE_CARD("создать новую карточку"),

    REMOVE_CARD("удалить карточку"),

    LEARNING_MODE("режим обучения"),

    SHOW_ALL_CARDS("показать все карточки"),

    START_LEARNING("начать учить"),

    NEXT("Следующее слово"),

    PREVIOUS("Предыдущее слово"),

    GET_READY("Начать"),

    TRANSLATE("Перевести");

    public final String text;

    Command(String text) {
        this.text = text;
    }
}
