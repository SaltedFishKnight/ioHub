package org.king.iohub.server.logic.room.action;

import com.iohao.game.action.skeleton.annotation.ActionController;
import com.iohao.game.action.skeleton.annotation.ActionMethod;
import com.iohao.game.action.skeleton.core.CmdInfo;
import com.iohao.game.action.skeleton.core.commumication.BroadcastContext;
import com.iohao.game.action.skeleton.core.commumication.BroadcastOrderContext;
import com.iohao.game.action.skeleton.core.flow.FlowContext;
import com.iohao.game.bolt.broker.core.client.BrokerClientHelper;
import org.king.iohub.common.protobuf.lobby.AutoMatchResult;
import org.king.iohub.common.protobuf.lobby.RemoteUserInfo;
import org.king.iohub.common.protobuf.room.ActionWithId;
import org.king.iohub.common.protobuf.room.ShipAction;
import org.king.iohub.common.route.RoomCmd;
import org.king.iohub.server.logic.room.combat.CombatRoom;
import org.king.iohub.server.logic.room.combat.CombatRoomService;
import org.king.iohub.server.logic.room.combat.ShipPlayer;
import org.king.iohub.server.logic.room.domain.message.ExpectedExitEo;
import org.king.iohub.server.logic.room.domain.message.ReadyEo;
import org.king.iohub.server.logic.room.domain.message.UnexpectedExitEo;

@ActionController(RoomCmd.cmd)
public class RoomAction {

    @ActionMethod(RoomCmd.ready)
    public void ready(FlowContext flowContext) {
        long userId = flowContext.getUserId();
        new ReadyEo(userId).send();
    }

    @ActionMethod(RoomCmd.exchangeShipAction)
    public ShipAction exchangeShipAction(ShipAction producerShipAction, FlowContext flowContext) {
        long userId = flowContext.getUserId();
        CombatRoomService combatRoomService = CombatRoomService.INSTANCE;
        CombatRoom combatRoom = combatRoomService.getRoomByUserId(userId);
        if (combatRoom == null) {
            return new ShipAction();
        }
        ShipPlayer shipPlayer = combatRoom.getPlayerById(userId);

        try {
            shipPlayer.producerQueue.put(producerShipAction);
        } catch (Exception e) {
            throw new RuntimeException("生产 ShipAction 失败", e);
        }

        try {
            return shipPlayer.consumerQueue.take();
        } catch (Exception e) {
            throw new RuntimeException("消费 ShipAction 失败", e);
        }
    }

    @ActionMethod(RoomCmd.createRoomByAutoMatch)
    public boolean createRoomByAutoMatch(AutoMatchResult autoMatchResult) {

        // 取出玩家信息
        RemoteUserInfo remoteUserOneInfo = autoMatchResult.RemoteUserOneInfo;
        RemoteUserInfo remoteUserTwoInfo = autoMatchResult.RemoteUserTwoInfo;

        // 创建玩家
        ShipPlayer shipPlayerOne = new ShipPlayer();
        ShipPlayer shipPlayerTwo = new ShipPlayer();
        shipPlayerOne.setUserId(remoteUserOneInfo.userId);
        shipPlayerTwo.setUserId(remoteUserTwoInfo.userId);
        shipPlayerOne.consumerQueue = shipPlayerTwo.producerQueue;
        shipPlayerTwo.consumerQueue = shipPlayerOne.producerQueue;

        // 获取房间服务
        CombatRoomService combatRoomService = CombatRoomService.INSTANCE;

        // TODO 检测roomId是否已被占用，且roomId需要自动循环
        // 创建并添加房间
        long roomId = combatRoomService.roomIdCounter.getAndIncrement();
        CombatRoom combatRoom = new CombatRoom();
        combatRoom.setRoomId(roomId);
        combatRoomService.addRoom(combatRoom);

        // 玩家加入房间
        combatRoomService.addPlayer(combatRoom, shipPlayerOne);
        combatRoomService.addPlayer(combatRoom, shipPlayerTwo);

        // 交换玩家双方的信息
        CmdInfo pushRemoteUserInfoCmdInfo = CmdInfo.of(RoomCmd.cmd, RoomCmd.pushRemoteUserInfo);
        BroadcastContext broadcastContext = BrokerClientHelper.getBroadcastContext();
        broadcastContext.broadcast(pushRemoteUserInfoCmdInfo, remoteUserOneInfo, remoteUserTwoInfo.userId);
        broadcastContext.broadcast(pushRemoteUserInfoCmdInfo, remoteUserTwoInfo, remoteUserOneInfo.userId);

        // 通知大厅逻辑服，跨服调用完成
        return true;
    }

    @ActionMethod(RoomCmd.unexpectedExit)
    public void unexpectedExit(long userId) {
        new UnexpectedExitEo(userId).send();
    }


    @ActionMethod(RoomCmd.expectedExit)
    public void expectedExit(long userId) {
        new ExpectedExitEo(userId).send();
    }

    @ActionMethod(RoomCmd.uploadShipAction)
    public void uploadShipAction(ActionWithId actionWithId) {
        CmdInfo pushShipActionCmdInfo = CmdInfo.of(RoomCmd.cmd, RoomCmd.pushShipAction);
        BroadcastOrderContext broadcastOrderContext = BrokerClientHelper.getBroadcastOrderContext();
        broadcastOrderContext.broadcastOrder(pushShipActionCmdInfo, actionWithId.shipAction, actionWithId.remoteUserId);
    }

}
