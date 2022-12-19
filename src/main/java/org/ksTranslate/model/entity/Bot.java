package org.ksTranslate.model.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.ksTranslate.supportive.BotStatus;

import javax.persistence.*;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bots")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Bot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private BotStatus modeWork;

    @OneToOne(fetch = FetchType.LAZY)
    private TelegramUser telegramUser;
}
