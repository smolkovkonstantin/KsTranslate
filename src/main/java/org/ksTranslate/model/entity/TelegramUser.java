package org.ksTranslate.model.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "telegram_users")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelegramUser {

    @Id
    private Long id;

    private String userName;

    private String firstName;

    private String lastName;

    @OneToMany(mappedBy = "telegramUser")
    @ToString.Exclude
    private List<Card> cards;

    @OneToOne(mappedBy = "telegramUser")
    @JoinColumn(name = "bot_id")
    private Bot bot;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TelegramUser that = (TelegramUser) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
