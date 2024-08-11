package org.king.iohub.server.external.hook;

import com.iohao.game.action.skeleton.core.CmdInfo;
import com.iohao.game.action.skeleton.core.commumication.InvokeModuleContext;
import com.iohao.game.action.skeleton.protocol.wrapper.WrapperKit;
import com.iohao.game.bolt.broker.core.client.BrokerClientHelper;
import com.iohao.game.external.core.netty.hook.SocketIdleHook;
import com.iohao.game.external.core.session.UserSession;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.king.iohub.common.route.LobbyCmd;

@Slf4j
public class DisconnectSocketIdleHook implements SocketIdleHook {

    @Override
    public boolean callback(UserSession userSession, IdleStateEvent idleStateEvent) {
        // 通知大厅逻辑服，该用户断开连接，同步调用，忽略返回结果
        CmdInfo heartbeatLostCmdInfo = CmdInfo.of(LobbyCmd.cmd, LobbyCmd.heartbeatLost);
        InvokeModuleContext invokeModuleContext = BrokerClientHelper.getInvokeModuleContext();
        invokeModuleContext.invokeModuleMessage(heartbeatLostCmdInfo, WrapperKit.of(userSession.getUserId()));

        log.info("通知框架关闭以下用户的连接" +
                        "{}" +
                        "\t用户ID：[{}]" +
                        "{}" +
                        "\t用户IP地址：{}" +
                        "{}" +
                        "\t用户状态：{}",
                System.lineSeparator(),
                userSession.getUserId(),
                System.lineSeparator(),
                userSession.getIp(),
                System.lineSeparator(),
                userSession.getState()
        );

        // 由心跳事件通知框架关闭当前用户的连接
        return true;
    }
}
