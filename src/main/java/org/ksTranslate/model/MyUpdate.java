package org.ksTranslate.model;

import org.ksTranslate.supportive.Command;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public class MyUpdate {
    private final Update update;
    private ReplyKeyboardMarkup replyKeyboardMarkup;

    public MyUpdate(Update update) {
        this.update = update;
    }

    public Message getMessage() {
        return update.getMessage();
    }

    public String getText() {
        return update.getMessage().getText();
    }

    public boolean isStart() {
        return getText().equals(Command.START.text);
    }

    public boolean isHelp() {
        return getText().equals(Command.HELP.text);
    }

    public boolean isStop() {
        return getText().equals(Command.STOP_TRANSLATE.text);
    }

    public boolean isRuToEnSwitch() {
        return getText().equals(Command.RU_TO_EN.text);
    }

    public boolean isEnToRuSwitch() {
        return getText().equals(Command.EN_TO_RU.text);
    }

    public ReplyKeyboardMarkup getBoard() {
        return replyKeyboardMarkup;
    }

    public void setBoard(ReplyKeyboardMarkup replyKeyboardMarkup) {
        this.replyKeyboardMarkup = replyKeyboardMarkup;
    }

    public Update getUpdate() {
        return update;
    }
}
