package org.king.iohub.common.protobuf.lobby;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

@ProtobufClass
public class LoginResponse {

    public long userId;

    public boolean existUser;

    public boolean success;
}
