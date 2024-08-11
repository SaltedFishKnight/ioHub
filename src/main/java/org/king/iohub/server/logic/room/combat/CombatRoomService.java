package org.king.iohub.server.logic.room.combat;

import com.iohao.game.widget.light.room.Room;
import com.iohao.game.widget.light.room.RoomService;
import org.jctools.maps.NonBlockingHashMapLong;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public enum CombatRoomService implements RoomService {

    INSTANCE;

    /**
     * 房间 map
     * <pre>
     *     key : roomId
     *     value : room
     * </pre>
     */
    private final Map<Long, Room> roomMap = new NonBlockingHashMapLong<>();

    /**
     * 玩家对应的房间 map
     * <pre>
     *     key : userId
     *     value : roomId
     * </pre>
     */
    private final Map<Long, Long> userRoomMap = new NonBlockingHashMapLong<>();

    public final AtomicLong roomIdCounter = new AtomicLong(1);

    @Override
    public Map<Long, Room> getRoomMap() {
        return roomMap;
    }

    @Override
    public Map<Long, Long> getUserRoomMap() {
        return userRoomMap;
    }

}
