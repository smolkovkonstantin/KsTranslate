package org.ksTranslate.service;

import org.ksTranslate.model.MyUpdate;

public interface MainService {
    String processStartMessage(MyUpdate update);

    String addTextToCard(String text, String nameCard);

    String createCard(MyUpdate update);
}
