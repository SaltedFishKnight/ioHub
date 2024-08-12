package org.king.iohub.server.external.hook;

import com.iohao.game.action.skeleton.core.CmdInfo;
import com.iohao.game.action.skeleton.core.commumication.InvokeModuleContext;
import com.iohao.game.action.skeleton.protocol.wrapper.WrapperKit;
import com.iohao.game.bolt.broker.core.client.BrokerClientHelper;
import com.iohao.game.external.core.hook.UserHook;
import com.iohao.game.external.core.session.UserSession;
import lombok.extern.slf4j.Slf4j;
import org.king.iohub.common.route.LobbyCmd;

@Slf4j
public class IoHubUserHook implements UserHook {

    @Override
    public void into(UserSession userSession) {
        log.info("用户登录成功" +
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
        log.info("User login successfully" +
                        "{}" +
                        "\tUser ID: [{}]" +
                        "{}" +
                        "\tUser IP: {}" +
                        "{}" +
                        "\tUser State: {}",
                System.lineSeparator(),
                userSession.getUserId(),
                System.lineSeparator(),
                userSession.getIp(),
                System.lineSeparator(),
                userSession.getState()
        );
    }

    @Override
    public void quit(UserSession userSession) {
        // 通知大厅逻辑服用户下线，取消匹配，解散房间
        CmdInfo logoutCmdInfo = CmdInfo.of(LobbyCmd.cmd, LobbyCmd.logout);
        InvokeModuleContext invokeModuleContext = BrokerClientHelper.getInvokeModuleContext();
        invokeModuleContext.invokeModuleVoidMessage(logoutCmdInfo, WrapperKit.of(userSession.getUserId()));

        log.info("用户注销成功" +
                        "{}" +
                        "\tUser ID: [{}]" +
                        "{}" +
                        "\tUser IP: {}" +
                        "{}" +
                        "\tUser State: {}",
                System.lineSeparator(),
                userSession.getUserId(),
                System.lineSeparator(),
                userSession.getIp(),
                System.lineSeparator(),
                userSession.getState()
        );
        log.info("User logout successfully" +
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
    }
}
