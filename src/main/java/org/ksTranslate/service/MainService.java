package org.ksTranslate.service;

import org.ksTranslate.model.MyUpdate;
import org.ksTranslate.supportive.BotStatus;

import java.util.List;

public interface MainService {
    String processStartMessage(MyUpdate update); // регистрация пользователя и приветствие

    String addTextToCard(MyUpdate update, String text, String nameCard); // добавить текст на карточку

    String registerCard(MyUpdate update); // добавить карточку

    List<String> getAllCards(MyUpdate update); // получить все карточки

    List<String> getAllWordsFromCard(MyUpdate update); // получить все слова с карточки её названию

    String removeCard(MyUpdate update); // удалить карточку по её названию

    BotStatus getBotStatus(MyUpdate update);

    void setBotStatus(BotStatus botStatus, MyUpdate update);
}
