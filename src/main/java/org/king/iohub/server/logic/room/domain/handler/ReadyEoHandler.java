package org.king.iohub.server.logic.room.domain.handler;

import com.iohao.game.action.skeleton.core.BarMessageKit;
import com.iohao.game.action.skeleton.core.CmdInfo;
import com.iohao.game.action.skeleton.core.commumication.BroadcastContext;
import com.iohao.game.action.skeleton.protocol.ResponseMessage;
import com.iohao.game.bolt.broker.core.client.BrokerClientHelper;
import com.iohao.game.widget.light.domain.event.message.DomainEventHandler;
import org.king.iohub.common.route.RoomCmd;
import org.king.iohub.server.logic.room.combat.CombatRoom;
import org.king.iohub.server.logic.room.combat.CombatRoomService;
import org.king.iohub.server.logic.room.domain.message.ReadyEo;

public class ReadyEoHandler implements DomainEventHandler<ReadyEo> {

    @Override
    public void onEvent(ReadyEo event, boolean endOfBatch) {
        // 准备的用户ID
        long userId = event.userId();

        // 获取用户所在房间
        CombatRoom combatRoom = CombatRoomService.INSTANCE.getRoomByUserId(userId);

        // 用户准备
        combatRoom.getPlayerById(userId).setReady(true);

        if (combatRoom.isReadyPlayers() && combatRoom.isCombatStarted.compareAndSet(false, true)) {
            CmdInfo allPlayersReadyCmdInfo = CmdInfo.of(RoomCmd.cmd, RoomCmd.allPlayersReady);
            BroadcastContext broadcastContext = BrokerClientHelper.getBroadcastContext();
            ResponseMessage responseMessage = BarMessageKit.createResponseMessage(allPlayersReadyCmdInfo);
            broadcastContext.broadcast(responseMessage, combatRoom.listPlayerId());
        }
    }
}
