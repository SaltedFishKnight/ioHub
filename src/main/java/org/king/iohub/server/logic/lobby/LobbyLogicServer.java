package org.king.iohub.server.logic.lobby;

import com.iohao.game.action.skeleton.core.BarSkeleton;
import com.iohao.game.action.skeleton.core.BarSkeletonBuilder;
import com.iohao.game.action.skeleton.core.BarSkeletonBuilderParamConfig;
import com.iohao.game.action.skeleton.core.flow.FlowContext;
import com.iohao.game.action.skeleton.core.flow.internal.DebugInOut;
import com.iohao.game.action.skeleton.core.flow.internal.StatActionInOut;
import com.iohao.game.action.skeleton.core.flow.internal.ThreadMonitorInOut;
import com.iohao.game.action.skeleton.core.flow.internal.TraceIdInOut;
import com.iohao.game.bolt.broker.client.AbstractBrokerClientStartup;
import com.iohao.game.bolt.broker.core.client.BrokerAddress;
import com.iohao.game.bolt.broker.core.client.BrokerClient;
import com.iohao.game.bolt.broker.core.client.BrokerClientBuilder;
import com.iohao.game.common.kit.ExecutorKit;
import lombok.extern.slf4j.Slf4j;
import org.king.iohub.server.logic.lobby.action.LobbyAction;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LobbyLogicServer extends AbstractBrokerClientStartup {

    private final BrokerAddress brokerAddress;

    public LobbyLogicServer(BrokerAddress brokerAddress) {
        this.brokerAddress = brokerAddress;
    }

    @Override
    public BarSkeleton createBarSkeleton() {
        // 业务框架构建器的配置
        BarSkeletonBuilderParamConfig config = new BarSkeletonBuilderParamConfig()
                // 扫描 LobbyAction.class 所在包
                .scanActionPackage(LobbyAction.class);

        // 业务框架构建器
        BarSkeletonBuilder builder = config.createBuilder();

        // 添加插件
        addInOutPlugins(builder);

        return builder.build();

    }

    @Override
    public BrokerClientBuilder createBrokerClientBuilder() {
        BrokerClientBuilder builder = BrokerClient.newBuilder();
        // 逻辑服信息
        builder.appName("大厅逻辑服");
        return builder;
    }

    @Override
    public BrokerAddress createBrokerAddress() {
        // 游戏网关地址
        return brokerAddress;
    }

    private void addInOutPlugins(BarSkeletonBuilder builder) {
        // DebugInOut：业务执行时长、参数与返回值的观察、业务代码定位
        DebugInOut debugInOut = new DebugInOut();
        builder.addInOut(debugInOut);

        // 全链路调用日志跟踪插件
        TraceIdInOut traceIdInOut = new TraceIdInOut();
        builder.addInOut(traceIdInOut);

        // 业务线程监控插件
        ThreadMonitorInOut threadMonitorInOut = new ThreadMonitorInOut();
        builder.addInOut(threadMonitorInOut);

        ThreadMonitorInOut.ThreadMonitorRegion region = threadMonitorInOut.getRegion();
        ExecutorKit.newSingleScheduled("大厅逻辑服-业务线程监控")
                .scheduleAtFixedRate(() -> log.info(region.toString()), 3, 3, TimeUnit.MINUTES);

        // action 调用统计插件
        StatActionInOut statActionInOut = new StatActionInOut();
        builder.addInOut(statActionInOut);

        statActionInOut.setListener(new StatActionInOut.StatActionChangeListener() {
            @Override
            public void changed(StatActionInOut.StatAction statAction, long time, FlowContext flowContext) {
                if (time > 20) {
                    log.info(statAction.toString());
                }
            }

            @Override
            public List<StatActionInOut.TimeRange> createTimeRangeList() {
                return List.of(
                        StatActionInOut.TimeRange.create(0, 10),
                        StatActionInOut.TimeRange.create(10, 20),
                        StatActionInOut.TimeRange.create(20, 50),
                        StatActionInOut.TimeRange.create(50, 100),
                        StatActionInOut.TimeRange.create(100, 500),
                        StatActionInOut.TimeRange.create(500, 1000),
                        StatActionInOut.TimeRange.create(1000, Long.MAX_VALUE, "> 1000"));
            }

            @Override
            public boolean triggerUpdateTimeRange(StatActionInOut.StatAction statAction, long time, FlowContext flowContext) {
                return time >= 0;
            }
        });
    }
}
