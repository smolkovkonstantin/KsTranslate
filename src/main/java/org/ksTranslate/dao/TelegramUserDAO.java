package org.ksTranslate.dao;

import org.ksTranslate.model.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelegramUserDAO extends JpaRepository<TelegramUser, Long> {
}
