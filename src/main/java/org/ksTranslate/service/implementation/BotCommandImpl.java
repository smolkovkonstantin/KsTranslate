package org.ksTranslate.service.implementation;


import org.ksTranslate.configuration.TranslateConfiguration;
import org.ksTranslate.model.MyUpdate;
import org.ksTranslate.service.BotCommand;
import org.ksTranslate.service.MainService;
import org.ksTranslate.supportive.BotStatus;
import org.ksTranslate.utilities.MessageUtil;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

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

    private final SetKeyBoardImpl keyBoard;

    public BotCommandImpl(MainService mainService, SetKeyBoardImpl setKeyBoard) {
        this.mainService = mainService;
        this.keyBoard = setKeyBoard;
    }

    @Override
    public SendMessage switchMode(MyUpdate update) {
        return MessageUtil.send(update, INSTRUCTION);
    }

    @Override
    public SendMessage stop(MyUpdate update) {
        return MessageUtil.send(update, "Выполняю...");
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
    public SendMessage invalidCommand(MyUpdate update) {
        String answer = "Меня такому ещё не научили :( \nВведите другую команду";
        return MessageUtil.send(update, answer);
    }

    @Override
    public SendMessage invalidMessage(MyUpdate update) {
        String answer = "Извините, я понимаю только текстовые сообщения";

        return MessageUtil.send(update, answer);
    }

    @Override
    public SendMessage createCard(MyUpdate update) {

        String answer = "Введите название карточки";

        return MessageUtil.send(update, answer);
    }

    @Override
    public SendMessage showAllCards(MyUpdate update) {
        ReplyKeyboardMarkup keyboardMarkup = keyBoard.showAllCardsBoard(update);

        if (keyboardMarkup == null) {
            keyBoard.createStopBoard();
            return MessageUtil.send(update, "У вас ещё нет карточек");
        }

        update.setBoard(keyboardMarkup);

        return MessageUtil.send(update, "Нажмите на карточку, чтобы посмотреть какие слова вы добавили");
    }

    @Override
    public SendMessage chooseCard(MyUpdate update) {
        ReplyKeyboardMarkup keyboardMarkup = keyBoard.showAllCardsBoard(update);

        if (keyboardMarkup == null) {
            return MessageUtil.send(update, "У вас ещё нет карточек");
        }

        update.setBoard(keyboardMarkup);

        return MessageUtil.send(update, "Выберете карточку");
    }

    @Override
    public SendMessage getInstructionHowAddWords(MyUpdate update) {
        return MessageUtil.send(update, """
                Введите слово или фразу, которую хотите добавить в карточку, а после через '$'  введите название карточки
                                
                Слово автоматически будет переведено на английсий
                """);
    }

    @Override
    public SendMessage removeCard(MyUpdate update) {
        String answer;
        var temp = keyBoard.showAllCardsBoard(update);
        if (temp == null) {
            answer = "У вас нет карточек";
        } else {
            update.setBoard(temp);
            answer = "Выберете карточку которую хотите удалить";
        }

        return MessageUtil.send(update, answer);
    }

    @Override
    public SendMessage processTextWithBotStatus(MyUpdate update, BotStatus botStatus) {
        if (botStatus.equals(BotStatus.CREATE_CARD)) {

            String answer = mainService.registerCard(update);

            return MessageUtil.send(update, answer);

        } else if (botStatus.equals(BotStatus.ADDS_WORDS)) {

            String[] textAndCardName = update.getText().split("\\$");

            if (textAndCardName.length != 2) {
                return MessageUtil.send(update, "Не найдено '$' в Вашем сообщении");
            }

            String answer = mainService.addTextToCard(
                    translate(textAndCardName[0].strip(), "ru", "en"),
                    textAndCardName[1].strip()
            );

            return MessageUtil.send(update, answer);

        } else if (botStatus.equals(BotStatus.REMOVE_CARD)) {

            String answer = mainService.removeCard(update);

            update.setBoard(keyBoard.createLearningBoard());

            return MessageUtil.send(update, answer);

        } else if (botStatus.equals(BotStatus.TRANSLATE_RU_TO_EN)) {
            return translateText(update, "ru", "en");

        } else if (botStatus.equals(BotStatus.TRANSLATE_EN_TO_RU)) {
            return translateText(update, "en", "ru");

        } else if (botStatus.equals(BotStatus.LEARNING_MODE)) {
            return MessageUtil.send(update, "режим обучения активен");
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

    public SendMessage showAllWord(MyUpdate update) {
        List<String> texts = mainService.getAllWordsFromCard(update.getText());

        if (texts.size() == 0) {
            return MessageUtil.send(update, "В этой карточе нет слов");
        }

        StringBuilder answer = new StringBuilder();

        texts.forEach(text -> answer.append(text).append("\n"));

        return MessageUtil.send(update, answer.toString());

    }
}
