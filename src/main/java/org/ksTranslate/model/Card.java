package org.ksTranslate.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCard;

    private String nameCard;

    @ManyToOne
    @JoinColumn(name = "telegram_user_id")
    private TelegramUser telegramUser;

    @OneToMany(mappedBy = "card")
    @ToString.Exclude
    private List<Text> words;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Card card = (Card) o;
        return idCard != null && Objects.equals(idCard, card.idCard);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
