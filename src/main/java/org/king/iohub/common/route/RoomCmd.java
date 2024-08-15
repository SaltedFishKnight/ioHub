package org.king.iohub.common.route;

public interface RoomCmd {

    /**
     * 房间逻辑服的主路由
     */
    int cmd = CmdModule.roomCmd;


    // User to Room

    /**
     * 用户准备
     */
    int ready = 0;

    /**
     * 上传一帧之内，飞船的所有行为
     */
    int uploadShipAction = 8;


    // Lobby to Room

    /**
     * 通过自动匹配创建房间
     */
    int createRoomByAutoMatch = 2;

    /**
     * 玩家异常退出
     */
    int unexpectedExit = 3;

    /**
     * 玩家正常退出
     */
    int expectedExit = 4;


    // Room to User

    /**
     * 通知用户发生异常
     */
    int userException = 5;

    /**
     * 推送玩家信息
     */
    int pushRemoteUserInfo = 6;

    /**
     * 所有玩家已准备
     */
    int allPlayersReady = 7;

    /**
     * 推送一帧之内，飞船的所有行为
     */
    int pushShipAction = 9;

}
