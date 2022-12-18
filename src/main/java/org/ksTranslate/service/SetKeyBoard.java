package org.ksTranslate.service;

import org.ksTranslate.model.MyUpdate;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;


public interface SetKeyBoard {
    ReplyKeyboardMarkup createStarterBoard(); // создаётся board со стартовыми указаниями

    ReplyKeyboardMarkup createStopBoard(); // создаётся board с кнопкой выхода

    ReplyKeyboardMarkup createLearningBoard(MyUpdate update); // создаётся board с кнопками для режима обучения

    ReplyKeyboardMarkup showAllCardsBoard(MyUpdate update); // создаётся board со всеми карточками

    ReplyKeyboardMarkup showAllText(MyUpdate update); // создаётся board со фразами (словами)
}
