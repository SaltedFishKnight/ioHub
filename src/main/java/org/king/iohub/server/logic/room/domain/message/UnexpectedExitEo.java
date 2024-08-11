package org.king.iohub.server.logic.room.domain.message;

import com.iohao.game.widget.light.domain.event.message.Eo;

public record UnexpectedExitEo(long userId) implements Eo {
}
