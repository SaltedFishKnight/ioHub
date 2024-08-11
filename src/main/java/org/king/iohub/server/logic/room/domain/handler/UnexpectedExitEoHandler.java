package org.king.iohub.server.logic.room.domain.handler;

import com.iohao.game.action.skeleton.core.CmdInfo;
import com.iohao.game.action.skeleton.core.commumication.BroadcastContext;
import com.iohao.game.action.skeleton.core.commumication.InvokeModuleContext;
import com.iohao.game.action.skeleton.protocol.wrapper.WrapperKit;
import com.iohao.game.bolt.broker.core.client.BrokerClientHelper;
import com.iohao.game.widget.light.domain.event.message.DomainEventHandler;
import org.king.iohub.common.route.LobbyCmd;
import org.king.iohub.common.route.RoomCmd;
import org.king.iohub.server.logic.room.combat.CombatRoom;
import org.king.iohub.server.logic.room.combat.CombatRoomService;
import org.king.iohub.server.logic.room.domain.message.UnexpectedExitEo;

import java.util.Collection;

public class UnexpectedExitEoHandler implements DomainEventHandler<UnexpectedExitEo> {

    @Override
    public void onEvent(UnexpectedExitEo event, boolean endOfBatch) {
        // 异常退出的用户ID
        long userId = event.userId();

        // 获取房间服务
        CombatRoomService combatRoomService = CombatRoomService.INSTANCE;

        // 获取用户所在房间
        CombatRoom combatRoom = combatRoomService.getRoomByUserId(userId);

        // 当双方都异常退出，则以第一个触发事件的用户作为异常用户
        if (combatRoom != null && combatRoom.hasUnexpectedExit.compareAndSet(false, true)) {
            // 获取用户所在房间的用户ID列表
            Collection<Long> listPlayerId = combatRoom.listPlayerId();

            // 通知房间内所有用户，存在异常用户
            CmdInfo userExceptionCmdInfo = CmdInfo.of(RoomCmd.cmd, RoomCmd.userException);
            BroadcastContext broadcastContext = BrokerClientHelper.getBroadcastContext();
            broadcastContext.broadcast(userExceptionCmdInfo, WrapperKit.of(userId), listPlayerId);

            // 正常用户被动地退出房间，返回大厅
            CmdInfo returnToLobbyPassivelyCmdInfo = CmdInfo.of(LobbyCmd.cmd, LobbyCmd.returnToLobbyPassively);
            InvokeModuleContext invokeModuleContext = BrokerClientHelper.getInvokeModuleContext();
            for (Long id : listPlayerId) {
                if (id != userId) {
                    invokeModuleContext.invokeModuleVoidMessage(returnToLobbyPassivelyCmdInfo, WrapperKit.of(id));
                }
            }

            // 解散房间
            combatRoomService.removeRoom(combatRoom);
        }
    }
}
