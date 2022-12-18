package org.ksTranslate.dao;

import org.ksTranslate.model.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Optional;

public interface CardDAO extends JpaRepository<Card, Long> {
    Optional<Card> findByNameCard(String nameCard);

    @Query("SELECT COUNT (u) FROM Card u")
    Optional<Long> countCards();

    default int getMaxSize() {
        return 20;
    }

    @Transactional
    void deleteByNameCard(String name);
}
