package org.king.iohub.server.logic.room.combat;

import com.iohao.game.widget.light.room.SimpleRoom;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CombatRoom extends SimpleRoom {

    public CombatRoom() {
        this.isCombatStarted = new AtomicBoolean(false);
        this.expectedExitNum = new AtomicInteger(0);
        this.hasUnexpectedExit = new AtomicBoolean(false);
    }

    // 是否已经开始战斗
    public AtomicBoolean isCombatStarted;

    // 正常结束的玩家数量
    public AtomicInteger expectedExitNum;

    // 是否有玩家异常退出
    public AtomicBoolean hasUnexpectedExit;

}
