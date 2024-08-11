package org.king.iohub.common.protobuf.room;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

import java.util.List;

@ProtobufClass
public class ShipAction {

    public List<String> commandList;

    public MouseLocation mouseLocation;

    public WeaponGroupState weaponGroupState;

    public float shipFacing;

    public int frameIndex;

    public boolean isShiftPressed;

}
