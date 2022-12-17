package org.ksTranslate.utilities;


import org.ksTranslate.model.MyUpdate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class MessageUtil {
    public static SendMessage send(MyUpdate update, String answer) {
        SendMessage newMessage = new SendMessage();
        newMessage.setText(answer);
        newMessage.setChatId(update.getMessage().getChatId());
        newMessage.setReplyMarkup(update.getBoard());

        return newMessage;
    }
}
