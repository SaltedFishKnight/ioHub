package org.king.iohub.common.route;

public interface LobbyCmd {

    /**
     * 大厅逻辑服的主路由
     */
    int cmd = CmdModule.lobbyCmd;


    // User to Lobby

    /**
     * 用户登录
     */
    int login = 0;

    /**
     * 自动匹配
     */
    int autoMatch = 1;

    /**
     * 正常结束战斗
     */
    int endCombat = 2;

    /**
     * 主动地退出房间，回到大厅
     */
    int retrunToLobbyActively = 6;


    // External to Lobby

    /**
     * 用户登出
     */
    int logout = 3;

    /**
     * 对外服丢失用户心跳
     */
    int heartbeatLost = 4;


    // Lobby to User

    /**
     * 通知用户断线
     */
    int disconnect = 5;


    // Room to Lobby

    /**
     * 被动地退出房间，回到大厅
     */
    int returnToLobbyPassively = 7;

}
