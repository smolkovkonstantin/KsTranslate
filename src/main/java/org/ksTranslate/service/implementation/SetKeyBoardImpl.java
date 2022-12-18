package org.ksTranslate.service.implementation;

import org.ksTranslate.dao.TextDAO;
import org.ksTranslate.model.MyUpdate;
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

    private final MainServiceImpl mainService;
    private final TextDAO textDAO;

    public SetKeyBoardImpl(MainServiceImpl mainService,
                           TextDAO textDAO) {
        this.mainService = mainService;
        this.replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.getResizeKeyboard();

        this.keyboardRows = new ArrayList<>();
        this.textDAO = textDAO;
    }

    @Override
    public ReplyKeyboardMarkup createStarterBoard() {
        keyboardRows.clear();

        keyboardRows.add(addButtonWihText(Command.HELP.text));
        keyboardRows.add(addButtonWihText(Command.LEARNING_MODE.text));
        keyboardRows.add(addButtonWihText(Command.EN_TO_RU.text));
        keyboardRows.add(addButtonWihText(Command.RU_TO_EN.text));

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    @Override
    public ReplyKeyboardMarkup createStopBoard() {
        keyboardRows.clear();

        keyboardRows.add(addButtonWihText(Command.STOP.text));

        replyKeyboardMarkup.setKeyboard(keyboardRows);

        return replyKeyboardMarkup;
    }

    @Override
    public ReplyKeyboardMarkup createLearningBoard() {
        keyboardRows.clear();

        keyboardRows.add(addButtonWihText(Command.CREATE_CARD.text));
        if (mainService.getCardDAO().countCards().isEmpty() || mainService.getCardDAO().countCards().get() > 0) {
            keyboardRows.add(addButtonWihText(Command.REMOVE_CARD.text));
            keyboardRows.add(addButtonWihText(Command.ADD_WORD.text));
            keyboardRows.add(addButtonWihText(Command.START_LEARNING.text));
        }
        keyboardRows.add(addButtonWihText(Command.SHOW_ALL_CARDS.text));
        keyboardRows.add(addButtonWihText(Command.STOP.text));

        replyKeyboardMarkup.setKeyboard(keyboardRows);

        return replyKeyboardMarkup;
    }

    @Override
    public ReplyKeyboardMarkup showAllCardsBoard(MyUpdate update) {

        List<String> nameCards = mainService.getAllCards();

        keyboardRows.clear();

        nameCards.forEach(nameCard -> keyboardRows.add(addButtonWihText(nameCard)));

        if (nameCards.size() == 0) {
            return null;
        }

        keyboardRows.add(addButtonWihText(Command.STOP.text));
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup showNextPreviousBoard() {
        keyboardRows.clear();

        keyboardRows.add(addButtonWihText(Command.NEXT.text));

        keyboardRows.add(addButtonWihText(Command.STOP.text));
        replyKeyboardMarkup.setKeyboard(keyboardRows);

        return replyKeyboardMarkup;
    }

    @Override
    public ReplyKeyboardMarkup showAllText(MyUpdate update) {
        keyboardRows.clear();
        List<String> texts = textDAO.findAllByNameCard(update.getText());

        texts.forEach(nameCard -> keyboardRows.add(addButtonWihText(nameCard)));

        if (texts.size() == 0) {
            return null;
        }

        keyboardRows.add(addButtonWihText(Command.STOP.text));
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    private KeyboardRow addButtonWihText(String text) {

        KeyboardRow keyboardRow = new KeyboardRow();

        KeyboardButton button = new KeyboardButton(text);

        keyboardRow.add(button);

        return keyboardRow;
    }
}
