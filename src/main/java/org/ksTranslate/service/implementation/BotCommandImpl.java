package org.ksTranslate.service.implementation;


import org.ksTranslate.configuration.TranslateConfiguration;
import org.ksTranslate.model.MyUpdate;
import org.ksTranslate.service.BotCommand;
import org.ksTranslate.service.MainService;
import org.ksTranslate.supportive.BotStatus;
import org.ksTranslate.utilities.MessageUtil;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class BotCommandImpl implements BotCommand {

    private static final String HELP_MESSAGE = """
            Я могу переводить с русского языка на английский и наоборот.
                            
            Мои команды:
                /start -
                /help -
                /translate -
            """;

    private static final String INSTRUCTION = """
            Enter the word or phrase
            """;

    private final MainService mainService;

    public BotCommandImpl(MainService mainService) {
        this.mainService = mainService;
    }

    @Override
    public SendMessage switchMode(MyUpdate update) {
        return MessageUtil.send(update, INSTRUCTION);
    }

    @Override
    public SendMessage stop(MyUpdate update) {
        return MessageUtil.send(update, "What's next?");
    }

    private String translate(String text, String fromLanguage, String toLanguage) {

        TranslateConfiguration configuration = new TranslateConfiguration(fromLanguage, toLanguage);

        HttpsURLConnection con = configuration.createConnection(text);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {

            StringBuilder answer = new StringBuilder();

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                answer.append(inputLine);
            }
            in.close();

            return answer.toString();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SendMessage start(MyUpdate update) {
        String answer = mainService.processStartMessage(update);

        return MessageUtil.send(update, answer);
    }

    @Override
    public SendMessage help(MyUpdate update) {
        return MessageUtil.send(update, HELP_MESSAGE);
    }

    @Override
    public SendMessage processText(MyUpdate update, BotStatus botStatus) {

        String fromLanguage;
        String toLanguage;

        if (botStatus.equals(BotStatus.TRANSLATE_EN_TO_RU)){
            fromLanguage = "en";
            toLanguage = "ru";
        } else{
            fromLanguage = "ru";
            toLanguage = "en";
        }

        String answer = update.getUpdate().getMessage().getText();

        return MessageUtil.send(update, translate(answer, fromLanguage, toLanguage));
    }
}
