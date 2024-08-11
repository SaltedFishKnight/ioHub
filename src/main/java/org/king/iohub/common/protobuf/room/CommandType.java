package org.king.iohub.common.protobuf.room;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

@ProtobufClass
public enum CommandType {
    TURN_LEFT,

    TURN_RIGHT,

    STRAFE_LEFT,

    STRAFE_RIGHT,

    ACCELERATE,

    ACCELERATE_BACKWARDS,

    DECELERATE,

    SELECT_GROUP,

    TOGGLE_AUTOFIRE,

    FIRE,

    VENT_FLUX,

    TOGGLE_SHIELD_OR_PHASE_CLOAK,

    HOLD_FIRE,

    PULL_BACK_FIGHTERS,

    USE_SYSTEM,

    TARGET_SHIP_OR_CLEAR_TARGET

}
