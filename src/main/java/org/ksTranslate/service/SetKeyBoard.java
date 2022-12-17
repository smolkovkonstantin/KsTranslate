package org.ksTranslate.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;


public interface SetKeyBoard {
    ReplyKeyboardMarkup createStarterBoard();

    ReplyKeyboardMarkup createStopBoard();
}
