package com.wasu.game.service;

import com.wasu.game.domain.Session;
import com.wasu.game.tools.caches.RedisCache;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by admin on 2017/6/9.
 */
public class UserSessionService {

    /**
     * 在线会话
     */
    private static final ConcurrentHashMap<Long, Session> ONLINE_SESSIONS = new ConcurrentHashMap<Long, Session>();

    private static final String REDIS_ONLINE_KEY = "onlineUsers";

    /**
     * 加入
     *
     * @param playerId
     * @return
     */
    public static boolean putSession(Long playerId, Session session) {
        if (!ONLINE_SESSIONS.containsKey(playerId)) {
            RedisCache.putSet(REDIS_ONLINE_KEY, playerId.toString());
            return ONLINE_SESSIONS.putIfAbsent(playerId, session) == null;
        }
        return false;
    }

    /**
     * 移除
     *
     * @param playerId
     */
    public static Session removeSession(Long playerId) {
        RedisCache.delSet(REDIS_ONLINE_KEY, playerId.toString());
        return ONLINE_SESSIONS.remove(playerId);
    }

    /**
     * 获取该服务器用户session
     *
     * @param playerId
     * @return
     */
    public static Session getSession(Long playerId) {
        return ONLINE_SESSIONS.get(playerId);
    }

    /**
     * 是否在线
     *
     * @param playerId
     * @return
     */
    public static boolean isOnline(Long playerId) {
        return ONLINE_SESSIONS.containsKey(playerId);
    }


    /**
     * @return
     */
    public static Long getOnlinePlayerCountOfCurrentServer() {
        return new Long(ONLINE_SESSIONS.keySet().size());
    }

    /**
     * @return
     */
    public static Long getOnlinePlayerCountOfAllServer() {
        return RedisCache.countSet(REDIS_ONLINE_KEY);
    }


}
