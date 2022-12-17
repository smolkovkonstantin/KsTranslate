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
        } else {
            translateCommand(update);
            log.debug("TRANSLATE text: " + update.getUpdate().getMessage());
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

        } else {
            invalidCommand(update);

        }
    }

    private void unsupportedMessage(MyUpdate update) {
        update.setBoard(setKeyBoard.createStarterBoard());
        telegramBot.sendAnswerMessage(botCommand.invalidMessage(update));
        log.debug("UNSUPPORTED MESSAGE TYPE:" + update.getMessage());
    }

    private void stopCommand(MyUpdate update) {
        update.setBoard(setKeyBoard.createStarterBoard());
        telegramBot.setModeWork(BotStatus.INITIAL_STATE);
        telegramBot.sendAnswerMessage(botCommand.stop(update));
        log.debug("STOP TRANSLATE:" + update);
    }

    private void translateCommand(MyUpdate update) {
        update.setBoard(setKeyBoard.createStopBoard());
        System.out.println(telegramBot.getModeWork());
        telegramBot.sendAnswerMessage(
                botCommand.processText(update, telegramBot.getModeWork())
        );
    }

    private void switchCommand(MyUpdate update) {
        update.setBoard(setKeyBoard.createStopBoard());
        telegramBot.sendAnswerMessage(botCommand.switchMode(update));
        log.debug("SWITCH_MODE command: " + update.getMessage());
    }

    private void startCommand(MyUpdate update) {
        update.setBoard(setKeyBoard.createStarterBoard());
        telegramBot.sendAnswerMessage(botCommand.start(update));
        log.debug("START command: " + update.getMessage());
    }

    private void helpCommand(MyUpdate update) {
        update.setBoard(setKeyBoard.createStarterBoard());
        telegramBot.sendAnswerMessage(botCommand.help(update));
        log.debug("HELP command: " + update.getMessage());
    }

    private void invalidCommand(MyUpdate update) {
        update.setBoard(setKeyBoard.createStarterBoard());
        telegramBot.sendAnswerMessage(botCommand.invalidCommand(update));
        log.debug("INVALID command: " + update.getMessage());
    }
}
