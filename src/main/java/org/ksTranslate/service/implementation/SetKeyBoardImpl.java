package org.ksTranslate.service.implementation;

import lombok.AllArgsConstructor;
import org.ksTranslate.dao.CardDAO;
import org.ksTranslate.service.SetKeyBoard;
import org.ksTranslate.supportive.Command;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetKeyBoardImpl implements SetKeyBoard {

    private final ReplyKeyboardMarkup replyKeyboardMarkup;
    private final List<KeyboardRow> keyboardRows;
    private final CardDAO cardDAO;

    public SetKeyBoardImpl(CardDAO cardDAO) {
        this.cardDAO = cardDAO;
        this.replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.getResizeKeyboard();

        this.keyboardRows = new ArrayList<>();
    }

    @Override
    public ReplyKeyboardMarkup createStarterBoard() {
        keyboardRows.clear();

        keyboardRows.add(addButtonWihText(Command.HELP.text));
        keyboardRows.add(addButtonWihText(Command.CREATE_CARD.text));

        if (cardDAO.countAllRaws().isEmpty() || cardDAO.countAllRaws().get() > 0){
            keyboardRows.add(addButtonWihText(Command.ADD_WORD.text));
        }

        keyboardRows.add(addButtonWihText(Command.EN_TO_RU.text));
        keyboardRows.add(addButtonWihText(Command.RU_TO_EN.text));

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    @Override
    public ReplyKeyboardMarkup createStopBoard() {
        keyboardRows.clear();

        keyboardRows.add(addButtonWihText(Command.STOP_TRANSLATE.text));

        replyKeyboardMarkup.setKeyboard(keyboardRows);

        return replyKeyboardMarkup;
    }

    private KeyboardRow addButtonWihText(String text) {

        KeyboardRow keyboardRow = new KeyboardRow();

        KeyboardButton button =  new KeyboardButton(text);



        keyboardRow.add(button);

        return keyboardRow;
    }
}
