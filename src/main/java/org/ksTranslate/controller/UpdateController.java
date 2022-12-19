package org.ksTranslate.controller;

import lombok.extern.log4j.Log4j;
import org.ksTranslate.model.MyUpdate;
import org.ksTranslate.service.implementation.BotCommandImpl;
import org.ksTranslate.service.implementation.MainServiceImpl;
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

    private final MainServiceImpl mainService;

    public UpdateController(BotCommandImpl botCommand, SetKeyBoardImpl setKeyBoard, MainServiceImpl mainService) {
        this.botCommand = botCommand;
        this.keyBoard = setKeyBoard;
        this.mainService = mainService;
    }

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    // метод проверяет, какое сообщение бот получил и обрабатывает только текстовое
    // если сообщение "непонятного" типа, то пользователю пишется об этом
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

        if (update.isStart()){
            startCommand(update);

        } else if (mainService.getBotStatus(update).equals(BotStatus.INITIAL_STATE) || update.isStop()) {
            chooseCommand(update);

        } else if (mainService.getBotStatus(update).equals(BotStatus.CREATE_CARD)) {
            createCard(update);

        } else if (mainService.getBotStatus(update).equals(BotStatus.REMOVE_CARD)) {
            removeCard(update);

        } else if (mainService.getBotStatus(update).equals(BotStatus.ADDS_WORDS)) {
            addText(update);

        } else if (mainService.getBotStatus(update).equals(BotStatus.LEARNING_MODE)) {
            chooseLearningCommand(update);

        } else if (mainService.getBotStatus(update).equals(BotStatus.SHOW_ALL_WORDS)) {
            if (update.isStop()) {
                chooseCommand(update);
            } else {
                chooseCardCommand(update);
            }
        } else if (mainService.getBotStatus(update).equals(BotStatus.TEACHER_MODE)) {
            if (update.isStop()) {
                chooseCommand(update);
            } else {
                findCardCommand(update);
            }
        } else if (mainService.getBotStatus(update).equals(BotStatus.READY_TO_TRANSLATE)) {
            if (update.isStop()) {
                chooseCommand(update);
            } else {
                translateTextFromCard(update);
            }
        } else {
            translate(update);
        }
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
            mainService.setBotStatus(BotStatus.TRANSLATE_RU_TO_EN, update);
            switchCommand(update);

        } else if (update.isEnToRuSwitch()) {
            mainService.setBotStatus(BotStatus.TRANSLATE_EN_TO_RU, update);
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
    private void findCardCommand(MyUpdate update) {
        if (findCardByName(update)) {
            mainService.setBotStatus(BotStatus.READY_TO_TRANSLATE, update);
            update.setBoard(keyBoard.showAllText(update));
            telegramBot.sendAnswerMessage(botCommand.cardWasFind(update));
        } else {
            update.setBoard(keyBoard.createLearningBoard(update));
            telegramBot.sendAnswerMessage(botCommand.cardWasNotFind(update));
            mainService.setBotStatus(BotStatus.LEARNING_MODE, update);
        }
    }

    private boolean findCardByName(MyUpdate update) { // пользователь ввёл название карточки
        // проверка на правильность ввода
        // и проверка, что карточка не пустая
        return botCommand.findCardByName(update).isPresent() && botCommand.isCardEmpty(update);
    }

    private void translateTextFromCard(MyUpdate update) {
        update.setBoard(keyBoard.showAllText(update));
        telegramBot.sendAnswerMessage(
                botCommand.processTextWithBotStatus(update, BotStatus.TRANSLATE_EN_TO_RU)
        );
        log.debug("TRANSLATE text from card: " + update.getUser() + " : " + update.getText());
    }

    // пользователь решил начать учить слова
    private void startLearningCommand(MyUpdate update) {
        mainService.setBotStatus(BotStatus.TEACHER_MODE, update);

        update.setBoard(keyBoard.showAllCardsBoard(update));

        telegramBot.sendAnswerMessage(botCommand.startLearning(update));

        log.debug("START LEARNING: " + update.getUser());
    }

    private void chooseCardCommand(MyUpdate update) {
        // ожидаю, что пользователь введёт название своей карточки (выберет из списка)
        mainService.setBotStatus(BotStatus.SHOW_ALL_WORDS, update); // ставлю этот режим, чтобы показать другие карточки или выйти
        telegramBot.sendAnswerMessage(botCommand.showAllWord(update));
        log.debug("SHOW ALL WORD:" + update.getUserName() + " in card " + update.getText() + " ");
    }

    private void showAllCardsCommand(MyUpdate update) {
        // ставлю на следующее сообщение пользователя учебный режим бота
        mainService.setBotStatus(BotStatus.SHOW_ALL_WORDS, update);

        // показываю все доступные карточки
        telegramBot.sendAnswerMessage(
                botCommand.showAllCards(update)
        );
        log.debug("SHOW ALL CARDS: " + update.getUser() + " name card: " + update.getText());
    }

    private void learningModeCommand(MyUpdate update) {
        mainService.setBotStatus(BotStatus.LEARNING_MODE, update);
        update.setBoard(keyBoard.createLearningBoard(update));
        telegramBot.sendAnswerMessage(
                botCommand.processTextWithBotStatus(update, mainService.getBotStatus(update))
        );
        log.debug("BOT SWITCHED TO TRAINING MODE: " + update.getUser() + " name card: " + update.getText());
    }

    private void backCommand(MyUpdate update) {
        update.setBoard(keyBoard.createLearningBoard(update));
        telegramBot.sendAnswerMessage(
                botCommand.stop(update)
        );
    }

    // пользователь указал боту, что он хочет удалить какую-то карточку
    private void removeCardCommand(MyUpdate update) {
        mainService.setBotStatus(BotStatus.REMOVE_CARD, update);
        telegramBot.sendAnswerMessage(botCommand.removeCard(update));
        log.debug("USER WANT TO DELETE SAME CARD: " + update.getUser() + " " + update.getText());
    }

    // пользователь указал боту, какую конкретно карточку он хочет удалить
    private void removeCard(MyUpdate update) {
        update.setBoard(keyBoard.showAllCardsBoard(update));
        telegramBot.sendAnswerMessage(
                botCommand.processTextWithBotStatus(update, mainService.getBotStatus(update))
        );
        mainService.setBotStatus(BotStatus.LEARNING_MODE, update);
        log.debug("DELETE CARD: " + update.getUser() + " name card: " + update.getText());
    }

    // создание карточки
    private void createCard(MyUpdate update) {
        update.setBoard(keyBoard.createStopBoard());
        telegramBot.sendAnswerMessage(
                botCommand.processTextWithBotStatus(update, mainService.getBotStatus(update))
        );
        log.debug("CREATE CARD: " + update.getUser() + " name card: " + update.getText());
    }

    // пользователь указал боту, что он хочет ввести слово
    private void addWordCommand(MyUpdate update) {
        mainService.setBotStatus(BotStatus.ADDS_WORDS, update);
        telegramBot.sendAnswerMessage(
                botCommand.getInstructionHowAddWords(update) // бот выдал указания
        );
        log.debug("ADD NEW WORD IN CARD" + update.getUser() + " " + update.getText());
    }

    // пользователь ввёл слово, которое он хочет добавить в карточку
    private void addText(MyUpdate update) {
        update.setBoard(keyBoard.createStopBoard());
        telegramBot.sendAnswerMessage(
                botCommand.processTextWithBotStatus(update, mainService.getBotStatus(update))
        );
        log.debug("ADD TEXT: " + update.getUser() + " add text " + update.getText());
    }

    //
    private void translate(MyUpdate update) {
        update.setBoard(keyBoard.createStopBoard());
        telegramBot.sendAnswerMessage(
                botCommand.processTextWithBotStatus(update, mainService.getBotStatus(update))
        );
        log.debug("TRANSLATE text: " + update.getUser() + " : " + update.getText());
    }

    private void createCardCommand(MyUpdate update) {
        update.setBoard(keyBoard.createStopBoard());
        telegramBot.sendAnswerMessage(botCommand.createCard(update));
        mainService.setBotStatus(BotStatus.CREATE_CARD, update);
        log.debug("CREATE COMMAND:" + update.getUser() + " : " + update.getText());
    }

    private void switchCommand(MyUpdate update) {
        update.setBoard(keyBoard.createStopBoard());
        telegramBot.sendAnswerMessage(botCommand.getInstruction(update));
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
        mainService.setBotStatus(BotStatus.INITIAL_STATE, update);
        log.debug("STOP: " + update.getUser() + " : " + update.getText());
    }

    private void helpCommand(MyUpdate update) {
        update.setBoard(keyBoard.createStarterBoard());
        telegramBot.sendAnswerMessage(botCommand.help(update));
        log.debug("HELP command: " + update.getUser() + " : " + update.getText());
    }

    private void invalidCommand(MyUpdate update) {
        update.setBoard(keyBoard.createStarterBoard());
        mainService.setBotStatus(BotStatus.INITIAL_STATE, update);
        telegramBot.sendAnswerMessage(botCommand.invalidCommand(update));
        log.debug("INVALID command: " + update.getUser() + " : " + update.getText());
    }

    private void unsupportedMessage(MyUpdate update) {
        update.setBoard(keyBoard.createStarterBoard());
        telegramBot.sendAnswerMessage(botCommand.invalidMessage(update));
        log.debug("UNSUPPORTED MESSAGE TYPE:" + update.getUser() + " : " + update.getText());
    }

}
