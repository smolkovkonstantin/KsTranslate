package org.ksTranslate.model;

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

    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;
}
