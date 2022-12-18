package org.ksTranslate.service;

import org.ksTranslate.model.MyUpdate;

import java.util.List;

public interface MainService {
    String processStartMessage(MyUpdate update); // регистрация пользователя и приветствие

    String addTextToCard(String text, String nameCard); // добавить текст на карточку

    String registerCard(MyUpdate update); // добавить карточку

    List<String> getAllCards(); // получить все карточки

    List<String> getAllWordsFromCard(String text); // получить все слова с карточки её названию

    String removeCard(MyUpdate update); // удалить карточку по её названию
}
