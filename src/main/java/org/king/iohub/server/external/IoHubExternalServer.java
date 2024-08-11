package org.king.iohub.server.external;

import com.iohao.game.bolt.broker.core.client.BrokerAddress;
import com.iohao.game.external.core.ExternalServer;
import com.iohao.game.external.core.config.ExternalJoinEnum;
import com.iohao.game.external.core.hook.internal.IdleProcessSetting;
import com.iohao.game.external.core.netty.DefaultExternalCoreSetting;
import com.iohao.game.external.core.netty.DefaultExternalServer;
import com.iohao.game.external.core.netty.DefaultExternalServerBuilder;
import org.king.iohub.server.external.hook.DisconnectSocketIdleHook;
import org.king.iohub.server.external.hook.IoHubUserHook;

public class IoHubExternalServer {

    private final int externalPort;

    private final BrokerAddress brokerAddress;

    public IoHubExternalServer(int externalPort, BrokerAddress brokerAddress) {
        this.externalPort = externalPort;
        this.brokerAddress = brokerAddress;
    }

    public ExternalServer createExternalServer() {
        // 对外服构建器
        DefaultExternalServerBuilder builder = DefaultExternalServer
                // 对外服端口
                .newBuilder(externalPort)
                // 对外服连接方式
                .externalJoinEnum(ExternalJoinEnum.WEBSOCKET)
                // 与网关服的连接地址
                .brokerAddress(brokerAddress);

        // 得到 setting 对象（开发者可根据自身业务做扩展）
        DefaultExternalCoreSetting setting = builder.setting();

        // 设置用户上线、下线的钩子
        userHook(setting);

        // 心跳设置
        idle(setting);

        // 构建对外服
        return builder.build();
    }

    private void userHook(DefaultExternalCoreSetting setting) {
        setting.setUserHook(new IoHubUserHook());
    }

    private void idle(DefaultExternalCoreSetting setting) {
        // 创建 IdleProcessSetting，用于心跳相关的设置
        IdleProcessSetting idleProcessSetting = new IdleProcessSetting()
                // 心跳的最长间隔时间为 10 秒
                .setIdleTime(10)
                // 收到客户端心跳ping，响应心跳pong给客户端
                .setPong(true)
                // 客户端断线时，触发钩子
                .setIdleHook(new DisconnectSocketIdleHook());

        setting.setIdleProcessSetting(idleProcessSetting);
    }
}
