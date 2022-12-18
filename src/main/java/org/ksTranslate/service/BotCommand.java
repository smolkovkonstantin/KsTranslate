package org.ksTranslate.service;

import org.ksTranslate.model.MyUpdate;
import org.ksTranslate.supportive.BotStatus;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface BotCommand {

    SendMessage getInstruction(MyUpdate update); // команда выдаёт инструкции

    SendMessage stop(MyUpdate update); // команда выполняется при нажатии на крестик

    SendMessage start(MyUpdate update);

    SendMessage processTextWithBotStatus(MyUpdate update, BotStatus command);

    SendMessage help(MyUpdate update);

    SendMessage invalidCommand(MyUpdate update);

    SendMessage invalidMessage(MyUpdate update);

    SendMessage createCard(MyUpdate update);

    SendMessage showAllCards(MyUpdate update);

    SendMessage getInstructionHowAddWords(MyUpdate update);

    SendMessage removeCard(MyUpdate update);

    SendMessage cardWasFind(MyUpdate update);

    SendMessage cardWasNotFind(MyUpdate update);

    boolean isCardEmpty(MyUpdate update);
}
