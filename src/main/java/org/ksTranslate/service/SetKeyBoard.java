package org.ksTranslate.service;

import org.ksTranslate.model.MyUpdate;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;


public interface SetKeyBoard {
    ReplyKeyboardMarkup createStarterBoard();

    ReplyKeyboardMarkup createStopBoard();

    ReplyKeyboardMarkup createLearningBoard();

    ReplyKeyboardMarkup showAllCardsBoard(MyUpdate update);

    ReplyKeyboardMarkup showAllText(MyUpdate update);
}
