package org.king.iohub.server.logic.room.domain.message;

import com.iohao.game.widget.light.domain.event.message.Eo;

public record ExpectedExitEo(long userId) implements Eo {
}
