package com.wasu.game.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.wasu.game.domain.Operation;
import com.wasu.game.domain.PlayerST;
import com.wasu.game.domain.RoomInf;
import com.wasu.game.domain.entity.Room;
import com.wasu.game.tools.caches.RedisCache;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by admin on 2017/6/9.
 */
public class OperationService {

    private static Logger logger = Logger.getLogger(OperationService.class);

    public static List<Operation> getOperation(long roomId,Integer count) {
        List<Operation> res = Lists.newArrayList();
        List<String> str = Lists.newArrayList();
        try {
            str = RedisCache.lRange("operation"+roomId+count);
            if (!StringUtils.isEmpty(str)) {
                for(String s:str){
                    Operation operation=JSONObject.parseObject(s, Operation.class);
                    res.add(operation);
                }
                Collections.reverse(res);
            } else {
                logger.info("jsonString is empty....roomId:" + roomId);
            }
        } catch (Exception e) {
            logger.error("getOperation", e);
            logger.info("roomId：" + roomId + "；count"+count+"; str:" + str);
            return null;
        }
        return res;
    }

    public static Long addOperation(Long roomId,Integer count,Operation operation) {
        Long str = null;
        try {
            String jsonString = JSON.toJSONString(operation, SerializerFeature.DisableCircularReferenceDetect);
            str = RedisCache.lPush("operation"+roomId+count, jsonString);
            logger.info("setRoom:" + jsonString);
        } catch (Exception e) {
            logger.error("setRoom", e);
        }
        return str;
    }


    public static Long delOperation(Long roomId,Integer count) {
        return RedisCache.delValue("operation"+roomId+count);
    }

    public static Long delOperation(Long roomId) {
        return RedisCache.delValue("operation"+roomId);
    }

}
