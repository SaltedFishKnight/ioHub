package org.king.iohub.server.logic.room.combat;

import com.iohao.game.widget.light.room.SimplePlayer;
import org.king.iohub.common.protobuf.room.ShipAction;

import java.util.concurrent.LinkedBlockingQueue;

public class ShipPlayer extends SimplePlayer {

    public LinkedBlockingQueue<ShipAction> producerQueue = new LinkedBlockingQueue<>();

    public LinkedBlockingQueue<ShipAction> consumerQueue;

}
