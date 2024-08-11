package org.king.iohub.server.logic.lobby.singleton;

import com.iohao.game.action.skeleton.core.CmdInfo;
import com.iohao.game.action.skeleton.core.commumication.InvokeModuleContext;
import com.iohao.game.action.skeleton.protocol.wrapper.WrapperKit;
import com.iohao.game.bolt.broker.core.client.BrokerClientHelper;
import lombok.Getter;
import org.jctools.maps.NonBlockingHashMapLong;
import org.jctools.queues.MpscBlockingConsumerArrayQueue;
import org.jctools.queues.SpscArrayQueue;
import org.king.iohub.common.protobuf.lobby.AutoMatchResult;
import org.king.iohub.common.protobuf.lobby.RemoteUserInfo;
import org.king.iohub.common.route.RoomCmd;
import org.king.iohub.server.logic.lobby.internal.UserStateType;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;

public enum AutoMatchProcessor {

    INSTANCE;

    @Getter
    private final BlockingQueue<RemoteUserInfo> UserInfoQueue = new MpscBlockingConsumerArrayQueue<>(1024);

    private final InternalProcess internalProcess = new InternalProcess();

    private class InternalProcess {

        Queue<RemoteUserInfo> buffer = new SpscArrayQueue<>(1);

        InternalProcess() {
            new Thread(() -> {
                try {
                    while (true) {
                        RemoteUserInfo userInfo = UserInfoQueue.take();
                        consume(userInfo);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }, "AutoMatchRequestConsumer").start();
        }

        void consume(RemoteUserInfo userOneInfo) {

            if (buffer.isEmpty()) {
                buffer.offer(userOneInfo);
                return;
            }

            RemoteUserInfo userTwoInfo = buffer.poll();

            NonBlockingHashMapLong<UserStateType> userStateMap = UserStateHolder.INSTANCE.getUserStateMap();

            UserStateType userOneState = userStateMap.get(userOneInfo.userId);
            UserStateType userTwoState = userStateMap.get(userTwoInfo.userId);

            if (userOneState == UserStateType.QUEUE && userTwoState == UserStateType.QUEUE) {
                // 构建自动匹配结果
                AutoMatchResult autoMatchResult = new AutoMatchResult();
                autoMatchResult.RemoteUserOneInfo = userOneInfo;
                autoMatchResult.RemoteUserTwoInfo = userTwoInfo;

                // 创建房间，同步调用，等待调用结束，但忽略返回结果
                CmdInfo createRoomByAutoMatchCmdInfo = CmdInfo.of(RoomCmd.cmd, RoomCmd.createRoomByAutoMatch);
                InvokeModuleContext invokeModuleContext = BrokerClientHelper.getInvokeModuleContext();
                invokeModuleContext.invokeModuleMessage(createRoomByAutoMatchCmdInfo, autoMatchResult);

                // 尝试变更状态
                boolean isUserOneChanged = userStateMap.replace(userOneInfo.userId, UserStateType.QUEUE, UserStateType.ROOM);
                boolean isUserTwoChanged = userStateMap.replace(userTwoInfo.userId, UserStateType.QUEUE, UserStateType.ROOM);

                CmdInfo unexpectedExitCmdInfo = CmdInfo.of(RoomCmd.cmd, RoomCmd.unexpectedExit);
                if (!isUserOneChanged) {
                    invokeModuleContext.invokeModuleVoidMessage(unexpectedExitCmdInfo, WrapperKit.of(userOneInfo.userId));
                    userStateMap.replace(userTwoInfo.userId, UserStateType.ROOM, UserStateType.LOBBY);
                }
                if (!isUserTwoChanged) {
                    invokeModuleContext.invokeModuleVoidMessage(unexpectedExitCmdInfo, WrapperKit.of(userTwoInfo.userId));
                    userStateMap.replace(userOneInfo.userId, UserStateType.ROOM, UserStateType.LOBBY);
                }
            } else if (userOneState == UserStateType.QUEUE) {
                buffer.offer(userOneInfo);
            } else if (userTwoState == UserStateType.QUEUE) {
                buffer.offer(userTwoInfo);
            }
        }
    }
}
