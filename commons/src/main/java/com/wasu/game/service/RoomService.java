package com.wasu.game.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.wasu.game.domain.PlayerST;
import com.wasu.game.domain.entity.Room;
import com.wasu.game.domain.RoomInf;
import com.wasu.game.tools.caches.RedisCache;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/6/9.
 */
public class RoomService {

    private static Logger logger = Logger.getLogger(RoomService.class);


    /**
     * 加入房间
     *
     * @param playerId
     * @return
     */
    public static boolean putRoom(long roomId, long playerId, Room r) throws Exception {
        boolean success = false;
        RoomInf room = getRoom(roomId);
        if (room != null && room.getPlayers().size() > 0) {
            // 已存在该房间则直接加入
            if (room.getPlayers().size() < room.getPlayerNum()) {
                if (getRoomUserState(roomId, playerId) != null)//已进入
                    return true;
                PlayerST player = new PlayerST(playerId);
                player.setPosition(room.getPosistions().poll());
                room.getPlayers().add(player);
                success = true;
                setRoom(room);
            }
        } else {
            // 新建房间加入room
            if (room == null)
                room = new RoomInf(r);
            PlayerST player = new PlayerST(playerId);
            player.setPosition(room.getPosistions().poll());
            room.getPlayers().add(player);
            success = true;
            setRoom(room);
        }
        return success;
    }


    /**
     * 判断房间用户是否都准备
     *
     * @return
     */
    public static boolean isRoomAllReady(long roomId) {
        RoomInf room = getRoom(roomId);
        boolean success = false;
        if (room != null && room.getPlayers().size() == room.getPlayerNum()) {
            success = true;
            for (PlayerST i : room.getPlayers()) {
                if (!i.getState()) {
                    success = false;
                    break;
                }
            }
        }
        return success;
    }

    /**
     * 根据id获取位置
     *
     * @return
     */
    public static Integer getPositionById(long roomId, long userId) {
        RoomInf room = getRoom(roomId);
        for (PlayerST p : room.getPlayers()) {
            if (p.getPlayerId() == userId)
                return p.getPosition();
        }
        return null;
    }

    /**
     * 用户准备与取消
     *
     * @param playerId
     * @return
     */
    public static boolean userReady(long roomId, long playerId) {
        RoomInf room = getRoom(roomId);
        boolean success = false;
        if (room != null) {
            for (PlayerST i : room.getPlayers()) {
                if (i.getPlayerId() == playerId) {
                    i.setState(!i.getState());//
                    success = true;
                    break;
                }
            }
        }
        setRoom(room);
        return success;
    }

    /**
     * 用户准备与取消
     *
     * @param playerId
     * @return
     */
    public static PlayerST getRoomUser(long roomId, long playerId) {
        RoomInf room = getRoom(roomId);
        PlayerST st = null;
        boolean success = false;
        if (room != null) {
            for (PlayerST i : room.getPlayers()) {
                if (i.getPlayerId() == playerId) {
                    st = i;
                    break;
                }
            }
        }
        return st;
    }

    /**
     * 移除
     * 返回是否删除房间
     *
     * @param playerId
     */
    public static Boolean removePlayer(long roomId, long playerId) {
        Boolean is = false;
        RoomInf room = getRoom(roomId);
        if (room == null) {
            return true;
        }
        for (PlayerST r : room.getPlayers()) {
            if (r.getPlayerId() == playerId) {
                //将该用户位置返还房间
                room.getPosistions().add(r.getPosition());
                room.getPlayers().remove(r);
                is = true;
                break;
            }
        }
        if (room.getPlayers().size() == 0) {
            delRoom(roomId);
            is = true;
        } else {
            setRoom(room);
        }
        return is;
    }

    /**
     * 获得房间用户playerId
     *
     * @param roomId
     */
    public static List<PlayerST> getRoomInf(long roomId) {
        RoomInf room = getRoom(roomId);
        if (room != null) {
            return room.getPlayers();
        }
        return null;
    }

    public static List<Long> getRoomUserIds(long roomId) {
        RoomInf room = getRoom(roomId);
        List<Long> userIds = new ArrayList<Long>();
        if (room != null && room.getPlayers() != null) {
            userIds.addAll(Collections2.transform(room.getPlayers(), new Function<PlayerST, Long>() {
                public Long apply(PlayerST playerST) {
                    return playerST.getPlayerId();
                }
            }));
        }
        return userIds;
    }


    public static Boolean getRoomUserState(long roomId, long playerId) {
        RoomInf room = getRoom(roomId);
        Boolean state = null;
        for (PlayerST i : room.getPlayers()) {
            if (i.getPlayerId() == playerId) {
                state = i.getState();
                break;
            }
        }
        return state;
    }

    @SuppressWarnings("unchecked")
    public static RoomInf getRoom(long roomId) {
        RoomInf res = null;
        String str = "";
        try {
            str = RedisCache.getHash("rooms", roomId + "");
            if (!StringUtils.isEmpty(str)) {
                res = JSONObject.parseObject(str, RoomInf.class);
            } else {
                logger.info("jsonString is empty....roomId:" + roomId);
            }
        } catch (Exception e) {
            logger.error("getRoom", e);
            logger.info("roomId：" + roomId + "; str:" + str);
            return null;
        }
        return res;
    }

    public static Long setRoom(RoomInf room) {
        Long str = null;
        try {
            String jsonString = JSON.toJSONString(room, SerializerFeature.DisableCircularReferenceDetect);
            str = RedisCache.setHash("rooms", room.getRoomId() + "", jsonString); //setValue("roomInf" + room.getRoomId(), jsonString);
            logger.info("setRoom:" + jsonString);
        } catch (Exception e) {
            logger.error("setRoom", e);
        }
        return str;
    }

    public static Long delRoom(long roomId) {
        return RedisCache.delHash("rooms", roomId + "");
//        RedisCache.delValue("roomInf" + roomId);
    }

    public static void save(RoomInf roomInf) {
        // TODO Auto-generated method stub
        roomInf.setLeftDeskCardNum(roomInf.getLeftDeskCards() == null ? 0 : roomInf.getLeftDeskCards().size());
        for (int i = 0; i < roomInf.getPlayers().size(); i++) {
            if (roomInf.getPlayers().get(i).getCards() == null && roomInf.getPlayers().get(i).getCards().size() > 0)
                continue;
            for (int j = 0; j < roomInf.getPlayers().get(i).getCards().size(); j++) {
                roomInf.getPlayers().get(i).getCards().get(j)
                        .setCardNum(roomInf.getPlayers().get(i).getCards().get(j).getCards().size());
            }
        }
        setRoom(roomInf);
    }


}
