package org.king.iohub.server.logic.lobby.singleton;

import lombok.Getter;
import org.jctools.maps.NonBlockingHashMapLong;
import org.king.iohub.server.logic.lobby.internal.UserStateType;


public enum UserStateHolder {

    INSTANCE;

    @Getter
    private final NonBlockingHashMapLong<UserStateType> userStateMap = new NonBlockingHashMapLong<>();

}
