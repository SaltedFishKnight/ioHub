package org.king.iohub;

import com.iohao.game.bolt.broker.client.AbstractBrokerClientStartup;
import com.iohao.game.bolt.broker.core.client.BrokerAddress;
import com.iohao.game.bolt.broker.core.common.IoGameGlobalConfig;
import com.iohao.game.bolt.broker.server.BrokerServer;
import com.iohao.game.common.kit.NetworkKit;
import com.iohao.game.external.core.ExternalServer;
import com.iohao.game.external.core.netty.simple.NettyRunOne;
import lombok.extern.slf4j.Slf4j;
import org.king.iohub.server.broker.IoHubBrokerServer;
import org.king.iohub.server.external.IoHubExternalServer;
import org.king.iohub.server.logic.lobby.LobbyLogicServer;
import org.king.iohub.server.logic.room.RoomLogicServer;
import org.king.iohub.server.logic.room.domain.RoomDomainEvent;

import java.io.PrintStream;
import java.util.List;

@Slf4j
public class IoHubApplication {

    public static void main(String[] args) {

        // 创建一个无缓冲的 PrintStream 实例
        PrintStream noBufferOut = new PrintStream(System.out, true);
        // 将 System.out 重定向到无缓冲的 PrintStream
        System.setOut(noBufferOut);

        // 开启 traceId 特性
        IoGameGlobalConfig.openTraceId = true;
        // 开启请求响应相关日志
        IoGameGlobalConfig.requestResponseLog = true;
        // 开启对外服相关日志
        IoGameGlobalConfig.externalLog = true;
        // 开启广播相关日志
        IoGameGlobalConfig.broadcastLog = true;

        // 构建网关地址
        int externalPort = Integer.parseInt(System.getProperty("externalPort"));
        int brokerPort = Integer.parseInt(System.getProperty("brokerPort"));
        BrokerAddress brokerAddress = new BrokerAddress(NetworkKit.LOCAL_IP, brokerPort);

        // 构建对外服
        ExternalServer externalServer = new IoHubExternalServer(externalPort, brokerAddress).createExternalServer();

        // 构建网关服
        BrokerServer brokerServer = new IoHubBrokerServer(brokerPort).createBrokerServer();

        // 构建逻辑服
        List<AbstractBrokerClientStartup> logicServerList = List.of(
                new LobbyLogicServer(brokerAddress),
                new RoomLogicServer(brokerAddress)
        );

        // 启动服务器
        new NettyRunOne()
                .setExternalServer(externalServer)
                .setBrokerServer(brokerServer)
                .setLogicServerList(logicServerList)
                .startup();

        // 启动领域事件
        RoomDomainEvent.launch();

        log.info("IoHub 相关连接信息：" +
                        "{}" +
                        "\t对外服地址：{}" +
                        "{}" +
                        "\t对外服端口：{}",
                System.lineSeparator(),
                NetworkKit.LOCAL_IP,
                System.lineSeparator(),
                externalPort
        );
    }
}
