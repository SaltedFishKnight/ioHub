package org.king.iohub.common.route;

public interface CmdModule {

    // 保留路由
    int debugCmd = 0;

    /**
     * 大厅模块
     */
    int lobbyCmd = 1;


    /**
     * 房间模块
     */
    int roomCmd = 2;
}
