package com.wasu.game.module.player.handler;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wasu.game.activeMQ.JmsTypeEnum;
import com.wasu.game.activeMQ.MQProductor;
import com.wasu.game.domain.*;
import com.wasu.game.domain.response.RoomOperationInfoResponse;
import com.wasu.game.domain.response.RoomResponse;
import com.wasu.game.enums.heb.RuleType;
import com.wasu.game.exception.ErrorCodeException;
import com.wasu.game.module.CardCmd;
import com.wasu.game.module.ModuleId;
import com.wasu.game.module.PlayerCmd;
import com.wasu.game.module.player.core.LinkedHandler;
import com.wasu.game.module.player.core.PlayCardUtils;
import com.wasu.game.module.player.service.CardService;
import com.wasu.game.module.player.service.PlayerService;
import com.wasu.game.quarz.QuarzManager;
import com.wasu.game.quarz.job.EatJob;
import com.wasu.game.quarz.job.PengJob;
import com.wasu.game.quarz.job.PlayJob;
import com.wasu.game.service.RoomService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 玩家模块
 *
 * @author -琴兽-
 */
@Component
public class PlayerHandlerImpl implements PlayerHandler {

    private static Logger logger = Logger.getLogger(PlayerHandlerImpl.class);

    @Autowired
    private PlayerService playerService;
    @Autowired
    private CardService cardService;
    @Autowired
    private LinkedHandler linkedHandler;
    @Autowired
    private PlayCardUtils playCardUtils;

    public void intoRoom(Request request) {
        RoomResponse result = null;
        JSONObject data = (JSONObject) request.getData();
        // 去出对象
        long playerId = data.getLong("id");
        int roomNum = data.getInteger("roomNum");
        String password = data.getString("password");
        try {
            // 参数判空
            if (StringUtils.isEmpty(playerId) || StringUtils.isEmpty(roomNum)) {
                logger.error(Result.ERROR(ResultCode.AGRUMENT_ERROR, request, playerId));
                return;
            }
            // 执行业务
            result = playerService.intoRoom(playerId, roomNum, password);
            // 发送人
            List<Long> receiver = new ArrayList<Long>();
            for (PlayerST s : RoomService.getRoomInf(result.getId())) {
                receiver.add(s.getPlayerId());
            }
            MQProductor.build().send(Result.SUCCESS(result, request, receiver), JmsTypeEnum.RADIATE);
            logger.info("用户id:" + playerId + ":进入房间roomNum:" + roomNum);
            //距离提示
            playerService.distance(result.getId(),playerId);
        } catch (ErrorCodeException e) {
            logger.info("ErrorCodeException:" + e.getErrorCode() + "; " + e.toString());
//            e.printStackTrace();
            MQProductor.build().send(Result.ERROR(e.getErrorCode(), request, playerId), JmsTypeEnum.RADIATE);
        } catch (Exception e) {
            logger.error("PlayerHandlerImpl.intoRoom", e);
            logger.info(request.getData());
            e.printStackTrace();
            MQProductor.build().send(Result.ERROR(ResultCode.UNKOWN_EXCEPTION, request, playerId), JmsTypeEnum.RADIATE);
        }
    }

    @Override
    public void outRoome(Request request) {
        RoomResponse result = null;
        JSONObject data = (JSONObject) request.getData();
        // 取出对象
        long playerId = data.getLong("id");
        int roomId = data.getInteger("roomId");
        try {


            List<Long> userIds = RoomService.getRoomUserIds(roomId);
            if(CollectionUtils.isEmpty(userIds)){
                userIds.add(playerId);
            }

            // 执行业务
            result = playerService.outRoom(playerId, roomId);
//            if (result == null)
//                return;
            // 发送人
//            List<Long> receiver = new ArrayList<Long>();
//            for (PlayerST s : SessionManager.getRoomInf(roomId)) {
//                receiver.add(s.getPlayerId());
//            }
            if(result.getState() == 4){
                MQProductor.build().send(Result.SUCCESS(result, Request.valueOf(ModuleId.CARD, CardCmd.DISSOLUTION, null), userIds), JmsTypeEnum.RADIATE);
            }else {
                MQProductor.build().send(Result.SUCCESS(result, request, userIds), JmsTypeEnum.RADIATE);
            }
        } catch (ErrorCodeException e) {
            logger.info(request.toString());
            e.printStackTrace();
            MQProductor.build().send(Result.ERROR(e.getErrorCode(), request, playerId), JmsTypeEnum.RADIATE);
        } catch (Exception e) {
            logger.info(request.toString());
            e.printStackTrace();
            MQProductor.build().send(Result.ERROR(ResultCode.UNKOWN_EXCEPTION, request, playerId), JmsTypeEnum.RADIATE);
        }
    }

    public void createRoom(Request request) {
        long start = System.currentTimeMillis();
        RoomResponse result = null;
        logger.info("createRoom json:" + request.getData());
        JSONObject data = (JSONObject) request.getData();
        // 去出对象
        long playerId = data.getLong("id");
        int totalCount = data.getInteger("totalCount");
        int playerNum = data.getInteger("playerNum");
        String rule = data.getString("rule");
        String password = data.getString("password");
        if (rule == null)
            rule = "";
        try {
            // 参数判空
            if (StringUtils.isEmpty(playerId) || StringUtils.isEmpty(totalCount)) {
                logger.error(Result.ERROR(ResultCode.AGRUMENT_ERROR, request, playerId));
                return;
            }
            // 执行业务
            result = playerService.createRoom(playerId, totalCount, playerNum, rule, password);
            logger.info(" createRoom 1 time:" + (System.currentTimeMillis() - start));
            // 发送人
            List<Long> receiver = new ArrayList<Long>();
            receiver.add(playerId);
            MQProductor.build().send(Result.SUCCESS(result, request, receiver), JmsTypeEnum.RADIATE);
        } catch (ErrorCodeException e) {
            logger.info("createRoom ErrorCodeException:" + e.getErrorCode() + "userId:" + playerId);
            MQProductor.build().send(Result.ERROR(e.getErrorCode(), request, playerId), JmsTypeEnum.RADIATE);
        } catch (Exception e) {
            logger.error("createRoom Exception", e);
            e.printStackTrace();
            MQProductor.build().send(Result.ERROR(ResultCode.UNKOWN_EXCEPTION, request, playerId), JmsTypeEnum.RADIATE);
        }
        logger.info(" createRoom time:" + (System.currentTimeMillis() - start));
    }

    public void ready(Request request) {
        RoomResponse result = null;
//        List<PlayerModule.RoomOperationInfoResponse> shuffleResult = null;
        List<RoomOperationInfoResponse> shuffleResult = null;
        JSONObject data = (JSONObject) request.getData();
        // 取出对象
        long playerId = data.getLong("id");
        long roomId = data.getInteger("roomId");
        try {
            // 参数判空
            if (StringUtils.isEmpty(playerId) || StringUtils.isEmpty(roomId)) {
                logger.error(Result.ERROR(ResultCode.AGRUMENT_ERROR, request, playerId));
                return;
            }
            // 执行业务
            result = playerService.ready(playerId, roomId);
            // 发送人
            List<Long> receiver = new ArrayList<Long>();
            for (PlayerST s : RoomService.getRoomInf(roomId)) {
                receiver.add(s.getPlayerId());
            }
            // 准备回馈
            MQProductor.build().send(Result.SUCCESS(result, request, receiver), JmsTypeEnum.RADIATE);
            if (RoomService.isRoomAllReady(roomId)) {
                //房间庄信息
                RoomResponse res = playerService.roomInfo(roomId);
                request.setCmd(PlayerCmd.ROOMINFO);
                MQProductor.build().send(Result.SUCCESS(res, request, receiver), JmsTypeEnum.RADIATE);
                // 发牌
                shuffleResult = playerService.shuffle1(RoomService.getRoom(roomId));
                //生成财神
                RoomOperationInfo god = cardService.setGodWealth(roomId);
                //发送财神
                List<Result> godResult = new ArrayList<Result>();
                for (PlayerST s : RoomService.getRoomInf(roomId)) {
                    List<Long> a = new ArrayList<Long>();
                    a.add(s.getPlayerId());
                    request.setCmd(PlayerCmd.GOD);
                    godResult.add(Result.SUCCESS(god, request, a));
                }
                MQProductor.build().send(godResult, JmsTypeEnum.RADIATE);
                Thread.sleep(1000);

                // 发牌
                List<Result> dataList = new ArrayList<Result>();
                for (PlayerST s : RoomService.getRoomInf(roomId)) {
                    List<Long> a = new ArrayList<Long>();
                    a.add(s.getPlayerId());
                    request.setCmd(PlayerCmd.SendCard);
                    dataList.add(Result.SUCCESS(shuffleResult.get(s.getPosition()), request, a));
                }
                MQProductor.build().send(dataList, JmsTypeEnum.RADIATE);

                Thread.sleep(1000);

                //庄家抓牌
                linkedHandler.push1(roomId);
            }
        } catch (ErrorCodeException e) {
            logger.info(request.toString());
            MQProductor.build().send(Result.ERROR(e.getErrorCode(), request, playerId), JmsTypeEnum.RADIATE);
        } catch (Exception e) {
            logger.info(request.toString());
            e.printStackTrace();
            MQProductor.build().send(Result.ERROR(ResultCode.UNKOWN_EXCEPTION, request, playerId), JmsTypeEnum.RADIATE);
        }
    }

    @Override
//    public void roomInfo(Request request) {
//        RoomResponse result = null;
//        JSONObject data = (JSONObject) request.getData();
//        long roomId = data.getInteger("roomId");
//        result = playerService.roomInfo(roomId);
//        // 发送人
//        List<Long> receiver = new ArrayList<Long>();
//        for (PlayerST s : RoomService.getRoomInf(roomId)) {
//            receiver.add(s.getPlayerId());
//        }
//        MQProductor.build().send(Result.SUCCESS(result, request, receiver), JmsTypeEnum.RADIATE);
//    }

    public void roomInfo(Request request) {
        JSONObject data = (JSONObject) request.getData();
        long playerId = data.getLong("id");
        int roomId = data.getInteger("roomId");
        try {
            RoomResponse roomResponse = playerService.getUserInfo(roomId);
            playCardUtils.sendMsgForOne(PlayerCmd.ROOMINFO, playerId, roomResponse);
        } catch (Exception e) {
            logger.info("errorCode:" + ResultCode.UNKOWN_EXCEPTION);
            logger.error("getUserInfo error", e);
            MQProductor.build().send(Result.ERROR(ResultCode.UNKOWN_EXCEPTION, request, playerId), JmsTypeEnum.RADIATE);
        }
    }

    @Override
    public void getUserInfo(Request request) {
        JSONObject data = (JSONObject) request.getData();
        long playerId = data.getLong("id");
        int roomId = data.getInteger("roomId");
        try {
            RoomResponse roomResponse = playerService.getUserInfo(roomId);
            playCardUtils.sendMsgForOne(PlayerCmd.USERINFO, playerId, roomResponse);
        } catch (Exception e) {
            logger.info("errorCode:" + ResultCode.UNKOWN_EXCEPTION);
            logger.error("getUserInfo error", e);
            MQProductor.build().send(Result.ERROR(ResultCode.UNKOWN_EXCEPTION, request, playerId), JmsTypeEnum.RADIATE);
        }
    }

    @Override
    public void activity(Request request) {
        JSONObject data = (JSONObject) request.getData();
        long playerId = data.getLong("id");
        int type = data.getInteger("type");
        try {
            int cardNum = playerService.activity(playerId,type);
            playCardUtils.sendMsgForOne(PlayerCmd.ACTIVITY, playerId, cardNum);
        } catch (Exception e) {
            logger.info("errorCode:" + ResultCode.UNKOWN_EXCEPTION);
            logger.error("getUserInfo error", e);
            MQProductor.build().send(Result.ERROR(ResultCode.UNKOWN_EXCEPTION, request, playerId), JmsTypeEnum.RADIATE);
        }
    }
//
//    @Override
//    public void out(Request data) {
//        //删除当前job
//        try {
//            QuarzManager.deleteJob(1, PengJob.class);
//            QuarzManager.deleteJob(1, EatJob.class);
//            QuarzManager.deleteJob(1, PlayJob.class);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    boolean isDaFirst(long roomId) {
        String[] rule = RoomService.getRoom(roomId).getRule();
        return rule != null && Lists.newArrayList(rule).contains(RuleType.DA.getType() + "");
    }

}
