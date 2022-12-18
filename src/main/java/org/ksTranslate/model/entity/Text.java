package org.ksTranslate.model.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "words")
public class Text {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idText;

    private String text;

    private int numberOnCard; // порядковый номер слова на карточке

    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;
}
