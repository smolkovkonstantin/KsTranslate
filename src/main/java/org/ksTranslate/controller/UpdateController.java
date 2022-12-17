package org.ksTranslate.controller;

import lombok.extern.log4j.Log4j;
import org.ksTranslate.model.MyUpdate;
import org.ksTranslate.service.implementation.BotCommandImpl;
import org.ksTranslate.service.implementation.SetKeyBoardImpl;
import org.ksTranslate.supportive.BotStatus;
import org.springframework.stereotype.Component;

@Component
@Log4j
public class UpdateController {

    private TelegramBot telegramBot;
    private final BotCommandImpl botCommand;
    private final SetKeyBoardImpl setKeyBoard;

    public UpdateController(BotCommandImpl botCommand, SetKeyBoardImpl setKeyBoard) {
        this.botCommand = botCommand;
        this.setKeyBoard = setKeyBoard;
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

        } else if (telegramBot.getModeWork().equals(BotStatus.ADDS_WORD)) {
            addText(update);

        } else {
            translate(update);
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

        } else if (update.isAddWord()) {
            telegramBot.setModeWork(BotStatus.ADDS_WORD);
            switchCommand(update);

        } else if (update.isCreateCard()) {
            telegramBot.setModeWork(BotStatus.CREATE_CARD);
            createCommand(update);

        } else {
            invalidCommand(update);

        }
    }

    private void createCard(MyUpdate update) {
        update.setBoard(setKeyBoard.createStopBoard());
        telegramBot.sendAnswerMessage(
                botCommand.processText(update, telegramBot.getModeWork())
        );
        telegramBot.setModeWork(BotStatus.INITIAL_STATE);
        log.debug("CREATE CARD: " + update.getUserName() + " name card: " + update.getText());
    }

    private void addText(MyUpdate update) {
        update.setBoard(setKeyBoard.createStopBoard());
        telegramBot.sendAnswerMessage(
                botCommand.processText(update, telegramBot.getModeWork())
        );
        log.debug("ADD TEXT: " + update.getUserName() + " add text " + update.getText());
    }

    private void translate(MyUpdate update) {
        update.setBoard(setKeyBoard.createStopBoard());
        telegramBot.sendAnswerMessage(
                botCommand.processText(update, telegramBot.getModeWork())
        );
        log.debug("TRANSLATE text: " + update.getUserName() + " : " + update.getText());
    }

    private void stopCommand(MyUpdate update) {
        update.setBoard(setKeyBoard.createStarterBoard());
        telegramBot.setModeWork(BotStatus.INITIAL_STATE);
        telegramBot.sendAnswerMessage(botCommand.stop(update));
        log.debug("STOP TRANSLATE: " + update.getUserName() + " : " + update.getText());
    }

    private void createCommand(MyUpdate update) {
        update.setBoard(setKeyBoard.createStopBoard());
        telegramBot.sendAnswerMessage(botCommand.createCard(update));
        log.debug("CREATE COMMAND:" + update.getUserName() + " : " + update.getText());
    }

    private void unsupportedMessage(MyUpdate update) {
        update.setBoard(setKeyBoard.createStarterBoard());
        telegramBot.sendAnswerMessage(botCommand.invalidMessage(update));
        log.debug("UNSUPPORTED MESSAGE TYPE:" + update.getUserName() + " : " + update.getText());
    }

    private void switchCommand(MyUpdate update) {
        update.setBoard(setKeyBoard.createStopBoard());
        telegramBot.sendAnswerMessage(botCommand.switchMode(update));
        log.debug("SWITCH_MODE command: " + update.getUserName() + " : " + update.getText());
    }

    private void startCommand(MyUpdate update) {
        update.setBoard(setKeyBoard.createStarterBoard());
        telegramBot.sendAnswerMessage(botCommand.start(update));
        log.debug("START command: " + update.getUserName() + " : " + update.getText());
    }

    private void helpCommand(MyUpdate update) {
        update.setBoard(setKeyBoard.createStarterBoard());
        telegramBot.sendAnswerMessage(botCommand.help(update));
        log.debug("HELP command: " + update.getUserName() + " : " + update.getText());
    }

    private void invalidCommand(MyUpdate update) {
        update.setBoard(setKeyBoard.createStarterBoard());
        telegramBot.sendAnswerMessage(botCommand.invalidCommand(update));
        log.debug("INVALID command: " + update.getUserName() + " : " + update.getText());
    }

}
