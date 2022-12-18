package org.ksTranslate.dao;

import org.ksTranslate.model.entity.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TelegramUserDAO extends JpaRepository<TelegramUser, Long> {

    @Query("SELECT (u.nameCard) FROM Card u")
    List<String> showAllNameCards();
}
