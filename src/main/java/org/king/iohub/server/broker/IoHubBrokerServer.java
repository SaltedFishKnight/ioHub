package org.king.iohub.server.broker;

import com.iohao.game.bolt.broker.server.BrokerServer;
import com.iohao.game.bolt.broker.server.BrokerServerBuilder;

public class IoHubBrokerServer {

    private final int brokerPort;

    public IoHubBrokerServer(int brokerPort) {
        this.brokerPort = brokerPort;
    }

    public BrokerServer createBrokerServer() {
        // 网关服构建器
        BrokerServerBuilder brokerServerBuilder = BrokerServer.newBuilder()
                // 网关服端口
                .port(brokerPort);

        // 构建网关服
        return brokerServerBuilder.build();
    }
}
