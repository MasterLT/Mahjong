package com.wasu.game.module.player.handler;

import com.alibaba.fastjson.JSONObject;
import com.wasu.game.activeMQ.JmsTypeEnum;
import com.wasu.game.activeMQ.MQProductor;
import com.wasu.game.domain.PlayerST;
import com.wasu.game.domain.Request;
import com.wasu.game.domain.Result;
import com.wasu.game.domain.response.RoomResponse;
import com.wasu.game.exception.ErrorCodeException;
import com.wasu.game.module.AdminCmd;
import com.wasu.game.module.ModuleId;
import com.wasu.game.module.player.service.CardService;
import com.wasu.game.module.player.service.PlayerService;
import com.wasu.game.service.RoomService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 玩家模块
 *
 * @author -琴兽-
 */
@Component
public class AdminHandlerImpl implements AdminHandler {

    private static Logger logger = Logger.getLogger(AdminHandlerImpl.class);

    @Autowired
    private CardService cardService;
    @Autowired
    private PlayerService playerService;

    @Override
    public void dissolution(Request request) {
        RoomResponse response = null;
        JSONObject data = (JSONObject) request.getData();
        int roomId = data.getInteger("roomId");
        try {
            if (RoomService.getRoom(roomId) != null){
                cardService.toDissolution(RoomService.getRoom(roomId));
                sendMsgForAllSame(roomId, AdminCmd.DISSOLUTION, response);
            }
            cardService.distroyRoom(roomId);
        } catch (ErrorCodeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addCard(Request request) {
        JSONObject data = (JSONObject) request.getData();
        long id = data.getInteger("id");
        int perssion=playerService.getPermission(id);
        try {
            MQProductor.build().send(Result.SUCCESS(perssion, request, id), JmsTypeEnum.RADIATE);
        } catch (ErrorCodeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //四个人发送相同消息
    public void sendMsgForAllSame(long roomId, short cmd, Object response) {
        List<Result> dataList1 = new ArrayList<Result>();
        for (PlayerST s : RoomService.getRoomInf(roomId)) {
            List<Long> a = new ArrayList<Long>();
            a.add(s.getPlayerId());
            dataList1.add(Result.SUCCESS(response, Request.valueOf(ModuleId.ADMIN, cmd, null), a));
        }
        MQProductor.build().send(dataList1, JmsTypeEnum.RADIATE);
    }

}
