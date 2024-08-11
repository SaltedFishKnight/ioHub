package org.king.iohub.server.logic.room.domain;

import com.iohao.game.widget.light.domain.event.DomainEventContext;
import com.iohao.game.widget.light.domain.event.DomainEventContextParam;
import lombok.extern.slf4j.Slf4j;
import org.king.iohub.server.logic.room.domain.handler.ExpectedExitEoHandler;
import org.king.iohub.server.logic.room.domain.handler.ReadyEoHandler;
import org.king.iohub.server.logic.room.domain.handler.UnexpectedExitEoHandler;

@Slf4j
public class RoomDomainEvent {

    private static DomainEventContext domainEventContext;

    public static void launch() {

        // 领域事件上下文参数
        DomainEventContextParam contextParam = new DomainEventContextParam();

        // 为领域事件配置处理器
        // 对应关系 xxxEo（Topic）-> Disruptor（RingBuffer）-> EventHandlers<xxxEo>
        contextParam.addEventHandler(new UnexpectedExitEoHandler());
        contextParam.addEventHandler(new ExpectedExitEoHandler());
        contextParam.addEventHandler(new ReadyEoHandler());

        // 启动事件驱动
        domainEventContext = new DomainEventContext(contextParam);
        boolean startup = domainEventContext.startup();
        log.info("房间逻辑服的领域事件启动状态：{}", startup);

    }

    public static void stop() {
        domainEventContext.stop();
    }

}
