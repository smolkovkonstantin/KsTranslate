package org.ksTranslate.supportive;

public enum Command {
    STOP_TRANSLATE("\u274C"),
    EN_TO_RU("\uD83C\uDDEC\uD83C\uDDE7 \u2192 \uD83C\uDDF7\uD83C\uDDFA"),
    RU_TO_EN("\uD83C\uDDF7\uD83C\uDDFA \u2192 \uD83C\uDDEC\uD83C\uDDE7"),
    HELP("\u2753"),
    START("/start"),
    ;

    public final String text;

    Command(String text) {
        this.text = text;
    }
}
