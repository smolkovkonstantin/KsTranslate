package org.ksTranslate.controller;

import lombok.extern.log4j.Log4j;
import org.ksTranslate.model.MyUpdate;
import org.ksTranslate.model.SequenceWords;
import org.ksTranslate.service.implementation.BotCommandImpl;
import org.ksTranslate.service.implementation.SetKeyBoardImpl;
import org.ksTranslate.supportive.BotStatus;
import org.springframework.stereotype.Component;

import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Log4j
public class UpdateController {

    private TelegramBot telegramBot;
    private final BotCommandImpl botCommand;
    private final SetKeyBoardImpl keyBoard;

    private final CopyOnWriteArrayList<SequenceWords> sequenceWords = new CopyOnWriteArrayList<>();

    public UpdateController(BotCommandImpl botCommand, SetKeyBoardImpl setKeyBoard) {
        this.botCommand = botCommand;
        this.keyBoard = setKeyBoard;
    }

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(MyUpdate update) {
        if (update != null) {
            if (update.getUpdate().hasMessage()) {
                processText(update);
            } else {
                if (!update.getUpdate().hasEditedMessage()) {
                    unsupportedMessage(update);
                }
            }
        }
    }

    private void processText(MyUpdate update) {
        if (telegramBot.getModeWork().equals(BotStatus.INITIAL_STATE) || update.isStop()) {
            chooseCommand(update);

        } else if (telegramBot.getModeWork().equals(BotStatus.CREATE_CARD)) {
            createCard(update);

        } else if (telegramBot.getModeWork().equals(BotStatus.REMOVE_CARD)) {
            removeCard(update);

        } else if (telegramBot.getModeWork().equals(BotStatus.ADDS_WORDS)) {
            addText(update);

        } else if (telegramBot.getModeWork().equals(BotStatus.LEARNING_MODE)) {
            chooseLearningCommand(update);

        } else if (telegramBot.getModeWork().equals(BotStatus.SHOW_ALL_WORDS)) {
            if (update.isStop()) {
                chooseCommand(update);
            } else {
                chooseCardCommand(update);
            }
        } else if (telegramBot.getModeWork().equals(BotStatus.TEACHER_MODE)) {
            if (update.isStop()) {
                chooseCommand(update);
            } else {
                findCardCommand(update);
            }

        } else if (telegramBot.getModeWork().equals(BotStatus.LINKED_LIST_MODE)) {
            SequenceWords currentSequenceWords = new SequenceWords(update.getUser());
            int index = sequenceWords.indexOf(currentSequenceWords);

            if (update.isGetReady()) {
                showNextWord(update, sequenceWords.get(index));

            } else if (update.isNext() || update.isPrevious()) {
                // передаю именно ту карточку, которую выбрал именно тот пользователь ранее
                showNextWord(update, sequenceWords.get(index));

            } else if (update.isTranslate()) {
                translateThisWord(update, sequenceWords.get(index));


            } else {
                invalidCommand(update);
            }
        } else {
            translate(update);
        }
    }

    // выбрана карточка по которой пользователь будет учить слова
    private void findCardCommand(MyUpdate update) {
        if (findCardByName(update.getText())) {
            telegramBot.setModeWork(BotStatus.LINKED_LIST_MODE);
            update.setBoard(keyBoard.showStartForLearnBoard());
            telegramBot.sendAnswerMessage(botCommand.cardWasFind(update));
            sequenceWords.add(new SequenceWords(update.getText(), update.getUser()));

        } else {
            update.setBoard(keyBoard.createLearningBoard());
            telegramBot.sendAnswerMessage(botCommand.cardWasNotFind(update));
            telegramBot.setModeWork(BotStatus.LEARNING_MODE);

        }
    }

    private boolean findCardByName(String text) { // пользователь ввёл название карточки
        // проверка на правильность ввода
        return botCommand.findCardByName(text).isPresent();
    }

    private void chooseLearningCommand(MyUpdate update) {
        if (update.isAddWord()) {
            addWordCommand(update);

        } else if (update.isRemoveCard()) {
            removeCardCommand(update);

        } else if (update.isCreateCard()) {
            createCardCommand(update);

        } else if (update.showAllCards()) {
            showAllCardsCommand(update);

        } else if (update.isStop()) {
            backCommand(update);

        } else if (update.isStartLearning()) {
            startLearningCommand(update);

        } else {
            invalidCommand(update);
        }
    }

    private void chooseCommand(MyUpdate update) {
        if (update.isStart()) {
            startCommand(update);

        } else if (update.isHelp()) {
            helpCommand(update);

        } else if (update.isRuToEnSwitch()) {
            telegramBot.setModeWork(BotStatus.TRANSLATE_RU_TO_EN);
            switchCommand(update);

        } else if (update.isEnToRuSwitch()) {
            telegramBot.setModeWork(BotStatus.TRANSLATE_EN_TO_RU);
            switchCommand(update);

        } else if (update.isStop()) {
            stopCommand(update);

        } else if (update.isLearningMode()) {
            learningModeCommand(update);

        } else {
            invalidCommand(update);
        }
    }

    // пользователь решил начать учить слова
    private void startLearningCommand(MyUpdate update) {
        telegramBot.setModeWork(BotStatus.TEACHER_MODE);

        update.setBoard(keyBoard.showAllCardsBoard(update));

        telegramBot.sendAnswerMessage(botCommand.startLearning(update));

        log.debug("START LEARNING: " + update.getUser());
    }

    // пользователь выбрал какую именно карточку он будет учить, бот показывает ему следующее (первое) слово
    private synchronized void showNextWord(MyUpdate update, SequenceWords sequenceWords) {

        if (update.isNext() || sequenceWords.isFirstWord()) {
            sequenceWords.setIdWord(sequenceWords.getIdWord() + 1);
        } else {
            sequenceWords.setIdWord(sequenceWords.getIdWord() - 1);
        }

        update.setBoard(keyBoard.showNextPreviousBoard(sequenceWords.getNameCard(), sequenceWords.getIdWord()));

        var message = botCommand.showWord(update, sequenceWords.getNameCard(), sequenceWords.getIdWord());

        telegramBot.sendAnswerMessage(message);

        log.debug("SHOW WORD:" + update.getUser() + " " + sequenceWords.getNameCard() + " " + sequenceWords.getIdWord());
    }


    private synchronized void translateThisWord(MyUpdate update, SequenceWords sequenceWords) {
        update.setBoard(keyBoard.showNextPreviousBoard(sequenceWords.getNameCard(), sequenceWords.getIdWord()));

        String word = botCommand.findByNameCardAndNumberOnCard(sequenceWords);

        telegramBot.sendAnswerMessage(botCommand.translateTextToRus(update, word));

        log.debug("SHOW WORD TRANSLATION:" + update.getUser() + " " + sequenceWords.getNameCard() + " " + sequenceWords.getIdWord());
    }

    private void chooseCardCommand(MyUpdate update) {
        // ожидаю, что пользователь введёт название своей карточки (выберет из списка)
        telegramBot.setModeWork(BotStatus.SHOW_ALL_WORDS); // ставлю этот режим, чтобы показать другие карточки или выйти
        telegramBot.sendAnswerMessage(botCommand.showAllWord(update));
        log.debug("SHOW ALL WORD:" + update.getUserName() + " in card " + update.getText() + " ");
    }

    private void showAllCardsCommand(MyUpdate update) {
        // ставлю на следующее сообщение пользователя учебный режим бота
        telegramBot.setModeWork(BotStatus.SHOW_ALL_WORDS);

        // показываю все доступные карточки
        telegramBot.sendAnswerMessage(
                botCommand.showAllCards(update)
        );
        log.debug("SHOW ALL CARDS: " + update.getUser() + " name card: " + update.getText());
    }

    private void learningModeCommand(MyUpdate update) {
        telegramBot.setModeWork(BotStatus.LEARNING_MODE);
        update.setBoard(keyBoard.createLearningBoard());
        telegramBot.sendAnswerMessage(
                botCommand.processTextWithBotStatus(update, telegramBot.getModeWork())
        );
        log.debug("BOT SWITCHED TO TRAINING MODE: " + update.getUser() + " name card: " + update.getText());
    }

    private void backCommand(MyUpdate update) {
        update.setBoard(keyBoard.createLearningBoard());
        telegramBot.sendAnswerMessage(
                botCommand.stop(update)
        );
    }

    // пользователь указал боту, что он хочет удалить какую-то карточку
    private void removeCardCommand(MyUpdate update) {
        telegramBot.setModeWork(BotStatus.REMOVE_CARD);
        telegramBot.sendAnswerMessage(botCommand.removeCard(update));
        log.debug("USER WANT TO DELETE SAME CARD: " + update.getUser() + " " + update.getText());
    }

    // пользователь указал боту, какую конкретно карточку он хочет удалить
    private void removeCard(MyUpdate update) {
        update.setBoard(keyBoard.showAllCardsBoard(update));
        telegramBot.sendAnswerMessage(
                botCommand.processTextWithBotStatus(update, telegramBot.getModeWork())
        );
        telegramBot.setModeWork(BotStatus.LEARNING_MODE);
        log.debug("DELETE CARD: " + update.getUser() + " name card: " + update.getText());
    }

    // создание карточки
    private void createCard(MyUpdate update) {
        update.setBoard(keyBoard.createStopBoard());
        telegramBot.sendAnswerMessage(
                botCommand.processTextWithBotStatus(update, telegramBot.getModeWork())
        );
        log.debug("CREATE CARD: " + update.getUser() + " name card: " + update.getText());
    }

    // пользователь указал боту, что он хочет ввести слово
    private void addWordCommand(MyUpdate update) {
        telegramBot.setModeWork(BotStatus.ADDS_WORDS);
        telegramBot.sendAnswerMessage(
                botCommand.getInstructionHowAddWords(update) // бот выдал указания
        );
        log.debug("ADD NEW WORD IN CARD" + update.getUser() + " " + update.getText());
    }

    // пользователь ввёл слово, которое он хочет добавить в карточку
    private void addText(MyUpdate update) {
        update.setBoard(keyBoard.createStopBoard());
        telegramBot.sendAnswerMessage(
                botCommand.processTextWithBotStatus(update, telegramBot.getModeWork())
        );
        log.debug("ADD TEXT: " + update.getUser() + " add text " + update.getText());
    }

    //
    private void translate(MyUpdate update) {
        update.setBoard(keyBoard.createStopBoard());
        telegramBot.sendAnswerMessage(
                botCommand.processTextWithBotStatus(update, telegramBot.getModeWork())
        );
        log.debug("TRANSLATE text: " + update.getUser() + " : " + update.getText());
    }

    private void createCardCommand(MyUpdate update) {
        update.setBoard(keyBoard.createStopBoard());
        telegramBot.sendAnswerMessage(botCommand.createCard(update));
        telegramBot.setModeWork(BotStatus.CREATE_CARD);
        log.debug("CREATE COMMAND:" + update.getUser() + " : " + update.getText());
    }

    private void switchCommand(MyUpdate update) {
        update.setBoard(keyBoard.createStopBoard());
        telegramBot.sendAnswerMessage(botCommand.switchMode(update));
        log.debug("SWITCH_MODE command: " + update.getUser() + " : " + update.getText());
    }

    private void startCommand(MyUpdate update) {
        update.setBoard(keyBoard.createStarterBoard());
        telegramBot.sendAnswerMessage(botCommand.start(update));
        log.debug("START command: " + update.getUser() + " : " + update.getText());
    }

    private void stopCommand(MyUpdate update) {
        update.setBoard(keyBoard.createStarterBoard());
        telegramBot.sendAnswerMessage(botCommand.stop(update));
        telegramBot.setModeWork(BotStatus.INITIAL_STATE);
        log.debug("STOP: " + update.getUser() + " : " + update.getText());
    }

    private void helpCommand(MyUpdate update) {
        update.setBoard(keyBoard.createStarterBoard());
        telegramBot.sendAnswerMessage(botCommand.help(update));
        log.debug("HELP command: " + update.getUser() + " : " + update.getText());
    }

    private void invalidCommand(MyUpdate update) {
        update.setBoard(keyBoard.createStarterBoard());
        telegramBot.setModeWork(BotStatus.INITIAL_STATE);
        telegramBot.sendAnswerMessage(botCommand.invalidCommand(update));
        log.debug("INVALID command: " + update.getUser() + " : " + update.getText());
    }

    private void unsupportedMessage(MyUpdate update) {
        update.setBoard(keyBoard.createStarterBoard());
        telegramBot.sendAnswerMessage(botCommand.invalidMessage(update));
        log.debug("UNSUPPORTED MESSAGE TYPE:" + update.getUser() + " : " + update.getText());
    }

}
