package org.king.iohub.common.protobuf.room;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

@ProtobufClass
public class DefenseSystem {

    public boolean isTargetedShipOrClearedTarget;

    public boolean isFired;

    public boolean isHoldenFire;

    public boolean isToggledShieldOrPhaseCloak;

    public boolean isUsedSystem;

    public boolean isVentedFlux;

    public boolean isPulledBackFighters;

}