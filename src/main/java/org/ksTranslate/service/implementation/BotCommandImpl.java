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
    public SendMessage createCard(MyUpdate update) {

        String answer = "Enter the name of the card";

        return MessageUtil.send(update, answer);
    }

    @Override
    public SendMessage processText(MyUpdate update, BotStatus botStatus) {
        if (botStatus.equals(BotStatus.CREATE_CARD)) {

            String answer = mainService.createCard(update);

            return MessageUtil.send(update, answer);

        } else if (botStatus.equals(BotStatus.ADDS_WORD)) {

            String[] textAndCardName = update.getText().split(":");

            if (textAndCardName.length != 2) {
                return MessageUtil.send(update, "Error input");
            }

            String answer = mainService.addTextToCard(textAndCardName[0].strip(), textAndCardName[1].strip());
            return MessageUtil.send(update, answer);

        } else if (botStatus.equals(BotStatus.TRANSLATE_RU_TO_EN)) {
            return translateText(update, "ru", "en");

        } else if (botStatus.equals(BotStatus.TRANSLATE_EN_TO_RU)) {
            return translateText(update, "en", "ru");

        } else {
            throw new RuntimeException();
        }
    }

    private SendMessage translateText(MyUpdate update, String fromLanguage, String toLanguage) {
        String answer = update.getUpdate().getMessage().getText();
        return MessageUtil.send(update, translate(answer, fromLanguage, toLanguage));
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

}
