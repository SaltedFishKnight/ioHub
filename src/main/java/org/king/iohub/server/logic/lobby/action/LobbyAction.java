package org.king.iohub.server.logic.lobby.action;

import com.iohao.game.action.skeleton.annotation.ActionController;
import com.iohao.game.action.skeleton.annotation.ActionMethod;
import com.iohao.game.action.skeleton.core.BarMessageKit;
import com.iohao.game.action.skeleton.core.CmdInfo;
import com.iohao.game.action.skeleton.core.commumication.BroadcastContext;
import com.iohao.game.action.skeleton.core.commumication.InvokeModuleContext;
import com.iohao.game.action.skeleton.core.flow.FlowContext;
import com.iohao.game.action.skeleton.protocol.ResponseMessage;
import com.iohao.game.action.skeleton.protocol.wrapper.WrapperKit;
import com.iohao.game.bolt.broker.client.kit.ExternalCommunicationKit;
import com.iohao.game.bolt.broker.client.kit.UserIdSettingKit;
import com.iohao.game.bolt.broker.core.client.BrokerClientHelper;
import org.king.iohub.common.protobuf.lobby.LoginResponse;
import org.king.iohub.common.protobuf.lobby.RemoteUserInfo;
import org.king.iohub.common.route.LobbyCmd;
import org.king.iohub.common.route.RoomCmd;
import org.king.iohub.server.logic.lobby.internal.UserStateType;
import org.king.iohub.server.logic.lobby.singleton.AutoMatchProcessor;
import org.king.iohub.server.logic.lobby.singleton.UserStateHolder;

@ActionController(LobbyCmd.cmd)
public class LobbyAction {

    @ActionMethod(LobbyCmd.login)
    public LoginResponse login(long userId, FlowContext flowContext) {

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.userId = userId;

        // 查询该用户ID是否被占用
        loginResponse.existUser = ExternalCommunicationKit.existUser(userId);

        // 将连接的客户端绑定到指定的userId
        if (!loginResponse.existUser) {
            loginResponse.success = UserIdSettingKit.settingUserId(flowContext, userId);
        }

        // 用户进入大厅
        if (loginResponse.success) {
            UserStateHolder.INSTANCE.getUserStateMap().put(userId, UserStateType.LOBBY);
        }

        return loginResponse;
    }

    @ActionMethod(LobbyCmd.autoMatch)
    public boolean autoMatch(String variantId, FlowContext flowContext) {

        long userId = flowContext.getUserId();

        if (UserStateHolder.INSTANCE.getUserStateMap().replace(userId, UserStateType.LOBBY, UserStateType.QUEUE)) {
            // 构建用户信息，排队等待自动匹配
            RemoteUserInfo remoteUserInfo = new RemoteUserInfo();
            remoteUserInfo.userId = userId;
            remoteUserInfo.variantId = variantId;
            return AutoMatchProcessor.INSTANCE.getUserInfoQueue().offer(remoteUserInfo);
        }

        return false;
    }

    @ActionMethod(LobbyCmd.heartbeatLost)
    public boolean heartbeatLost(long userId) {
        // 通知客户端触发心跳超时回调
        BroadcastContext broadcastContext = BrokerClientHelper.getBroadcastContext();
        CmdInfo disconnectCmdInfo = CmdInfo.of(LobbyCmd.cmd, LobbyCmd.disconnect);
        ResponseMessage responseMessage = BarMessageKit.createResponseMessage(disconnectCmdInfo);
        broadcastContext.broadcast(responseMessage, userId);
        return true;
    }

    @ActionMethod(LobbyCmd.logout)
    public void logout(long userId) {
        UserStateType userState = UserStateHolder.INSTANCE.getUserStateMap().replace(userId, UserStateType.DEPARTURE);

        if (userState == UserStateType.ROOM) {
            CmdInfo unexpectedExitCmdInfo = CmdInfo.of(RoomCmd.cmd, RoomCmd.unexpectedExit);
            InvokeModuleContext invokeModuleContext = BrokerClientHelper.getInvokeModuleContext();
            invokeModuleContext.invokeModuleVoidMessage(unexpectedExitCmdInfo, WrapperKit.of(userId));
        }
    }

    @ActionMethod(LobbyCmd.endCombat)
    public void endCombat(FlowContext flowContext) {
        long userId = flowContext.getUserId();

        boolean isExpectedExit = UserStateHolder.INSTANCE.getUserStateMap().replace(userId, UserStateType.ROOM, UserStateType.LOBBY);

        if (isExpectedExit) {
            CmdInfo expectedExitCmdInfo = CmdInfo.of(RoomCmd.cmd, RoomCmd.expectedExit);
            flowContext.invokeModuleVoidMessage(expectedExitCmdInfo, WrapperKit.of(userId));
        }
    }

    @ActionMethod(LobbyCmd.retrunToLobbyActively)
    public void returnToLobbyActively(FlowContext flowContext) {
        long userId = flowContext.getUserId();

        boolean isReturned = UserStateHolder.INSTANCE.getUserStateMap().replace(userId, UserStateType.ROOM, UserStateType.LOBBY);

        if (isReturned) {
            CmdInfo unexpectedExitCmdInfo = CmdInfo.of(RoomCmd.cmd, RoomCmd.unexpectedExit);
            InvokeModuleContext invokeModuleContext = BrokerClientHelper.getInvokeModuleContext();
            invokeModuleContext.invokeModuleVoidMessage(unexpectedExitCmdInfo, WrapperKit.of(userId));
        }
    }

    @ActionMethod(LobbyCmd.returnToLobbyPassively)
    public void returnToLobbyPassively(long userId) {
        UserStateHolder.INSTANCE.getUserStateMap().replace(userId, UserStateType.ROOM, UserStateType.LOBBY);
    }

}
