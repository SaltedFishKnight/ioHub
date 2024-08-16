package org.king.iohub.common.protobuf.room;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

@ProtobufClass
public class Movement {

    public boolean isAccelerated;

    public boolean isAcceleratedBackwards;

    public boolean isDecelerated;

    public boolean isTurnedLeft;

    public boolean isTurnedRight;

    public boolean isStrafedLeft;

    public boolean isStrafedRight;

}
