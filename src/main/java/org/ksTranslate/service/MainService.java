package org.ksTranslate.service;

import org.ksTranslate.model.MyUpdate;

import java.util.List;

public interface MainService {
    String processStartMessage(MyUpdate update);

    String addTextToCard(String text, String nameCard);

    String registerCard(MyUpdate update);

    List<String> getAllCards();

    List<String> getAllWordsFromCard(String text);

    String removeCard(MyUpdate update);

    String showWordFromCard(String nameCard, int idWord);
}
