package org.king.iohub.server.logic.room.domain.handler;

import com.iohao.game.widget.light.domain.event.message.DomainEventHandler;
import org.king.iohub.server.logic.room.combat.CombatRoom;
import org.king.iohub.server.logic.room.combat.CombatRoomService;
import org.king.iohub.server.logic.room.domain.message.ExpectedExitEo;

public class ExpectedExitEoHandler implements DomainEventHandler<ExpectedExitEo> {

    @Override
    public void onEvent(ExpectedExitEo event, boolean endOfBatch) {
        // 正常退出的用户ID
        long userId = event.userId();

        // 获取房间服务
        CombatRoomService combatRoomService = CombatRoomService.INSTANCE;

        // 获取用户所在房间
        CombatRoom combatRoom = combatRoomService.getRoomByUserId(userId);

        // 最后一个触发正常退出事件的用户来移除房间
        if (combatRoom != null && combatRoom.expectedExitNum.incrementAndGet() == 2) {
            combatRoomService.removeRoom(combatRoom);
        }
    }
}
