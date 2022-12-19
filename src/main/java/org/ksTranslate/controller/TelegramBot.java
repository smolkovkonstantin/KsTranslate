package org.ksTranslate.controller;

import lombok.extern.log4j.Log4j;
import org.ksTranslate.model.MyUpdate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

/**
 * Класс описывает создание бота, прописывается botName и botToken, чтобы можно было подключиться к боту в ТГ
 */

@Component
@Log4j
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${bot.token}")
    private String botToken;

    @Value("${bot.name}")
    private String botName;

    private final UpdateController updateController;


    public TelegramBot(UpdateController updateController) {
        this.updateController = updateController;
    }

    @PostConstruct
    public void init() {
        updateController.registerBot(this);
    }

    @Override
    public void onUpdateReceived(Update update) {

        MyUpdate myUpdate = new MyUpdate(update);

        updateController.processUpdate(myUpdate);
    }

    public void sendAnswerMessage(SendMessage sendmessage) {
        try {
            execute(sendmessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
