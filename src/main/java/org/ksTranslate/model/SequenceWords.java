package org.ksTranslate.model;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Objects;

@Getter
@Setter
public class SequenceWords {

    private String nameCard = null;

    private User user;

    private int idWord = 0;

    public SequenceWords(String nameCard, User user) {
        this.nameCard = nameCard;
        this.user = user;
    }

    public SequenceWords(User user) {
        this.user = user;
    }

    public boolean isFirstWord() {
        return idWord == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SequenceWords wordQueue = (SequenceWords) o;
        return Objects.equals(user, wordQueue.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }

    @Override
    public String toString() {
        return "SequenceWords{" +
                "nameCard='" + nameCard + '\'' +
                ", user=" + user +
                ", idWord=" + idWord +
                '}';
    }
}
