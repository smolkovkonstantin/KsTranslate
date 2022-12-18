package org.ksTranslate.dao;

import org.ksTranslate.model.Text;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface TextDAO extends JpaRepository<Text, Long> {
    Optional<Text> findByText(String text);
    @Query("SELECT COUNT (u) FROM Text u")
    Optional<Character> countAll();

    @Query("SELECT (t.text) from Text t where t.card.nameCard=?1")
    List<String> findAllByNameCard(String nameCard);

    @Modifying
    @Transactional
    @Query("delete from Text t where t.card in (select (c) from Card c where c.nameCard=?1)")
    void deleteAllByNameCard(String name);
}
