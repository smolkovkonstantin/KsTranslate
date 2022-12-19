package org.ksTranslate.dao;

import org.ksTranslate.model.entity.Text;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface TextDAO extends JpaRepository<Text, Long> {
    Optional<Text> findByTextAndCard_TelegramUser_id(String text, Long card_telegramUser_chartId);

    @Query("SELECT (t.text) from Text t where t.card.nameCard=?1 and t.card.telegramUser.id=?2")
    List<String> findAllByNameCard(String nameCard, Long chartId);

    @Modifying
    @Transactional
    @Query("delete from Text t where t.card in (select (c) from Card c" +
            " where c.nameCard=?1 and c.telegramUser.id=?2)")
    void deleteAllByNameCard(String name, Long chartId);


    @Query("select count(t) from Text t where t.card.nameCard=?1 and t.card.telegramUser.id=?2")
    Integer countByCardNameCard(String card_nameCard, Long chartId);
}
