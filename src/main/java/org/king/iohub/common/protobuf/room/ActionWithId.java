package org.king.iohub.common.protobuf.room;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

@ProtobufClass
public class ActionWithId {

    public long remoteUserId;

    public ShipAction shipAction;

}