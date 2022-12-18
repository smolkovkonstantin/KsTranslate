package org.ksTranslate.controller;

import lombok.extern.log4j.Log4j;
import org.ksTranslate.model.MyUpdate;
import org.ksTranslate.service.implementation.BotCommandImpl;
import org.ksTranslate.service.implementation.SetKeyBoardImpl;
import org.ksTranslate.supportive.BotStatus;
import org.springframework.stereotype.Component;

/**
 * Класс описывает всё взаимодействие между пользователем и ботом
 */

@Component
@Log4j
public class UpdateController {

    private TelegramBot telegramBot;
    private final BotCommandImpl botCommand;
    private final SetKeyBoardImpl keyBoard;

    public UpdateController(BotCommandImpl botCommand, SetKeyBoardImpl setKeyBoard) {
        this.botCommand = botCommand;
        this.keyBoard = setKeyBoard;
    }

    public synchronized void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    // метод проверяет, какое сообщение бот получил и обрабатывает только текстовое
    // если сообщение "непонятного" типа, то пользователю пишется об этом
    public synchronized void processUpdate(MyUpdate update) {
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

    private synchronized void processText(MyUpdate update) {
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
        } else if (telegramBot.getModeWork().equals(BotStatus.READY_TO_TRANSLATE)) {
            if (update.isStop()) {
                chooseCommand(update);
            } else {
                translateTextFromCard(update);
            }
        } else {
            translate(update);
        }
    }

    private synchronized void chooseLearningCommand(MyUpdate update) {
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

    private synchronized void chooseCommand(MyUpdate update) {
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

    // выбрана карточка по которой пользователь будет учить слова
    private synchronized void findCardCommand(MyUpdate update) {
        if (findCardByName(update)) {
            telegramBot.setModeWork(BotStatus.READY_TO_TRANSLATE);
            update.setBoard(keyBoard.showAllText(update));
            telegramBot.sendAnswerMessage(botCommand.cardWasFind(update));
        } else {
            update.setBoard(keyBoard.createLearningBoard(update));
            telegramBot.sendAnswerMessage(botCommand.cardWasNotFind(update));
            telegramBot.setModeWork(BotStatus.LEARNING_MODE);
        }
    }

    private synchronized boolean findCardByName(MyUpdate update) { // пользователь ввёл название карточки
        // проверка на правильность ввода
        // и проверка, что карточка не пустая
        return botCommand.findCardByName(update).isPresent() && botCommand.isCardEmpty(update);
    }

    private synchronized void translateTextFromCard(MyUpdate update) {
        update.setBoard(keyBoard.showAllText(update));
        telegramBot.sendAnswerMessage(
                botCommand.processTextWithBotStatus(update, BotStatus.TRANSLATE_EN_TO_RU)
        );
        log.debug("TRANSLATE text from card: " + update.getUser() + " : " + update.getText());
    }

    // пользователь решил начать учить слова
    private synchronized void startLearningCommand(MyUpdate update) {
        telegramBot.setModeWork(BotStatus.TEACHER_MODE);

        update.setBoard(keyBoard.showAllCardsBoard(update));

        telegramBot.sendAnswerMessage(botCommand.startLearning(update));

        log.debug("START LEARNING: " + update.getUser());
    }

    private synchronized void chooseCardCommand(MyUpdate update) {
        // ожидаю, что пользователь введёт название своей карточки (выберет из списка)
        telegramBot.setModeWork(BotStatus.SHOW_ALL_WORDS); // ставлю этот режим, чтобы показать другие карточки или выйти
        telegramBot.sendAnswerMessage(botCommand.showAllWord(update));
        log.debug("SHOW ALL WORD:" + update.getUserName() + " in card " + update.getText() + " ");
    }

    private synchronized void showAllCardsCommand(MyUpdate update) {
        // ставлю на следующее сообщение пользователя учебный режим бота
        telegramBot.setModeWork(BotStatus.SHOW_ALL_WORDS);

        // показываю все доступные карточки
        telegramBot.sendAnswerMessage(
                botCommand.showAllCards(update)
        );
        log.debug("SHOW ALL CARDS: " + update.getUser() + " name card: " + update.getText());
    }

    private synchronized void learningModeCommand(MyUpdate update) {
        telegramBot.setModeWork(BotStatus.LEARNING_MODE);
        update.setBoard(keyBoard.createLearningBoard(update));
        telegramBot.sendAnswerMessage(
                botCommand.processTextWithBotStatus(update, telegramBot.getModeWork())
        );
        log.debug("BOT SWITCHED TO TRAINING MODE: " + update.getUser() + " name card: " + update.getText());
    }

    private synchronized void backCommand(MyUpdate update) {
        update.setBoard(keyBoard.createLearningBoard(update));
        telegramBot.sendAnswerMessage(
                botCommand.stop(update)
        );
    }

    // пользователь указал боту, что он хочет удалить какую-то карточку
    private synchronized void removeCardCommand(MyUpdate update) {
        telegramBot.setModeWork(BotStatus.REMOVE_CARD);
        telegramBot.sendAnswerMessage(botCommand.removeCard(update));
        log.debug("USER WANT TO DELETE SAME CARD: " + update.getUser() + " " + update.getText());
    }

    // пользователь указал боту, какую конкретно карточку он хочет удалить
    private synchronized void removeCard(MyUpdate update) {
        update.setBoard(keyBoard.showAllCardsBoard(update));
        telegramBot.sendAnswerMessage(
                botCommand.processTextWithBotStatus(update, telegramBot.getModeWork())
        );
        telegramBot.setModeWork(BotStatus.LEARNING_MODE);
        log.debug("DELETE CARD: " + update.getUser() + " name card: " + update.getText());
    }

    // создание карточки
    private synchronized void createCard(MyUpdate update) {
        update.setBoard(keyBoard.createStopBoard());
        telegramBot.sendAnswerMessage(
                botCommand.processTextWithBotStatus(update, telegramBot.getModeWork())
        );
        log.debug("CREATE CARD: " + update.getUser() + " name card: " + update.getText());
    }

    // пользователь указал боту, что он хочет ввести слово
    private synchronized void addWordCommand(MyUpdate update) {
        telegramBot.setModeWork(BotStatus.ADDS_WORDS);
        telegramBot.sendAnswerMessage(
                botCommand.getInstructionHowAddWords(update) // бот выдал указания
        );
        log.debug("ADD NEW WORD IN CARD" + update.getUser() + " " + update.getText());
    }

    // пользователь ввёл слово, которое он хочет добавить в карточку
    private synchronized void addText(MyUpdate update) {
        update.setBoard(keyBoard.createStopBoard());
        telegramBot.sendAnswerMessage(
                botCommand.processTextWithBotStatus(update, telegramBot.getModeWork())
        );
        log.debug("ADD TEXT: " + update.getUser() + " add text " + update.getText());
    }

    //
    private synchronized void translate(MyUpdate update) {
        update.setBoard(keyBoard.createStopBoard());
        telegramBot.sendAnswerMessage(
                botCommand.processTextWithBotStatus(update, telegramBot.getModeWork())
        );
        log.debug("TRANSLATE text: " + update.getUser() + " : " + update.getText());
    }

    private synchronized void createCardCommand(MyUpdate update) {
        update.setBoard(keyBoard.createStopBoard());
        telegramBot.sendAnswerMessage(botCommand.createCard(update));
        telegramBot.setModeWork(BotStatus.CREATE_CARD);
        log.debug("CREATE COMMAND:" + update.getUser() + " : " + update.getText());
    }

    private synchronized void switchCommand(MyUpdate update) {
        update.setBoard(keyBoard.createStopBoard());
        telegramBot.sendAnswerMessage(botCommand.getInstruction(update));
        log.debug("SWITCH_MODE command: " + update.getUser() + " : " + update.getText());
    }

    private synchronized void startCommand(MyUpdate update) {
        update.setBoard(keyBoard.createStarterBoard());
        telegramBot.sendAnswerMessage(botCommand.start(update));
        log.debug("START command: " + update.getUser() + " : " + update.getText());
    }

    private synchronized void stopCommand(MyUpdate update) {
        update.setBoard(keyBoard.createStarterBoard());
        telegramBot.sendAnswerMessage(botCommand.stop(update));
        telegramBot.setModeWork(BotStatus.INITIAL_STATE);
        log.debug("STOP: " + update.getUser() + " : " + update.getText());
    }

    private synchronized void helpCommand(MyUpdate update) {
        update.setBoard(keyBoard.createStarterBoard());
        telegramBot.sendAnswerMessage(botCommand.help(update));
        log.debug("HELP command: " + update.getUser() + " : " + update.getText());
    }

    private synchronized void invalidCommand(MyUpdate update) {
        update.setBoard(keyBoard.createStarterBoard());
        telegramBot.setModeWork(BotStatus.INITIAL_STATE);
        telegramBot.sendAnswerMessage(botCommand.invalidCommand(update));
        log.debug("INVALID command: " + update.getUser() + " : " + update.getText());
    }

    private synchronized void unsupportedMessage(MyUpdate update) {
        update.setBoard(keyBoard.createStarterBoard());
        telegramBot.sendAnswerMessage(botCommand.invalidMessage(update));
        log.debug("UNSUPPORTED MESSAGE TYPE:" + update.getUser() + " : " + update.getText());
    }

}
