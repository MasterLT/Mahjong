package com.wasu.game.service;

import com.wasu.game.tools.caches.RedisCache;

/**
 * Created by admin on 2017/6/9.
 */
public class UserService {


    private static final String REDIS_USERS_KEY = "users";

    public static String getUser(Long userId) {
        return RedisCache.getHash(REDIS_USERS_KEY, userId.toString());
    }

    public static Long setUser(Long userId, String value) {
        return RedisCache.setHash(REDIS_USERS_KEY, userId.toString(), value);
    }


}
