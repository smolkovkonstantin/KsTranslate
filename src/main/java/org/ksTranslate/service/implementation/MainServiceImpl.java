package org.ksTranslate.service.implementation;

import org.ksTranslate.dao.TelegramUserDAO;
import org.ksTranslate.model.MyUpdate;
import org.ksTranslate.model.TelegramUser;
import org.ksTranslate.service.MainService;
import org.springframework.stereotype.Service;

@Service
public class MainServiceImpl implements MainService {

    private final TelegramUserDAO telegramUserDAO;

    public MainServiceImpl(TelegramUserDAO telegramUserDAO) {
        this.telegramUserDAO = telegramUserDAO;
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
}
