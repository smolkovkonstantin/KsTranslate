package org.ksTranslate.service.implementation;

import lombok.Getter;
import org.ksTranslate.dao.CardDAO;
import org.ksTranslate.dao.TelegramUserDAO;
import org.ksTranslate.dao.TextDAO;
import org.ksTranslate.model.entity.Card;
import org.ksTranslate.model.MyUpdate;
import org.ksTranslate.model.entity.TelegramUser;
import org.ksTranslate.model.entity.Text;
import org.ksTranslate.service.MainService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Getter
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
    public List<String> getAllCards(MyUpdate update) {
        return telegramUserDAO.showAllNameCards(update.getChatId());
    }

    @Override
    public List<String> getAllWordsFromCard(MyUpdate update) {
        return textDAO.findAllByNameCard(update.getText(), update.getChatId());
    }

    @Override
    public String processStartMessage(MyUpdate update) {

        StringBuilder answer = new StringBuilder();

        if (telegramUserDAO.findById(update.getMessage().getChatId()).isEmpty()) {
            registerUser(update);
            answer.append("Рад с Вами познакомиться, ");
        } else {
            answer.append("Приятно Вас снова видеть, ");
        }

        answer.append(telegramUserDAO.findById(update.getChatId()).get().getFirstName());
        return answer.toString();
    }

    private void registerUser(MyUpdate update) {
        TelegramUser telegramUser = TelegramUser.builder()
                .chartId(update.getChatId())
                .firstName(update.getUser().getFirstName())
                .lastName(update.getUser().getLastName())
                .userName(update.getUserName()).build();

        telegramUserDAO.save(telegramUser);
    }

    @Override
    public String addTextToCard(MyUpdate update, String text, String nameCard) {
        String answer;

        if (textDAO.countByCardNameCard(nameCard, update.getChatId()).equals(cardDAO.getMaxSize())) {
            answer = "Карточка вся исписана, создайте ещё одну";
        } else {
            if (textDAO.findByTextAndCard_TelegramUser_ChartId(text, update.getChatId()).isEmpty()) {
                if (cardDAO.findByNameCardAndTelegramUser_ChartId(nameCard, update.getChatId()).isEmpty()) {
                    answer = "Карточки с таким названием нет";
                } else {
                    registerText(update, text, nameCard);
                    answer = "Карточка обновлена словом (фразой): " + text;
                }
            } else {
                answer = "Такое слово уже есть на карточке";
            }
        }

        return answer;
    }

    private void registerText(MyUpdate update, String newText, String nameCard) {
        Text text = Text.builder()
                .text(newText)
                .numberOnCard(textDAO.countByCardNameCard(nameCard, update.getChatId()) + 1)
                .card(cardDAO.findByNameCardAndTelegramUser_ChartId(nameCard, update.getChatId()).get())
                .build();

        textDAO.save(text);
    }

    @Override
    public String registerCard(MyUpdate update) {
        String answer;

        if (cardDAO.findByNameCardAndTelegramUser_ChartId(update.getText(), update.getChatId()).isEmpty()) {
            createCard(update);
            answer = "Создана новая карточка с названием: " + update.getText();
        } else {
            answer = "Такая карточка уже была создана\n" +
                    "Создайте карточку с другим названием";
        }
        return answer;
    }

    private void createCard(MyUpdate update) {
        Card card = Card.builder()
                .nameCard(update.getText())
                .telegramUser(telegramUserDAO.findById(update.getChatId()).get())
                .build();

        cardDAO.save(card);
    }

    @Override
    public String removeCard(MyUpdate update) {
        textDAO.deleteAllByNameCard(update.getText(), update.getChatId());
        cardDAO.deleteByNameCardAndTelegramUserChartId(update.getText(), update.getChatId());
        return "Карточка с названием" + " " + update.getText() + " " + "удалена";
    }
}
