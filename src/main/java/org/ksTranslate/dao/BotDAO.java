package org.ksTranslate.dao;

import org.ksTranslate.model.entity.Bot;
import org.ksTranslate.supportive.BotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


public interface BotDAO extends JpaRepository<Bot, Long> {
    @Transactional
    @Modifying
    @Query("update Bot b set b.modeWork = ?1 where b.telegramUser.id = ?2")
    void updateModeWorkByModeWork(BotStatus modeWork, Long chartId);

    @Query("select (b) from Bot b where b.telegramUser.id=?1")
    @Transactional
    Optional<Bot> getBotByTelegramUserChartId(Long telegramUserChartId);
}
