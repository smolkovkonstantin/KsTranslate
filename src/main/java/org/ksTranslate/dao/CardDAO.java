package org.ksTranslate.dao;

import org.ksTranslate.model.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Optional;

public interface CardDAO extends JpaRepository<Card, Long> {
    Optional<Card> findByNameCardAndTelegramUser_Id(String nameCard, Long telegramUser_chartId);

    @Query("SELECT COUNT (u) FROM Card u where u.telegramUser.id=?1")
    Optional<Long> countCards(Long chartId);

    default int getMaxSize() {
        return 20;
    }

    @Transactional
    void deleteByNameCardAndTelegramUserId(String nameCard, Long telegramUser_chartId);
}
