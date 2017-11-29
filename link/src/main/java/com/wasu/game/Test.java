package com.wasu.game;

import com.alibaba.fastjson.JSONObject;
import com.wasu.game.activeMQ.JmsTypeEnum;
import com.wasu.game.activeMQ.MQProductor;
import com.wasu.game.tools.AppBeanContext;
import com.wasu.game.domain.Request;
import com.wasu.game.module.CardCmd;
import com.wasu.game.module.ModuleId;

/**
 * Hello world!
 */
public class Test {
    public static void main(String[] args) {
        AppBeanContext.getApplicationContext();
        MQProductor.build().send(JmsTypeEnum.REQUEST,"1");
        MQProductor.build().send(JmsTypeEnum.RADIATE,"2");
    }

    public static void test(long id,long roomId){
        JSONObject data = new JSONObject();
        data.put("id", id);
        data.put("roomId", roomId);
        MQProductor mQProductor = MQProductor.build();
        mQProductor.send(Request.valueOf(ModuleId.CARD, CardCmd.RECONNECTED, data), JmsTypeEnum.REQUEST);
    }
}
