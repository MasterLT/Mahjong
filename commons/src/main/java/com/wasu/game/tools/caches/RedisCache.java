package com.wasu.game.tools.caches;

import com.wasu.game.tools.AppBeanContext;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

/**
 * Created by admin on 2017/6/9.
 */
public class RedisCache {

    private static Logger logger = Logger.getLogger(RedisCache.class);

    private static JedisPool jedisPool = null;

    /**
     * 同步获取Jedis实例
     *
     * @return Jedis
     */
    private synchronized static Jedis getJedis() {
        Jedis jedis = null;
        try {
            if (jedisPool == null) {
                jedisPool = AppBeanContext.getBean(JedisPool.class);
            }
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
            }
        } catch (Exception e) {
            logger.error("Get jedis error : " + e);
        } finally {
//            returnResource(jedis);
        }
        return jedis;

    }

    /**
     * 释放jedis资源
     *
     * @param jedis
     */
    private static void returnResource(final Jedis jedis) {
        if (jedis != null && jedisPool != null) {
            jedisPool.returnResourceObject(jedis);
        }
    }

    private static void returnBrokenResource(final Jedis jedis) {
        if (jedis != null && jedisPool != null) {
            jedisPool.returnBrokenResource(jedis);
        }
    }

    /**
     * 设置 String
     *
     * @param key
     * @param value
     */
    public static String setValue(String key, String value) {
        long start = System.currentTimeMillis();
        Jedis jedis = null;
        try {
            value = StringUtils.isEmpty(value) ? "" : value;
            jedis = getJedis();
            return jedis.set(key, value);
        } catch (Exception e) {
            logger.error("Set key error : " + e);
            returnBrokenResource(jedis);
        } finally {
            jedis.close();
            returnResource(jedis);
            logger.info("redis setValue -- time :" + (System.currentTimeMillis() - start));
        }
        return null;
    }

    /**
     * 设置 过期时间
     *
     * @param key
     * @param seconds 以秒为单位
     * @param value
     */
    public static String setValue(String key, int seconds, String value) {
        long start = System.currentTimeMillis();
        Jedis jedis = null;
        try {
            value = StringUtils.isEmpty(value) ? "" : value;
            jedis = getJedis();
            return jedis.setex(key, seconds, value);
        } catch (Exception e) {
            returnBrokenResource(jedis);
            logger.error("Set keyex error : " + e);
        } finally {
            returnResource(jedis);
            logger.info("redis setValue -- time :" + (System.currentTimeMillis() - start));
        }
        return null;
    }

    /**
     * 获取String值
     *
     * @param key
     * @return value
     */
    public static String getValue(String key) {
        long start = System.currentTimeMillis();
        Jedis jedis = null;
        try {
            jedis = getJedis();
            if (jedis != null && jedis.exists(key)) {
                return jedis.get(key);
            }
        } catch (Exception e) {
            returnBrokenResource(jedis);
            logger.error("getValue: " + e);
        } finally {
            returnResource(jedis);
            logger.info("redis getValue -- time :" + (System.currentTimeMillis() - start));
        }
        return null;
    }

    /**
     * 删除String值
     *
     * @param key
     * @return
     */
    public static Long delValue(String key) {
        long start = System.currentTimeMillis();
        Jedis jedis = null;
        try {
            jedis = getJedis();
            if (jedis != null && jedis.exists(key)) {
                return jedis.del(key);
            }
        } catch (Exception e) {
            returnBrokenResource(jedis);
            logger.error("delValue : " + e);
        } finally {
            returnResource(jedis);
            logger.info("redis delValue -- time :" + (System.currentTimeMillis() - start));
        }
        return null;
    }


    public static Long setHash(String key, String field, String value) {
        long start = System.currentTimeMillis();
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hset(key, field, value);
        } catch (Exception e) {
            logger.error("setHash : " + e);
        } finally {
            jedis.close();
            logger.info("redis setHash -- time :" + (System.currentTimeMillis() - start));
        }
        return null;
    }

    public static String getHash(String key, String field) {
        long start = System.currentTimeMillis();
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hget(key, field);
        } catch (Exception e) {
            logger.error("getHash : " + e);
        } finally {
            jedis.close();
            logger.info("redis getHash -- time :" + (System.currentTimeMillis() - start));
        }
        return null;
    }


    public static Long lPush(String key, String value) {
        long start = System.currentTimeMillis();
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("lPush : " + e);
        } finally {
            jedis.close();
            logger.info("redis lPush -- time :" + (System.currentTimeMillis() - start));
        }
        return null;
    }

    public static List<String> lRange(String key) {
        long start = System.currentTimeMillis();
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.lrange(key, 0, -1);
        } catch (Exception e) {
            logger.error("lRange : " + e);
        } finally {
            jedis.close();
            logger.info("redis lRange -- time :" + (System.currentTimeMillis() - start));
        }
        return null;
    }

    public static Long delHash(String key, String... fields) {
        long start = System.currentTimeMillis();
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hdel(key, fields);
        } catch (Exception e) {
            logger.error("delHash : " + e);
        } finally {
            jedis.close();
            logger.info("redis delHash -- time :" + (System.currentTimeMillis() - start));
        }
        return null;
    }


    public static Long putSet(String key, String... value) {
        long start = System.currentTimeMillis();
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.sadd(key, value);
        } catch (Exception e) {
            logger.error("putSet : " + e);
        } finally {
            jedis.close();
            logger.info("redis putSet -- time :" + (System.currentTimeMillis() - start));
        }
        return null;
    }

    public static Long countSet(String key) {
        long start = System.currentTimeMillis();
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.scard(key);
        } catch (Exception e) {
            logger.error("countSet : " + e);
        } finally {
            jedis.close();
            logger.info("redis countSet -- time :" + (System.currentTimeMillis() - start));
        }
        return null;
    }

    public static Long delSet(String key, String... value) {
        long start = System.currentTimeMillis();
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.srem(key, value);
        } catch (Exception e) {
            logger.error("delSet : " + e);
        } finally {
            jedis.close();
            logger.info("redis delSet -- time :" + (System.currentTimeMillis() - start));
        }
        return null;
    }

    /**
     * SETNX命令（SET if Not eXists）
     *
     * @param key
     * @param value
     */
    public static Long setnx(String key, String value) {
        long start = System.currentTimeMillis();
        Jedis jedis = null;
        try {
            value = StringUtils.isEmpty(value) ? "" : value;
            jedis = getJedis();
            return jedis.setnx(key,value);
        } catch (Exception e) {
            logger.error("setnx key error : " + e);
            returnBrokenResource(jedis);
        } finally {
            jedis.close();
            logger.info("redis setnx -- time :" + (System.currentTimeMillis() - start));
        }
        return null;
    }


}
