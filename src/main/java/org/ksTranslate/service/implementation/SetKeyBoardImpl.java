package org.ksTranslate.service.implementation;

import lombok.AllArgsConstructor;
import org.ksTranslate.service.SetKeyBoard;
import org.ksTranslate.supportive.Command;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class SetKeyBoardImpl implements SetKeyBoard {

    private ReplyKeyboardMarkup replyKeyboardMarkup;
    private List<KeyboardRow> keyboardRows;

    public SetKeyBoardImpl() {
        this.replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.getResizeKeyboard();

        this.keyboardRows = new ArrayList<>();
    }

    @Override
    public ReplyKeyboardMarkup createStarterBoard() {
        keyboardRows.clear();

        keyboardRows.add(addButtonWihText(Command.HELP.text));
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
