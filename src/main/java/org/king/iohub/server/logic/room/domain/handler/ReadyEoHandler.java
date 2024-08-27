package org.king.iohub.server.logic.room.domain.handler;

import com.iohao.game.action.skeleton.core.CmdInfo;
import com.iohao.game.action.skeleton.core.commumication.BroadcastContext;
import com.iohao.game.action.skeleton.protocol.wrapper.WrapperKit;
import com.iohao.game.bolt.broker.core.client.BrokerClientHelper;
import com.iohao.game.widget.light.domain.event.message.DomainEventHandler;
import org.king.iohub.common.route.RoomCmd;
import org.king.iohub.server.logic.room.combat.CombatRoom;
import org.king.iohub.server.logic.room.combat.CombatRoomService;
import org.king.iohub.server.logic.room.domain.message.ReadyEo;

import java.time.Instant;

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

            Instant now = Instant.now();
            // 计算从 Unix Epoch 开始的纳秒数，并将其延后 1 秒
            long startTimeNano = now.getEpochSecond() * 1_000_000_000L + now.getNano() + 1_000_000_000L;

            broadcastContext.broadcast(allPlayersReadyCmdInfo, WrapperKit.of(startTimeNano), combatRoom.listPlayerId());
        }
    }
}
