package org.ksTranslate.service.implementation;

import org.ksTranslate.dao.CardDAO;
import org.ksTranslate.dao.TelegramUserDAO;
import org.ksTranslate.dao.TextDAO;
import org.ksTranslate.model.Card;
import org.ksTranslate.model.MyUpdate;
import org.ksTranslate.model.TelegramUser;
import org.ksTranslate.model.Text;
import org.ksTranslate.service.MainService;
import org.springframework.stereotype.Service;

@Service
public class MainServiceImpl implements MainService {

    private final TelegramUserDAO telegramUserDAO;

    private final CardDAO cardDAO;

    private final TextDAO textDAO;

    public MainServiceImpl(TelegramUserDAO telegramUserDAO, CardDAO cardDAO, TextDAO textDAO) {
        this.telegramUserDAO = telegramUserDAO;
        this.cardDAO = cardDAO;
        this.textDAO = textDAO;
    }

    @Override
    public String processStartMessage(MyUpdate update) {

        StringBuilder answer = new StringBuilder();

        if (telegramUserDAO.findById(update.getMessage().getChatId()).isEmpty()) {
            registerUser(update);
            answer.append("Nice to meet you, ");
        } else {
            answer.append("Hi, ");
        }

        answer.append(telegramUserDAO.findById(update.getMessage().getChatId()).get().getUserName());
        return answer.toString();
    }

    private void registerUser(MyUpdate update) {
        TelegramUser telegramUser = TelegramUser.builder()
                .chartId(update.getMessage().getChatId())
                .userName(update.getMessage().getFrom().getUserName()).build();

        telegramUserDAO.save(telegramUser);
    }

    @Override
    public String addTextToCard(String text, String nameCard) {
        String answer;

        if (textDAO.countAll().get().equals(cardDAO.getMaxSize())) {
            answer = "The card is full, so create a new one";
        } else {
            if (textDAO.findByText(text).isEmpty()) {
                if (cardDAO.findByNameCard(nameCard).isEmpty()) {
                    answer = "Wrong name of card";
                } else {
                    registerText(text, nameCard);
                    answer = "Added a new word";
                }
            } else {
                answer = "This word have already been added";
            }
        }

        return answer;
    }

    private void registerText(String newText, String nameCard) {
        Text text = Text.builder()
                .text(newText)
                .card(cardDAO.findByNameCard(nameCard).get())
                .build();

        textDAO.save(text);
    }

    @Override
    public String createCard(MyUpdate update) {
        String answer;

        if (cardDAO.findByNameCard(update.getText()).isEmpty()) {
            registerCard(update);
            answer = "Created new card with name: " + update.getText();
        } else {
            answer = "This card have already been created.\n" +
                    "Specify a different name for the card";
        }
        return answer;
    }

    private void registerCard(MyUpdate update) {
        Card card = Card.builder()
                .nameCard(update.getText())
                .telegramUser(telegramUserDAO.findById(update.getChatId()).get())
                .build();

        cardDAO.save(card);
    }
}
