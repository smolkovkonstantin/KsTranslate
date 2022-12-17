package org.ksTranslate.service;

import org.ksTranslate.model.MyUpdate;
import org.ksTranslate.supportive.BotStatus;
import org.ksTranslate.utilities.MessageUtil;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface BotCommand {

    SendMessage switchMode(MyUpdate update);

    SendMessage stop(MyUpdate update);

    SendMessage start(MyUpdate update);

    SendMessage processText(MyUpdate update, BotStatus command);

    SendMessage help(MyUpdate update);

    default SendMessage invalidCommand(MyUpdate update) {
        String answer = """
                Invalid command.

                Неверная команда.
                """;
        return MessageUtil.send(update, answer);
    }

    default SendMessage invalidMessage(MyUpdate update) {
        String answer = """
                Sorry, I understand only text message

                Извините, я понимаю только текстовые сообщения""";

        return MessageUtil.send(update, answer);
    }
}
