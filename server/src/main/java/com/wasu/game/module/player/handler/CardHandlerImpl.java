package com.wasu.game.module.player.handler;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wasu.game.activeMQ.JmsTypeEnum;
import com.wasu.game.activeMQ.MQProductor;
import com.wasu.game.domain.*;
import com.wasu.game.domain.response.RoomResponse;
import com.wasu.game.domain.response.UserResponse;
import com.wasu.game.enums.CardType;
import com.wasu.game.enums.heb.RuleType;
import com.wasu.game.enums.TipType;
import com.wasu.game.exception.ErrorCodeException;
import com.wasu.game.module.CardCmd;
import com.wasu.game.module.player.core.LinkedHandler;
import com.wasu.game.module.player.core.PlayCardUtils;
import com.wasu.game.module.player.service.CardService;
import com.wasu.game.module.player.service.PlayerService;
import com.wasu.game.quarz.QuarzManager;
import com.wasu.game.quarz.job.PopJob;
import com.wasu.game.quarz.job.PushJob;
import com.wasu.game.service.RoomService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2016/12/28.
 */
@Component
public class CardHandlerImpl implements CardHandler {

    private static Logger logger = Logger.getLogger(CardHandlerImpl.class);
    @Autowired
    private LinkedHandler linkedHandler;
    //    @Autowired
//    private ReverseCardUtils reverseCardUtils;
    @Autowired
    private PlayCardUtils playCardUtils;
    @Autowired
    private CardService cardService;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private LinkedHandler reverseHandler;

    @Override
    public void chuPai(Request request) {
        JSONObject data = (JSONObject) request.getData();
        // 取出对象
        long playerId = data.getLong("id");
        int roomId = data.getInteger("roomId");
        int outCard = data.getInteger("outCard");
        try {
            //有提示不能出牌
            if (RoomService.getRoom(roomId).getTempTips() != null)
                return;
            if (RoomService.getRoomUser(roomId, playerId).getPosition() != RoomService.getRoom(roomId).getCurrent())
                throw new ErrorCodeException(ResultCode.ERROROUTHAND);
            if (isDaFirst(roomId)) {//先打后抓
                reverseHandler.pop(roomId, outCard);
            } else {
                linkedHandler.pop(roomId, outCard);
            }
        } catch (ErrorCodeException e) {
            e.printStackTrace();
            MQProductor.build().send(Result.ERROR(e.getErrorCode(), request, playerId), JmsTypeEnum.RADIATE);
        } catch (Exception e) {
            e.printStackTrace();
            MQProductor.build().send(Result.ERROR(ResultCode.UNKOWN_EXCEPTION, request, playerId), JmsTypeEnum.RADIATE);
        }
    }

    @Override
    public void peng(Request request) {
        JSONObject data = (JSONObject) request.getData();
        // 取出对象
        long playerId = data.getLong("id");
        int roomId = data.getInteger("roomId");
        int card = data.getInteger("card");
        try {
            // 参数判空
            if (StringUtils.isEmpty(playerId) || StringUtils.isEmpty(roomId) || StringUtils.isEmpty(card)) {
                logger.error(Result.ERROR(ResultCode.AGRUMENT_ERROR, request, playerId));
                return;
            }
            //删除定时器
            QuarzManager.deleteJob(roomId, PushJob.class);
//            if (isDaFirst(roomId)) {//先打后抓
//                reverseCardUtils.peng(roomId, playerId, card);
//            } else {
            // 通知碰牌,出牌顺序变化,并删除tips
            playCardUtils.peng(roomId, playerId, card);
            //吃後杠牌
            List<Tip> tips = playCardUtils.setGangTips(roomId, 44);
            if (tips.size() > 0)
                playCardUtils.getCardTips(roomId, tips);
//            }
            //出牌定时
            QuarzManager.addJob(roomId, QuarzManager.outCardWaitTime, PopJob.class);
        } catch (ErrorCodeException e) {
            e.printStackTrace();
            MQProductor.build().send(Result.ERROR(e.getErrorCode(), request, playerId), JmsTypeEnum.RADIATE);
        } catch (Exception e) {
            e.printStackTrace();
            MQProductor.build().send(Result.ERROR(ResultCode.UNKOWN_EXCEPTION, request, playerId), JmsTypeEnum.RADIATE);
        }
    }

    @Override
    public void chi(Request request) {
        JSONObject data = (JSONObject) request.getData();
        // 取出对象
        long playerId = data.getLong("id");
        int roomId = data.getInteger("roomId");
        List<Integer> cards = Arrays.asList(data.getObject("card", Integer[].class));
        try {
            // 参数判空
            if (StringUtils.isEmpty(playerId) || StringUtils.isEmpty(roomId)) {
                logger.error(Result.ERROR(ResultCode.AGRUMENT_ERROR, request, playerId));
                return;
            }
            //删除定时器
            QuarzManager.deleteJob(roomId, PushJob.class);
//            if (isDaFirst(roomId)) {//先打后抓
//                reverseCardUtils.eat(roomId, cards);
//            } else {
            // 通知吃牌,出牌顺序变化,删除tips
            playCardUtils.eat(roomId, cards);
            //吃後杠牌
            List<Tip> tips = playCardUtils.setGangTips(roomId, 44);
            if (tips.size() > 0)
                playCardUtils.getCardTips(roomId, tips);
//            }
            //出牌定时
            QuarzManager.addJob(roomId, QuarzManager.outCardWaitTime, PopJob.class);
        } catch (ErrorCodeException e) {
            e.printStackTrace();
            MQProductor.build().send(Result.ERROR(e.getErrorCode(), request, playerId), JmsTypeEnum.RADIATE);
        } catch (Exception e) {
            e.printStackTrace();
            MQProductor.build().send(Result.ERROR(ResultCode.UNKOWN_EXCEPTION, request, playerId), JmsTypeEnum.RADIATE);
        }
    }

    @Override
    public void mgang(Request request) {
        JSONObject data = (JSONObject) request.getData();
        // 取出对象
        long playerId = data.getLong("id");
        int roomId = data.getInteger("roomId");
        int card = data.getInteger("card");
        try {
            // 参数判空
            if (StringUtils.isEmpty(playerId) || StringUtils.isEmpty(roomId) || StringUtils.isEmpty(card)) {
                logger.error(Result.ERROR(ResultCode.AGRUMENT_ERROR, request, playerId));
                return;
            }
            //删除定时器
            QuarzManager.deleteJob(roomId, PushJob.class);
//            if (isDaFirst(roomId)) {//先打后抓
//                // 通知杠牌,出牌顺序变化,并清空list
//                reverseCardUtils.gang(roomId, playerId, card);
//                //出一张再抓一张
//                reverseHandler.pop(roomId, null);
//            } else {
            // 通知杠牌,出牌顺序变化,并清空list
            playCardUtils.gang(roomId, playerId, card);
            //抓牌
            linkedHandler.push(roomId);
            //添加弃牌
            playCardUtils.addDisCard(roomId);
//            }
        } catch (ErrorCodeException e) {
            e.printStackTrace();
            MQProductor.build().send(Result.ERROR(e.getErrorCode(), request, playerId), JmsTypeEnum.RADIATE);
        } catch (Exception e) {
            e.printStackTrace();
            MQProductor.build().send(Result.ERROR(ResultCode.UNKOWN_EXCEPTION, request, playerId), JmsTypeEnum.RADIATE);
        }
    }

    @Override
    public void mgang2(Request request) {
        JSONObject data = (JSONObject) request.getData();
        // 取出对象
        long playerId = data.getLong("id");
        int roomId = data.getInteger("roomId");
        int card = data.getInteger("card");
        try {
            // 参数判空
            if (StringUtils.isEmpty(playerId) || StringUtils.isEmpty(roomId) || StringUtils.isEmpty(card)) {
                logger.error(Result.ERROR(ResultCode.AGRUMENT_ERROR, request, playerId));
                return;
            }
            // 通知杠牌,出牌顺序变化,并清空list
            playCardUtils.mgang2(roomId, card);
            //抓牌
//            linkedHandler.push(roomId);
        } catch (ErrorCodeException e) {
            e.printStackTrace();
            MQProductor.build().send(Result.ERROR(e.getErrorCode(), request, playerId), JmsTypeEnum.RADIATE);
        } catch (Exception e) {
            e.printStackTrace();
            MQProductor.build().send(Result.ERROR(ResultCode.UNKOWN_EXCEPTION, request, playerId), JmsTypeEnum.RADIATE);
        }
    }

    @Override
    public void agang(Request request) {
        JSONObject data = (JSONObject) request.getData();
        // 取出对象
        long playerId = data.getLong("id");
        int roomId = data.getInteger("roomId");
        int card = data.getInteger("card");
        try {
            // 参数判空
            if (StringUtils.isEmpty(playerId) || StringUtils.isEmpty(roomId) || StringUtils.isEmpty(card)) {
                logger.error(Result.ERROR(ResultCode.AGRUMENT_ERROR, request, playerId));
                return;
            }
            //删除定时器
            QuarzManager.deleteJob(roomId, PushJob.class);
//            if (isDaFirst(roomId)) {//先打后抓
//                // 通知杠牌,出牌顺序变化,并清空list
//                reverseCardUtils.anGang(roomId, playerId, card);
//                //抓牌--位置变化
//                reverseHandler.push(roomId);
//            } else {
            // 通知杠牌,出牌顺序变化,并清空list
            playCardUtils.anGang(roomId, playerId, card);
            //抓牌
            linkedHandler.push(roomId);
            //添加弃牌
            playCardUtils.addDisCard(roomId);
//            }
        } catch (ErrorCodeException e) {
            e.printStackTrace();
            MQProductor.build().send(Result.ERROR(e.getErrorCode(), request, playerId), JmsTypeEnum.RADIATE);
        } catch (Exception e) {
            e.printStackTrace();
            MQProductor.build().send(Result.ERROR(ResultCode.UNKOWN_EXCEPTION, request, playerId), JmsTypeEnum.RADIATE);
        }
    }

    @Override
    public void pass(Request request) {
        JSONObject data = (JSONObject) request.getData();
        int roomId = data.getInteger("roomId");
        long playerId = data.getLong("id");
        try {
            RoomInf roomInf = RoomService.getRoom(roomId);
            //删除定时器
            QuarzManager.deleteJob(roomId, PushJob.class);
            roomInf.getPlayerByPosition(roomInf.getPositionById(playerId)).setChiOrPengTingInfos(null);
            List<Tip> tips = roomInf.getTempTips();
            //其他玩家点pass无效
            if (tips.size() > 0 && playerId != tips.get(0).getUserid())
                return;
            roomInf.setTempTips(null);
            RoomService.setRoom(roomInf);
//            if (isDaFirst(roomId)) {//先打后抓
//                if (CardCmd.ZHUAPAI == SessionManager.getRoom(roomId).getOption())
//                    reverseHandler.push(roomId);
//                else
//                    reverseHandler.pop(roomId, null);
//            } else {
            if (CardCmd.ZHUAPAI == RoomService.getRoom(roomId).getOption())
                linkedHandler.push(roomId);
            else if (CardCmd.MGANG2 == RoomService.getRoom(roomId).getOption())
                playCardUtils.toMingGang(roomId, getHuCard(tips));
            else if (CardCmd.PENG == RoomService.getRoom(roomId).getOption())
                return;//碰后杠点pass'
            else
                linkedHandler.pop(roomId, null);
//            }
        } catch (ErrorCodeException e) {
            e.printStackTrace();
            MQProductor.build().send(Result.ERROR(e.getErrorCode(), request, playerId), JmsTypeEnum.RADIATE);
        } catch (Exception e) {
            e.printStackTrace();
            MQProductor.build().send(Result.ERROR(ResultCode.UNKOWN_EXCEPTION, request, playerId), JmsTypeEnum.RADIATE);
        }
    }

    private Integer getHuCard(List<Tip> tips) {
        Integer huCard = null;
        for (Tip t : tips) {
            if (t.getType() != TipType.CANHU.getType())
                continue;
            for (Card card : t.getTipInfos().get(0).getCards()) {
                if (card.getType() != CardType.BEHU.getType())
                    continue;
                huCard = card.getCards().get(0);
            }
        }
        return huCard;
    }

    @Override
    public void hu(Request request) {
        JSONObject data = (JSONObject) request.getData();
        // 取出对象
        long playerId = data.getLong("id");
        int roomId = data.getInteger("roomId");
        int card = data.getInteger("card");
        //删除定时器
        QuarzManager.deleteJob(roomId, PushJob.class);
        //发送所有用户手中牌，取消用户准备状态，清空tips，修改房间状态为0
        playCardUtils.hu(roomId, card, playerId);
        //发送所有用户积分
        playCardUtils.end(roomId);
    }


    public void dissolution1(Request request) {
        RoomResponse response = null;
        JSONObject data = (JSONObject) request.getData();
        long playerId = data.getLong("id");
        int roomId = data.getInteger("roomId");
        int dissolution = data.getInteger("dissolution");
        try {
            response = cardService.dissolution(playerId, roomId, dissolution);
            playCardUtils.sendMsgForAllSame(roomId, CardCmd.DISSOLUTION, response);
            if (RoomService.getRoom(roomId).getStatus() == 4)
                cardService.distroyRoom(roomId);
        } catch (ErrorCodeException e) {
            e.printStackTrace();
            MQProductor.build().send(Result.ERROR(e.getErrorCode(), request, playerId), JmsTypeEnum.RADIATE);
        } catch (Exception e) {
            e.printStackTrace();
            MQProductor.build().send(Result.ERROR(ResultCode.UNKOWN_EXCEPTION, request, playerId), JmsTypeEnum.RADIATE);
        }
    }

    @Override
    public void dissolution(Request request) {//合并退出房间和申请解散房间
        RoomResponse response = null;
        JSONObject data = (JSONObject) request.getData();
        long playerId = data.getLong("id");
        int roomId = data.getInteger("roomId");
        Integer dissolution = data.getInteger("dissolution");
        try {
            RoomInf roomInf = RoomService.getRoom(roomId);
            List<Long> userIds = RoomService.getRoomUserIds(roomId);
            if (CollectionUtils.isEmpty(userIds)) {
                userIds.add(playerId);
            }
            if (roomInf.getStatus() == 0 && roomInf.getCurrentCount() == 0) {
                response = playerService.outRoom(playerId, roomId);
                MQProductor.build().send(Result.SUCCESS(response, request, userIds), JmsTypeEnum.RADIATE);
            } else {
                response = cardService.dissolution(playerId, roomId, dissolution);
                playCardUtils.sendMsgForAllSame(roomId, CardCmd.DISSOLUTION, response);
            }
            if (RoomService.getRoom(roomId) != null && RoomService.getRoom(roomId).getStatus() == 4)
                cardService.distroyRoom(roomId);
        } catch (ErrorCodeException e) {
            e.printStackTrace();
            MQProductor.build().send(Result.ERROR(e.getErrorCode(), request, playerId), JmsTypeEnum.RADIATE);
        } catch (Exception e) {
            e.printStackTrace();
            MQProductor.build().send(Result.ERROR(ResultCode.UNKOWN_EXCEPTION, request, playerId), JmsTypeEnum.RADIATE);
        }
    }

    @Override
    public void reconnected(Request request) {
        JSONObject data = (JSONObject) request.getData();
        long playerId = data.getLong("id");
        int roomId = data.getInteger("roomId");
        try {
            //设置为在线
            RoomInf newroom = RoomService.getRoom(roomId);
            PlayerST playerST = newroom.getPlayerById(playerId);
            playerST.setOnline(true);
            RoomService.setRoom(newroom);
            //房间所有信息
            RoomInf roomInf = playerService.returnRoomInf(roomId, playerId);
            Thread.sleep(500);
            if (roomInf.getStatus() != 0)
                for (PlayerST p : roomInf.getPlayers()) {
                    if (p.getPlayerId() == playerId)
                        continue;
                    //其余用户手牌删除
                    List<Integer> list = p.getCardsByType(CardType.SHOUPAI.getType());
                    int lenth = list.size();
                    list.clear();
                    for (int i = 0; i < lenth; i++)
                        list.add(0);

                    //其余用户暗杠删除
                    List<Integer> list2 = p.getCardsByType(CardType.ANGANG.getType());
                    if (list2 != null && list2.size() > 0) {
                        int lenth2 = list2.size();
                        list2.clear();
                        for (int i = 0; i < lenth2; i++)
                            list2.add(0);
                    }
                }
            playCardUtils.sendMsgForOne(request, playerId, roomInf);
            //通知其他玩家
            UserResponse userResponse = new UserResponse();
            userResponse.setState(playerST.getState());
            userResponse.setId(playerId);
            playCardUtils.sendMsgForAllSame(roomId, CardCmd.ONLINE, userResponse);
            logger.info("id:" + playerId + " roomId:" + roomId + " 断线重连");
        } catch (InterruptedException e) {
            e.printStackTrace();
            MQProductor.build().send(Result.ERROR(ResultCode.UNKOWN_EXCEPTION, request, playerId), JmsTypeEnum.RADIATE);
        }
    }

    @Override
    public void totalRecord(Request request) {
        JSONObject data = (JSONObject) request.getData();
        long playerId = data.getLong("id");
        List<HistoryRecord> recordResponse = playerService.getHistoryRecord(playerId);
        MQProductor.build().send(Result.SUCCESS(recordResponse, request, playerId), JmsTypeEnum.RADIATE);
    }

    @Override
    public void record(Request request) {
        JSONObject data = (JSONObject) request.getData();
        long playerId = data.getLong("id");
        long roomId = data.getLong("roomId");
        HistoryRecord recordResponse = playerService.getRecord(playerId, roomId);
        MQProductor.build().send(Result.SUCCESS(recordResponse, request, playerId), JmsTypeEnum.RADIATE);
    }

    @Override
    public void ting(Request request) {
        JSONObject data = (JSONObject) request.getData();
        // 取出对象
        long playerId = data.getLong("id");
        int roomId = data.getInteger("roomId");
//        if (isDaFirst(roomId)) {//先打后抓
//            reverseCardUtils.ting(roomId, playerId);
//            playCardUtils.getBao(roomId, playerId);
//            //提示下一个人出牌
//            reverseHandler.pop(roomId, null);
//        } else {
        int card = data.getInteger("card");
        playCardUtils.ting(roomId, playerId, card);
        playCardUtils.getBao(roomId, playerId);
        linkedHandler.pop(roomId, card);
//        }
    }

    @Override
    public void chiTing(Request request) {
        JSONObject data = (JSONObject) request.getData();
        // 取出对象
        long playerId = data.getLong("id");
        int roomId = data.getInteger("roomId");
        List<Integer> cards = Lists.newArrayList(data.getObject("card", Integer[].class));
        int outCard = data.getInteger("outCard");

//        if (isDaFirst(roomId)) {//先打后抓
//            reverseCardUtils.chiTing(roomId, playerId, cards, outCard);
//            playCardUtils.getBao(roomId, playerId);
//            //提示下一个人出牌
//            reverseHandler.pop(roomId, outCard);
//        } else {
        playCardUtils.chiTing(roomId, playerId, cards, outCard);
        playCardUtils.getBao(roomId, playerId);
        linkedHandler.pop(roomId, outCard);
//        }
    }

    @Override
    public void pengTing(Request request) {
        JSONObject data = (JSONObject) request.getData();
        // 取出对象
        long playerId = data.getLong("id");
        int roomId = data.getInteger("roomId");
        List<Integer> cards = Lists.newArrayList(data.getObject("card", Integer[].class));
        int outCard = data.getInteger("outCard");

//        if (isDaFirst(roomId)) {//先打后抓
//            reverseCardUtils.pengTing(roomId, playerId, cards, outCard);
//            playCardUtils.getBao(roomId, playerId);
//            //提示下一个人出牌
//            reverseHandler.pop(roomId, outCard);
//        } else {
        playCardUtils.pengTing(roomId, playerId, cards, outCard);
        playCardUtils.getBao(roomId, playerId);
        linkedHandler.pop(roomId, outCard);
//        }
    }

    boolean isDaFirst(long roomId) {
        String[] rule = RoomService.getRoom(roomId).getRule();
        return rule != null && Lists.newArrayList(rule).contains(RuleType.DA.getType() + "");
    }
}
