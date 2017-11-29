package com.wasu.game.module.player.core;

import com.google.common.collect.Lists;
import com.wasu.game.activeMQ.JmsTypeEnum;
import com.wasu.game.activeMQ.MQProductor;
import com.wasu.game.domain.*;
import com.wasu.game.domain.entity.OptionRecord;
import com.wasu.game.domain.entity.Record;
import com.wasu.game.domain.entity.Room;
import com.wasu.game.domain.entity.User;
import com.wasu.game.domain.response.*;
import com.wasu.game.enums.CardType;
import com.wasu.game.enums.jy.HuType;
import com.wasu.game.enums.jy.RuleType;
import com.wasu.game.module.CardCmd;
import com.wasu.game.module.ModuleId;
import com.wasu.game.module.PlayerCmd;
import com.wasu.game.module.player.dao.OptionRecordDao;
import com.wasu.game.module.player.dao.RecordDao;
import com.wasu.game.module.player.dao.RoomDao;
import com.wasu.game.module.player.dao.UserDao;
import com.wasu.game.module.player.service.CardService;
import com.wasu.game.module.player.service.PlayerService;
import com.wasu.game.service.OperationService;
import com.wasu.game.service.RoomService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by litao on 2016/12/28.
 */
@Component
public class PlayCardUtils {

    private static Logger logger = Logger.getLogger(PlayCardUtils.class);
    @Autowired
    PlayerService playerService;
    @Autowired
    CardService cardService;
    @Autowired
    RoomDao roomDao;
    @Autowired
    UserDao userDao;
    @Autowired
    RecordDao recordDao;
    @Autowired
    OptionRecordDao optionRecordDao;
    @Autowired
    LinkedHandler linkedHandler;

    //抓牌
    public int getCard(long roomId) {
        List<RoomOperationInfoResponse> getResponse = null;
        getResponse = playerService.deal1(RoomService.getRoom(roomId));
        //发消息
        sendMsgForAll(roomId, PlayerCmd.ZHUAPAI, getResponse);
        return getResponse.get(RoomService.getRoom(roomId).getCurrent()).getPlayerInfos().get(0).getCards().get(0).getCards().get(0);

    }

    //获取宝牌
    public void getBao(long roomId, long playerId) {
        String[] rule = RoomService.getRoom(roomId).getRule();
        if (rule == null)
            return;
        //获取规则数组
        List<Integer> ruleList = Lists.newArrayList();
        for (String s : rule) {
            ruleList.add(Integer.parseInt(s));
        }
        if (ruleList.contains(com.wasu.game.enums.heb.RuleType.LOU.getType()) || ruleList.contains(com.wasu.game.enums.heb.RuleType.FENG) || ruleList.contains(com.wasu.game.enums.heb.RuleType.FEI))
            return;
        //如果漏胡刮大风或红中满天飞则明宝
        RoomOperationInfo bao = cardService.getBao(roomId);
        if (bao == null)
            return;
        //发消息
        sendMsgForOne(CardCmd.BAO, playerId, bao);
    }

    //打牌
    public int playCard(long roomId, Integer outCard) {
        List<RoomOperationInfoResponse> sendResponse = null;
        sendResponse = playerService.outHand1(RoomService.getRoom(roomId), outCard);
        //发消息
        sendMsgForAll(roomId, PlayerCmd.CHUPAI, sendResponse);
        return outCard;
    }

    //吃
    public void eat(long roomId, List<Integer> cards) {
        RoomInf room = RoomService.getRoom(roomId);
        List<RoomOperationInfoResponse> chi = playerService.chi1(room, cards, room.returnBeforePositon());

        //发消息
        sendMsgForAll(roomId, CardCmd.CHI, chi);
    }

    //碰
    public void peng(long roomId, long playerId, int card) {
        RoomInf room = RoomService.getRoom(roomId);
        List<RoomOperationInfoResponse> peng = playerService.peng1(room, card, room.getPositionById(playerId), room.returnBeforePositon());
        sendMsgForAll(roomId, CardCmd.PENG, peng);
    }

    //明杠
    public void gang(long roomId, long playerId, int card) {
        RoomInf room = RoomService.getRoom(roomId);
        List<RoomOperationInfoResponse> gang = playerService.mingGang1(room, card, room.getPositionById(playerId), room.returnBeforePositon());
        sendMsgForAll(roomId, CardCmd.GANG, gang);
    }

    public void mgang2(long roomId, int card) {
        List<Tip> tips = playerService.isQiangGang(roomId, card);
        RoomInf room = RoomService.getRoom(roomId);
        room.setOption(CardCmd.MGANG2);
        if (tips.size() > 0) {
            Collections.sort(tips);
            room.setTips(tips);
            CardResponse card1=new CardResponse(CardType.BEMINGGANG2.getType(),card);
            PlayerInfoResponse playerInfoResponse=new PlayerInfoResponse(room.getPlayerByPosition(room.getCurrent()).getPlayerId(),room.getCurrent(),Lists.<CardResponse>newArrayList(card1));
            sendMsgForAllSame(roomId, CardCmd.QIANGGANG, playerInfoResponse);
        } else
            room.setTips(null);
        RoomService.setRoom(room);
        toMingGang(roomId, card);
    }

    public void toMingGang(long roomId, int card) {
        //发送抓牌提示(听牌只提示胡)
        RoomInf room = RoomService.getRoom(roomId);
        List<Tip> tips = room.getTips();
        if (tips != null && tips.size() > 0) {
            getCardTips(roomId, tips);
            return;
        } else {
//            PlayerST currentPlayer = room.getPlayerByPosition(room.getCurrent());
//            List<Integer> shou=currentPlayer.getCardsByType(CardType.SHOUPAI.getType());
//            List<RoomOperationInfoResponse> mingGang = playerService.mingGang2(roomId, shou.get(shou.size()-1));
            List<RoomOperationInfoResponse> mingGang = playerService.mingGang2(roomId, card);
            sendMsgForAll(roomId, CardCmd.MGANG2, mingGang);
            linkedHandler.push(roomId);
            //添加弃牌
            addDisCard(roomId);
        }
    }

    //暗杠
    public void anGang(long roomId, long playerId, int card) {
        RoomInf room = RoomService.getRoom(roomId);
//        List<PlayerModule.RoomOperationInfoResponse> anGang = playerService.anGang(room, card);
        List<RoomOperationInfoResponse> anGang = playerService.anGang1(room, card);
        sendMsgForAll(roomId, CardCmd.ANGANG, anGang);
    }

    public void addDisCard(long roomId) {
        RoomInf room = RoomService.getRoom(roomId);
        room.setDiscard(room.getDiscard() + 5);
        RoomService.setRoom(room);
    }

    //听
    public void ting(long roomId, long playerId, int card) {
        RoomInf room = RoomService.getRoom(roomId);
        List<Tip> ting = playerService.ting(room, room.getCurrent(), card);
        sendMsgForAll(roomId, CardCmd.TING, ting);
    }

    //peng听
    public void pengTing(long roomId, long playerId, List<Integer> cards, int card) {
        RoomInf room = RoomService.getRoom(roomId);
        List<Tip> ting = playerService.pengTing(room, cards, RoomService.getPositionById(roomId, playerId), room.returnBeforePositon(), card);
        sendMsgForAll(roomId, CardCmd.PENGTING, ting);
    }

    //chi听
    public void chiTing(long roomId, long playerId, List<Integer> cards, int card) {
        RoomInf room = RoomService.getRoom(roomId);
        List<Tip> ting = playerService.chiTing(room, cards, RoomService.getPositionById(roomId, playerId), room.returnBeforePositon(), card);
        sendMsgForAll(roomId, CardCmd.CHITING, ting);
    }

    //胡
    public void hu(long roomId, int card, long playerId) {
        RoomInf room = RoomService.getRoom(roomId);
        playerService.hu(room, card, room.getPositionById(playerId));
    }

    //结束
    public void end(long roomId) {
        int score=0;
        RoomInf room = RoomService.getRoom(roomId);
        Long win = room.getWin();
        String huType = null;
        if (win != null) {
            huType = "";
            List<Integer> tp = room.getPlayerByPosition(room.getPositionById(win)).getHuInfo().getHuCards();
            for (int t : tp) {
                huType += t + ",";
            }
            huType = huType.substring(0, huType.length() - 1);
        }
        Long pao = room.getPao();
        //记录操作信息
        Room roomdb = roomDao.getRoomById(roomId);
        optionRecordDao.createOptionRecord(new OptionRecord(roomdb, room));
        //显示所有牌
        List<Tip> tips = playerService.over(roomId);
        sendMsgForAll(roomId, CardCmd.SHOWCARDS, tips);
        //延迟
        sleep(100);
        //积分
        List<UserResponse> users = Lists.newArrayList();
        for (int i = 0; i < room.getPlayers().size(); i++) {
            //计算积分
            PlayerST player = room.getPlayers().get(i);
            //更新积分
            Record one = recordDao.getRoomRecordByUserId(player.getPlayerId(), roomId, room.getCurrentCount(), 2).get(0);
            Record all = recordDao.getRoomRecordByUserId(player.getPlayerId(), roomId, 1).get(0);
            //庄
            one.setBanker(room.getPlayerByPosition(room.getBanker()).getPlayerId());
            //赢家
            one.setWin(win);
            one.setHutype(huType);
            //积分计算
            pointCount(room, player, one);
            pointCount(room, player, all);
            //更新用户信息胜局，负局
//            userHistoryCount(room, player.getPlayerId());
            updateUser(player.getPlayerId(), win, all.getPoint());
            //胡次数摸宝宝中宝次数
            winCount(room, player, one);
            winCount(room, player, all);
//            //站立
//            standCount(player, one);
//            standCount(player, all);
            //坐庄次数
            bankerCount(room, player, one);
            bankerCount(room, player, all);
            //点炮次数
            paoCount(room, player, one);
            paoCount(room, player, all);
            //更新记录
            recordDao.updateRecord(one);
            recordDao.updateRecord(all);
            //构造积分返回
            UserResponse user = getUserResponse(one, player.getPlayerId(), player);
            users.add(user);
            if(score<one.getPoint())
                score=one.getPoint();
            logger.info("score:"+one.getPoint());
        }
        RoomResponse res = new RoomResponse(roomdb, users);
        res.setWin(win);
        if (room.getBaoCard() != null)
            res.setBao(room.getGodWealth());
        if (room.getHuCard() != null)//赢牌
            res.setHuCard(room.getHuCard());
        logger.info(res);
        sendMsgForAllSame(roomId, CardCmd.END, res);
        //换庄
        /*if ((room.getWin() == null || room.getPlayerByPosition(room.getBanker()).getPlayerId() != room.getWin()) || room.getPlayerNum() != 4) {*/
            //判断下一个用户是否为第一个坐庄的，是否是最后一轮
            if (/*(room.getFirstBanker() == room.returnNextPositon(room.getBanker()) && room.getRound() == room.getCurrentRount()) ||*/ room.getTotalCount() == room.getCurrentCount()) {
                //房间积分
                roomDao.updateRoomByRoomId(roomId, 2);
                List<UserResponse> total = Lists.newArrayList();
                for (int i = 0; i < room.getPlayers().size(); i++) {
                    PlayerST player = room.getPlayers().get(i);
                    //房间使用完毕,更新用户信息
                    User userdb = userDao.getUserById(player.getPlayerId());
                    userdb.setRoomId(0);
                    userdb.setPoint(0);
                    userDao.updateUser(userdb);
                    //统计积分记录
                    Record all = recordDao.getRoomRecordByUserId(player.getPlayerId(), roomId, 1).get(0);
                    UserResponse user = getUserResponse(all, player.getPlayerId(), null);
                    total.add(user);
                }
                //炮手，大赢家
                getGunner(res, total);
                res.setPlayer(total);
                sendMsgForAllSame(roomId, CardCmd.GAMEOVER, res);
                RoomService.delRoom(roomId);
//                OperationService.delOperation(roomId,room.getCurrentCount());
            }
        /*}*/
        //庄家积分
        RoomInf newRoom =RoomService.getRoom(roomId);
        if(newRoom==null)
            return;
        newRoom.setBankerScore(newRoom.getBankerScore()+score);
        RoomService.setRoom(newRoom);
    }

    private void getGunner(RoomResponse res, List<UserResponse> total) {
        long gunner = total.get(0).getId();
        long winner = gunner;
        int maxguntime = total.get(0).getRecord().getGunTime();//点炮次数
        int maxpoint = total.get(0).getRecord().getPoint();//积分
        for (UserResponse u : total) {
            if (maxpoint < u.getRecord().getPoint()) {
                winner = u.getId();
                maxpoint = u.getRecord().getPoint();
            }
            if (maxguntime < u.getRecord().getGunTime()) {
                gunner = u.getId();
                maxguntime = u.getRecord().getGunTime();
            }
        }
        res.setGunner(gunner);
        res.setBigWiner(winner);
    }

    private void userHistoryCount(RoomInf room, long id) {
        Long win = room.getWin();
        User userdb = userDao.getUserById(id);
        if (win == null)
            userdb.setFleeCount(userdb.getFleeCount() + 1);
        else if (win == id)
            userdb.setWinCount(userdb.getWinCount() + 1);
        else
            userdb.setLostCount(userdb.getLostCount() + 1);
        userDao.updateUser(userdb);
    }

    private void updateUser(long id, Long win, int point) {
        User userdb = userDao.getUserById(id);
        if (win == null)
            userdb.setFleeCount(userdb.getFleeCount() + 1);
        else if (win != id)
            userdb.setLostCount(userdb.getLostCount() + 1);
        else if (win == id)
            userdb.setWinCount(userdb.getWinCount() + 1);
        userdb.setPoint(point);
        userDao.updateUser(userdb);
    }

    private void pointCount(RoomInf room, PlayerST player, Record one) {
        Long win = room.getWin();
        int banker = room.getBanker();
        if (win == null)
            return;
        List<Integer> huType = room.getPlayerByPosition(room.getPositionById(win)).getHuInfo().getHuCards();
        //根據規則，修改huType
        if(room.getRule()!=null)
            huType = changeHuTypeByRule(huType,Lists.newArrayList(room.getRule()));
        Long pao = room.getPao();
        if (win != null && win != 0 && win != player.getPlayerId()) {//输积分计算
            int point = getPoint(isBanker(room,player.getPosition()), huType, pao == null);
            one.setPoint(one.getPoint() - point);
        } else if (win != null && win != 0 && win == player.getPlayerId()) {//获胜积分计算
            int point = getPoint(room, huType, pao, win);
            one.setPoint(one.getPoint() + point);
        }
    }

    private List<Integer> changeHuTypeByRule(List<Integer> huType, List<String> rule){
        List<Integer> type=Lists.newArrayList(huType);
        if (rule.contains(RuleType.QING.getType()+"") && type.contains(HuType.QINGYISE.getType())){
            type.remove((Integer) HuType.QINGYISE.getType());
            type.add(HuType.QINGYISE1.getType());
        }
        if(rule.contains(RuleType.GANG.getType()+"") && type.contains(HuType.GANGKAI.getType())){
            type.remove((Integer) HuType.GANGKAI.getType());
            type.add(HuType.GANGKAI1.getType());
        }
        logger.info("胡牌类型：" + type);
        return type;
    }

    private void winCount(RoomInf room, PlayerST player, Record one) {
        Long win = room.getWin();
        if (win != null && win == player.getPlayerId()) {//该用户自摸计算
            one.setWinTime(one.getWinTime() + 1);
        }
    }

//    private void standCount(PlayerST player, Record one) {
//        if (player.isStand()) {
//            one.setZhanTime(one.getZhanTime() + 1);
//        }
//    }

    private void paoCount(RoomInf room, PlayerST player, Record one) {
        if (room.getPao() != null && room.getPao() == player.getPlayerId()) {//点炮
            one.setGunTime(one.getGunTime() + 1);
        }
    }

    private void bankerCount(RoomInf room, PlayerST player, Record one) {
        if (room.getPlayerByPosition(room.getBanker()).getPlayerId() == player.getPlayerId()) {
            one.setBankerTime(one.getBankerTime() + 1);
        }
    }

    private UserResponse getUserResponse(Record all, long id, PlayerST player) {
        UserResponse user = new UserResponse(userDao.getUserById(id));
        RecordResponse recordResponse = new RecordResponse(all);
//        if (player != null)
//            recordResponse.setTing(player.getStatus() == PlayerType.TING.getType() || player.getStatus() == PlayerType.HU.getType() ? true : false);
        user.setRecord(recordResponse);
        return user;
    }

    private int getPoint(boolean isBanker, List<Integer> huType, boolean isMo) {
        int point = HuType.getScoreByType(huType, isMo);
        if (isBanker)
            point = point * 2;
        if(point>40)
            point=40;
        return point;
    }

    private int getPoint(RoomInf roomInf, List<Integer> huType, Long pao, Long win) {
        int point = 0;
        for (PlayerST p : roomInf.getPlayers()) {
            if (win != p.getPlayerId())
                point += getPoint(isBanker(roomInf, p.getPosition()), huType, pao == null);
        }
        return point;
    }

    private boolean isBanker(RoomInf roomInf, int position) {
        return roomInf.getBanker() == position || roomInf.getBanker() == roomInf.getPositionById(roomInf.getWin());
    }

    //出牌提示
    public List<Tip> setPlayCardTips(long roomId, int outCard) {
        RoomInf room = RoomService.getRoom(roomId);
        List<Tip> tips = new ArrayList<Tip>();
        List<Tip> ishu = playerService.isHu(roomId, outCard, room.returnBeforePositon());
//        List<Tip> isChiTing = playerService.isChiTing(roomId, room.returnBeforePositon());
//        List<Tip> isPengTing = playerService.isPengTing(roomId, room.returnBeforePositon());
        if (ishu != null && ishu.size() > 0)
            tips.addAll(ishu);
//        if (isChiTing != null && isChiTing.size() > 0)
//            tips.addAll(isChiTing);
//        if (isPengTing != null && isPengTing.size() > 0)
//            tips.addAll(isPengTing);
        tips.add(playerService.isChi(roomId, room.returnBeforePositon()));
        tips.add(playerService.isPeng(roomId, outCard, room.returnBeforePositon()));
        tips.add(playerService.isGang(roomId, outCard, room.returnBeforePositon()));
        List<Tip> nullList = new ArrayList<Tip>();
        nullList.add(null);
        tips.removeAll(nullList);
        Collections.sort(tips);
        if (tips.size() > 0) {
            room = RoomService.getRoom(roomId);
            room.setTips(tips);
            RoomService.setRoom(room);
        }
        return tips;
    }


    //抓牌提示
    public List<Tip> setCardTips(long roomId, int inCard) {
        RoomInf room = RoomService.getRoom(roomId);
        List<Tip> tips = new ArrayList<Tip>();
//        tips.add(playerService.isTing(roomId, room.getCurrent()));
        tips.add(playerService.isAnGang(roomId, inCard, room.getCurrent()));
        tips.add(playerService.isMingGang2(roomId));
        List<Tip> ishu = playerService.isHu(roomId, inCard, room.getCurrent());
        if (ishu != null && ishu.size() > 0)
            tips.addAll(ishu);
        List<Tip> nullList = new ArrayList<Tip>();
        nullList.add(null);
        tips.removeAll(nullList);
        Collections.sort(tips);
        if (tips.size() > 0) {
            room = RoomService.getRoom(roomId);
            room.setTips(tips);
            RoomService.setRoom(room);
        }
        return tips;
    }


    //暗槓提示
    public List<Tip> setGangTips(long roomId, int inCard) {
        RoomInf room = RoomService.getRoom(roomId);
        List<Tip> tips = new ArrayList<Tip>();
        tips.add(playerService.isAnGang(roomId, inCard, room.getCurrent()));
        tips.add(playerService.isMingGang2(roomId));
        List<Tip> nullList = new ArrayList<Tip>();
        nullList.add(null);
        tips.removeAll(nullList);
        Collections.sort(tips);
        if (tips.size() > 0) {
            room = RoomService.getRoom(roomId);
            room.setTips(tips);
            RoomService.setRoom(room);
        }
        return tips;
    }


    //財神會提示
    public List<Tip> setCaiShenHuiTips(long roomId) {
        RoomInf room = RoomService.getRoom(roomId);
        List<Tip> tips = new ArrayList<Tip>();
        List<Tip> ishu = playerService.isHu(roomId, room.getCurrent());
        if (ishu != null && ishu.size() > 0)
            tips.addAll(ishu);
        List<Tip> nullList = new ArrayList<Tip>();
        nullList.add(null);
        tips.removeAll(nullList);
        Collections.sort(tips);
        if (tips.size() > 0) {
            room = RoomService.getRoom(roomId);
            room.setTips(tips);
            RoomService.setRoom(room);
        }
        return tips;
    }

    //发送提示
    public void getCardTips(long roomId, List<Tip> data) {
        Collections.sort(data);
        List<Tip> tips = new ArrayList<Tip>();
        //取出优先级最高的用户的提示
        for (Tip t : data) {
            if (tips.size() == 0) {
                tips.add(t);
                continue;
            }
            if (t.getUserid() != tips.get(0).getUserid())
                break;
            tips.add(t);
        }
        //删除要提示的信息并保存
        data.removeAll(tips);
        RoomInf room = RoomService.getRoom(roomId);
        room.setTips(data);
        room.setTempTips(tips);//临时存当前发送的消息
        RoomService.setRoom(room);
        //如果胡
//        if (tips.get(0).getType() == TipType.CANHU.getType() || tips.get(0).getType() == TipType.CANLOUHU.getType()) {
//            for (Card c : tips.get(0).getTipInfos().get(0).getCards()) {
//                if (c.getType() == CardType.BEHU.getType()) {
//                    //发送所有用户手中牌，取消用户准备状态，清空tips，修改房间状态为0
//                    hu(roomId, c.getCards().get(0), tips.get(0).getUserid());
//                    //发送所有用户积分
//                    end(roomId);
//                    return;
//                }
//            }
//        }
        //发送
        sendMsgForOne(CardCmd.TIP, tips);
    }

    //四个人发送不同消息
    public void sendMsgForAll(long roomId, short cmd, List response) {
        List<Result> dataList1 = new ArrayList<Result>();
        for (PlayerST s : RoomService.getRoomInf(roomId)) {
            List<Long> a = new ArrayList<Long>();
            a.add(s.getPlayerId());
            dataList1.add(Result.SUCCESS(response.get(s.getPosition()), Request.valueOf(ModuleId.CARD, cmd, null), a));
        }
        MQProductor.build().send(dataList1, JmsTypeEnum.RADIATE);
    }

    //四个人发送相同消息
    public void sendMsgForAllSame(long roomId, short cmd, Object response) {
        List<Result> dataList1 = new ArrayList<Result>();
        for (PlayerST s : RoomService.getRoomInf(roomId)) {
            List<Long> a = new ArrayList<Long>();
            a.add(s.getPlayerId());
            dataList1.add(Result.SUCCESS(response, Request.valueOf(ModuleId.CARD, cmd, null), a));
        }
        MQProductor.build().send(dataList1, JmsTypeEnum.RADIATE);
    }

    //发消息给一个人
    public void sendMsgForOne(short cmd, List<Tip> response) {
        //提示
        List<Long> a = new ArrayList<Long>();
        a.add(response.get(0).getUserid());
        Result result = Result.SUCCESS(response, Request.valueOf(ModuleId.CARD, cmd, null), a);
        MQProductor.build().send(result, JmsTypeEnum.RADIATE);
    }

    //发消息给一个人
    public void sendMsgForOne(short cmd, long playerId, Object response) {
        //提示
        List<Long> a = new ArrayList<Long>();
        a.add(playerId);
        Result result = Result.SUCCESS(response, Request.valueOf(ModuleId.CARD, cmd, null), a);
        MQProductor.build().send(result, JmsTypeEnum.RADIATE);
    }

    //发消息给一个人
    public void sendMsgForOne(short cmd, long playerId, Object response, Request request) {
        //提示
        List<Long> a = new ArrayList<Long>();
        a.add(playerId);
        Result result = Result.SUCCESS(response, Request.valueOf(ModuleId.CARD, cmd, null,request), a);
        MQProductor.build().send(result, JmsTypeEnum.RADIATE);
    }

    //发消息给一个人
    public void sendMsgForOne(Request request, long playerId, Object response) {
        //提示
        List<Long> a = new ArrayList<Long>();
        a.add(playerId);
        Result result = Result.SUCCESS(response, request, a);
        MQProductor.build().send(result, JmsTypeEnum.RADIATE);
    }

    public long plusOrderId(long roomId) {
        RoomInf room = RoomService.getRoom(roomId);
        room.setOrderId(room.getOrderId() + 1);
        RoomService.setRoom(room);
        return room.getOrderId();
    }

    public void sleep(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
