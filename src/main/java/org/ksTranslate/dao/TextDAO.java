package org.ksTranslate.dao;

import org.ksTranslate.model.Card;
import org.ksTranslate.model.Text;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TextDAO extends JpaRepository<Text, Long> {
    Optional<Text> findByText(String text);
    @Query("SELECT COUNT (u) FROM Text u")
    Optional<Character> countAll();
}
