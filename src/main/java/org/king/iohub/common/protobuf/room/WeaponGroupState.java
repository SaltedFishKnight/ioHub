package org.king.iohub.common.protobuf.room;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

import java.util.List;

@ProtobufClass
public class WeaponGroupState {

    public int curGroupIndex;

    public List<Boolean> autofireState;

}
