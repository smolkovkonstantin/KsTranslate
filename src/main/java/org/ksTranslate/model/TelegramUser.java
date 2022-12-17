package org.ksTranslate.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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
    private Long chartId;

    private String userName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TelegramUser that = (TelegramUser) o;
        return chartId != null && Objects.equals(chartId, that.chartId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
