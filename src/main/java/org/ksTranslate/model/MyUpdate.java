package org.ksTranslate.model;

import org.ksTranslate.supportive.Command;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
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
        return getText().equals(Command.STOP.text);
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

    public boolean isAddWord() {
        return getText().equals(Command.ADD_WORD.text);
    }

    public String getUserName() {
        return getMessage().getFrom().getUserName();
    }

    public Long getChatId() {
        return getMessage().getChatId();
    }

    public boolean isCreateCard() {
        return getText().equals(Command.CREATE_CARD.text);
    }

    public boolean isLearningMode() {
        return getText().equals(Command.LEARNING_MODE.text);
    }

    public boolean showAllCards() {
        return getText().equals(Command.SHOW_ALL_CARDS.text);
    }

    public boolean isStartLearning() {
        return getText().equals(Command.START_LEARNING.text);
    }

    public boolean isRemoveCard() {
        return getText().equals(Command.REMOVE_CARD.text);
    }

    public User getUser(){
        return getMessage().getFrom();
    }

    public boolean isNext() {
        return getText().equals(Command.NEXT.text);
    }

    public boolean isPrevious() {
        return getText().equals(Command.PREVIOUS.text);
    }

    public boolean isGetReady() {
        return getText().equals(Command.GET_READY.text);
    }

    public boolean isTranslate() {
        return getText().equals(Command.TRANSLATE.text);
    }
}
