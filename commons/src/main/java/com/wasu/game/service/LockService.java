package com.wasu.game.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.wasu.game.domain.Operation;
import com.wasu.game.tools.caches.RedisCache;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by admin on 2017/6/9.
 */
public class LockService {

    private static Logger logger = Logger.getLogger(LockService.class);

    public static Long getLock(long roomId) {
        Long res=null;
        String str = "";
        try {
            str = RedisCache.getValue("lock_"+roomId);
            if (!StringUtils.isEmpty(str)) {
                res = JSONObject.parseObject(str, Long.class);
            } else {
                logger.info("jsonString is empty....roomId:" + roomId);
            }
        } catch (Exception e) {
            logger.error("getLock", e);
            logger.info("roomId：" + roomId + "; str:" + str);
            return null;
        }
        return res;
    }

    public static Boolean setLock(Long roomId,Long time) {
        Long str = null;
        try {
            SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            str = RedisCache.setnx("lock_"+roomId,time+"");
            logger.info("setLock:" + sf.format(time));
        } catch (Exception e) {
            logger.error("setLock", e);
        }
        return str==1?true:false;
    }

    public static Long delLock(Long roomId) {
        return RedisCache.delValue("lock_"+roomId);
    }

}
