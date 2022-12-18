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
    public List<String> getAllCards() {
        return telegramUserDAO.showAllNameCards();
    }

    @Override
    public List<String> getAllWordsFromCard(String text) {
        return textDAO.findAllByNameCard(text);
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
                .chartId(update.getMessage().getChatId())
                .firstName(update.getUser().getFirstName())
                .lastName(update.getUser().getLastName())
                .userName(update.getUserName()).build();

        telegramUserDAO.save(telegramUser);
    }

    @Override
    public String addTextToCard(String text, String nameCard) {
        String answer;

        if (textDAO.countByCardNameCard(nameCard).equals(cardDAO.getMaxSize())) {
            answer = "Карточка вся исписана, создайте ещё одну";
        } else {
            if (textDAO.findByText(text).isEmpty()) {
                if (cardDAO.findByNameCard(nameCard).isEmpty()) {
                    answer = "Карточки с таким названием нет";
                } else {
                    registerText(text, nameCard);
                    answer = "Карточка обновлена словом (фразой): " + text;
                }
            } else {
                answer = "Такое слово уже есть на карточке";
            }
        }

        return answer;
    }

    private void registerText(String newText, String nameCard) {
        Text text = Text.builder()
                .text(newText)
                .numberOnCard(textDAO.countByCardNameCard(nameCard) + 1)
                .card(cardDAO.findByNameCard(nameCard).get())
                .build();

        textDAO.save(text);
    }

    @Override
    public String registerCard(MyUpdate update) {
        String answer;

        if (cardDAO.findByNameCard(update.getText()).isEmpty()) {
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
        textDAO.deleteAllByNameCard(update.getText());
        cardDAO.deleteByNameCard(update.getText());
        return "Карточка с названием" + " " + update.getText() + " " + "удалена";
    }

    @Override
    public String showWordFromCard(String nameCard, int idWord) {
        String result = textDAO.findByNameCardAndNumberOnCard(nameCard, idWord);

        if (result == null){
            result = "В этой карточке нет слов";
        }
        return result;
    }
}
