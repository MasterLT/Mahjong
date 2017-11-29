package com.wasu.game.module.player.untils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wasu.game.domain.*;
import com.wasu.game.enums.CardType;
import com.wasu.game.enums.jy.HuType;
import com.wasu.game.enums.MahjongType;
import com.wasu.game.enums.OperationType;
import com.wasu.game.service.OperationService;
import com.wasu.game.service.RoomService;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by Administrator on 2017/4/12.
 */
public class HuUtils {

    private static Logger logger = Logger.getLogger(HuUtils.class);

    public static List<Integer> getHuType(Integer inCard, long playerId, long roomId) {
        RoomInf roomInf = RoomService.getRoom(roomId);
        List<Integer> huType = win(inCard, playerId, roomInf);
        if (inCard == null)
            huType.add(caiShen(inCard, playerId, roomInf));
        huType.add(ziQing(inCard, playerId, roomInf));
//        huType.add(qingYiSe(inCard, playerId, roomInf));
        huType.addAll(qiDui(inCard, playerId, roomInf));
        huType.add(shiSanYao(inCard, playerId, roomInf));
        huType.add(tianDiGang(playerId, roomInf, huType));
        List<Tip> nullList = new ArrayList<Tip>();
        nullList.add(null);
        huType.removeAll(nullList);
        //十三幺重复
        int i=0;
        for (Integer h:huType){
            if (h==HuType.QINGYISE.getType())
                i++;
        }
        if (i==2)
            huType.remove(new Integer(HuType.QINGYISE.getType()));
        return huType;
    }

    /**
     * 判断自摸，点炮,天胡，地胡，独吊，杠开,大碰胡
     *
     * @param inCard
     * @param playerId
     * @param roomInf
     * @return
     */
    public static List<Integer> win(Integer inCard, long playerId, RoomInf roomInf) {
        List<Integer> res = new ArrayList<Integer>();
        PlayerST player = roomInf.getPlayerByPosition(roomInf.getPositionById(playerId));
//        List<Operation> history = roomInf.getHistory();
        List<Integer> shouPai = Lists.newArrayList(player.getCardsByType(CardType.SHOUPAI.getType()));
        List<Integer> peng = Lists.newArrayList(player.getCardsByType(CardType.PENGPAI.getType()));
        List<Integer> mgang = Lists.newArrayList(player.getCardsByType(CardType.MINGGANG.getType()));
        List<Integer> agang = Lists.newArrayList(player.getCardsByType(CardType.ANGANG.getType()));
        Map<Integer, List<Integer>> ke = Maps.newHashMap();
        List<Integer> cards = Lists.newArrayList(shouPai);
        if (inCard != null)
            cards.add(inCard);
        isHuNew(cards, roomInf.getGodWealth(), ke);
        if (ke.keySet().size() > 0) {
            if (inCard == null) {
                res.add(HuType.ZIMO.getType());//自摸
//                if (history.size() == 2)
//                    res.add(HuType.TIANHU.getType());//天胡
//                int operationType = history.get(history.size() - 2).getType();
//                if (operationType == OperationType.ANGANG.getType() || operationType == OperationType.MINGGANG1.getType() || operationType == OperationType.MINGGANG2.getType())
//                    res.add(HuType.GANGKAI.getType());//杠开
            } else {
                res.add(HuType.DIANPAO.getType());//点炮
//                if (history.size() == 3)
//                res.add(HuType.DIHU.getType());//地胡
            }
            if (shouPai.size() <= 2)
                res.add(HuType.DUDIAO.getType());//独吊
            if (ke.keySet().contains(4 - peng.size() / 3 - mgang.size() / 4 - agang.size() / 4))
                res.add(HuType.DAPENGHU.getType());//大碰胡
            if (qingYiSe(inCard, playerId, roomInf) != null)
                res.add(HuType.QINGYISE.getType());
        }
        return res;
    }

    public static Integer tianDiGang(long playerId, RoomInf roomInf, List<Integer> type) {
        List<Operation> history = OperationService.getOperation(roomInf.getRoomId(),roomInf.getCurrentCount());
        List<Integer> huType = Lists.newArrayList(type);
        List<Tip> nullList = new ArrayList<Tip>();
        nullList.add(null);
        huType.removeAll(nullList);
        if (huType.size() < 1)
            return null;

        if (history.size() == 2)
            return HuType.TIANHU.getType();//天胡

        else if (history.size() == 3 && roomInf.getBanker() != roomInf.getPositionById(playerId))
            return HuType.DIHU.getType();//地胡

        else {
            int operationType = Integer.MAX_VALUE;
            if (history.size() >= 2)
                operationType = history.get(history.size() - 2).getType();
            if (operationType == OperationType.ANGANG.getType() || operationType == OperationType.MINGGANG1.getType() || operationType == OperationType.MINGGANG2.getType())
                return HuType.GANGKAI.getType();//杠开
        }
        return null;
    }

    /**
     * 财神会
     *
     * @param inCard
     * @param playerId
     * @param roomInf
     * @return
     */
    public static Integer caiShen(Integer inCard, long playerId, RoomInf roomInf) {
        int num = 0;
        PlayerST player = roomInf.getPlayerByPosition(roomInf.getPositionById(playerId));
        List<Integer> shouPai = Lists.newArrayList(player.getCardsByType(CardType.SHOUPAI.getType()));
        for (int card : shouPai) {
            if (card == roomInf.getGodWealth())
                num++;
        }
        if (num == 3)
            return HuType.CAISHENHUI.getType();
        return null;
    }

    /**
     * 字清
     *
     * @param inCard
     * @param playerId
     * @param roomInf
     * @return
     */
    public static Integer ziQing(Integer inCard, long playerId, RoomInf roomInf) {
        Integer res = null;
        PlayerST player = roomInf.getPlayerByPosition(roomInf.getPositionById(playerId));
        List<Integer> shouPai = Lists.newArrayList(player.getCardsByType(CardType.SHOUPAI.getType()));
        List<Integer> peng = Lists.newArrayList(player.getCardsByType(CardType.PENGPAI.getType()));
        shouPai.addAll(peng);
        if (inCard != null)
            shouPai.add(inCard);
        if (shouPai.size() == 14) {
            for (int i : shouPai) {
                if (i == roomInf.getGodWealth())
                    continue;
//                if (i == 34 && (roomInf.getGodWealth() <= 27 || roomInf.getGodWealth() == 34))
//                    return null;白板算子清
                else if (i <= 27)
                    return null;
            }
            res = HuType.ZIQING.getType();
        }
        return res;
    }

    /**
     * 清一色
     *
     * @param inCard
     * @param playerId
     * @param roomInf
     * @return
     */
    public static Integer qingYiSe(Integer inCard, long playerId, RoomInf roomInf) {
        Integer res = HuType.QINGYISE.getType();
        PlayerST player = roomInf.getPlayerByPosition(roomInf.getPositionById(playerId));
        List<Integer> shouPai = Lists.newArrayList(player.getCardsByType(CardType.SHOUPAI.getType()));
        shouPai.addAll(Lists.newArrayList(player.getCardsByType(CardType.PENGPAI.getType())));
        shouPai.addAll(Lists.newArrayList(player.getCardsByType(CardType.MINGGANG.getType())));
        shouPai.addAll(Lists.newArrayList(player.getCardsByType(CardType.ANGANG.getType())));
        shouPai.addAll(Lists.newArrayList(player.getCardsByType(CardType.CHIPAI.getType())));
        if (inCard != null)
            shouPai.add(inCard);
        Integer type = null;
        for (int i : shouPai) {
            if (i == roomInf.getGodWealth())
                continue;
            if (type == null)
                type = 34 == i ? (roomInf.getGodWealth() - 1) / 9 : (i - 1) / 9;
            else {
                int current = 34 == i ? (roomInf.getGodWealth() - 1) / 9 : (i - 1) / 9;
                if (current != type)
                    return null;
            }
        }
        return res;
    }

    /**
     * 十三幺
     *
     * @param inCard
     * @param playerId
     * @param roomInf
     * @return
     */
    public static Integer shiSanYao(Integer inCard, long playerId, RoomInf roomInf) {
        Integer res = null;
        PlayerST player = roomInf.getPlayerByPosition(roomInf.getPositionById(playerId));
        List<Integer> shouPai = Lists.newArrayList(player.getCardsByType(CardType.SHOUPAI.getType()));
        if (inCard != null)
            shouPai.add(inCard);
        if (shouPai.size() != 14)
            return null;
        List<Integer> one = Lists.newArrayList();
        List<Integer> two = Lists.newArrayList();
        List<Integer> three = Lists.newArrayList();
        List<Integer> fore = Lists.newArrayList();
        for (int i : shouPai) {
            if (i == roomInf.getGodWealth())
                continue;
//            if (i == 34)十三幺白板不当财神
//                i = roomInf.getGodWealth();
            switch ((i - 1) / 9) {
                case 0:
                    one.add(i);
                    break;
                case 1:
                    two.add(i);
                    break;
                case 2:
                    three.add(i);
                    break;
                case 3:
                    if (fore.contains(i))
                        return null;
                    else
                        fore.add(i);
            }
        }
        if (isNear(one) && isNear(two) && isNear(three))
            res = HuType.SHISANYAO.getType();
        return res;
    }

    private static boolean isNear(List<Integer> list) {
        Collections.sort(list);
        Integer before = null;
        for (Integer i : list) {
            if (before == null)
                before = i;
            else if ((i - before) < 3)
                return false;
            before = i;
        }
        return true;
    }

    /**
     * 七对子
     *
     * @param inCard
     * @param playerId
     * @param roomInf
     * @return
     */
    public static List<Integer> qiDui(Integer inCard, long playerId, RoomInf roomInf) {
        List<Integer> res = Lists.newArrayList();
        PlayerST player = roomInf.getPlayerByPosition(roomInf.getPositionById(playerId));
        List<Integer> shouPai = Lists.newArrayList(player.getCardsByType(CardType.SHOUPAI.getType()));
        if (inCard != null)
            shouPai.add(inCard);
        if (shouPai.size() == 14) {
            int god = roomInf.getGodWealth() - 1;
            int[] cardN = new int[34];
            for (int i : shouPai) {
                cardN[i - 1]++;
            }
            for (int i = 0; i < 34; i++) {
                if (i == god)
                    continue;
                switch (cardN[i]) {
                    case 0:
                        break;
                    case 1: {
                        cardN[god]--;
                        break;
                    }
                    case 2:
                        break;
                    case 3: {
                        cardN[god]--;
                        break;
                    }
                    case 4:
                        break;
                }
            }
            if (cardN[god] >= 0){
                res.add(HuType.QIDUI.getType());
                if (qingYiSe(inCard, playerId, roomInf) != null)
                    res.add(HuType.QINGYISE.getType());
            }
        }
        return res;
    }


    /**
     * 综合判断胡和碰胡
     *
     * @param cards
     * @param god
     * @return
     * @Param ke 刻牌数为key，牌的组合为value
     */
    private static void isHuNew(List<Integer> cards, int god, Map<Integer, List<Integer>> ke) {
        int flat = 0;
        //替换财神为任意牌
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i) == god) {
                for (int j = 1; j < MahjongType.getMahjongTypeByType(1).getCardNum() + 1; j++) {
                    if (j == god || (!cards.contains(j - 1) && !cards.contains(j) && !cards.contains(j + 1) && j != 34))
                        continue;//去除可能的替换
                    List<Integer> newList = Lists.newArrayList(cards);
                    newList.set(i, j);
                    isHuNew(newList, god, ke);
                    if (ke.keySet().contains(4))
                        return;
                }
                flat = 1;
                break;
            }
        }
        //若替换后执行到此则返回false
        if (flat == 1)
            return;
        //白板变财神
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i) == 34)
                cards.set(i, god);
        }
        List<List<Integer>> hasTriplet = Lists.newArrayList();
        if (Mahjong.isHu(cards, null, hasTriplet))
            ke.put(hasTriplet.size(), cards);
        return;
    }

//    /**
//     * 大碰胡(废弃)
//     *
//     * @param inCard
//     * @param playerId
//     * @param roomInf
//     * @return
//     */
//
//    public static Integer DaPengHu(Integer inCard, long playerId, RoomInf roomInf) {
//        Integer res = null;
//        PlayerST player = roomInf.getPlayerByPosition(roomInf.getPositionById(playerId));
//        List<Integer> shouPai = Lists.newArrayList(player.getCardsByType(CardType.SHOUPAI.getType()));
//        shouPai.addAll(Lists.newArrayList(player.getCardsByType(CardType.PENGPAI.getType())));
//        int god = roomInf.getGodWealth();
//        if (inCard != null)
//            shouPai.add(inCard);
//        if (isHu1(shouPai, god))
//            res = HuType.DAPENGHU.getType();
//        return res;
//    }
//
//    /**
//     * 大碰胡(废弃)
//     * @param cards
//     * @param god
//     * @return
//     */
//    private static boolean isHu1(List<Integer> cards, int god) {
//        int flat = 0;
//        //替换财神为任意牌
//        for (int i = 0; i < cards.size(); i++) {
//            if (cards.get(i) == god) {
//                for (int j = 1; j < MahjongType.getMahjongTypeByType(1).getCardNum() + 1; j++) {
//                    if (j == god || (!cards.contains(j - 1) && !cards.contains(j) && !cards.contains(j + 1)))
//                        continue;//去除可能的替换
//                    List<Integer> newList = Lists.newArrayList(cards);
//                    newList.set(i, j);
//                    if (isHu1(newList, god)){
//                        System.out.println(newList);
//                        return true;
//                    }
//                }
//                flat = 1;
//                break;
//            }
//        }
//        //若替换后执行到此则返回false
//        if (flat == 1)
//            return false;
//        //白板变财神
//        for (int i = 0; i < cards.size(); i++) {
//            if (cards.get(i) == 34)
//                cards.set(i, god);
//        }
//        List<List<Integer>> hasTriplet = Lists.newArrayList();
//        boolean hu = Mahjong.isHu(cards, null, hasTriplet);
//        boolean triplet = hasTriplet.size() == 3;
//        return hu && triplet;
//    }
//
//    /**
//     * 判断胡(废弃)
//     *
//     * @param god
//     * @param shouPai
//     * @param inCard
//     * @return
//     */
//    private static boolean isHu(int god, List<Integer> shouPai, Integer inCard) {
//        if (inCard != null)
//            shouPai.add(inCard);
//        return isHu(shouPai, god);
//    }
//
//    private static boolean isHu(List<Integer> cards, int god) {
//        int flat = 0;
//        //替换财神为任意牌
//        for (int i = 0; i < cards.size(); i++) {
//            if (cards.get(i) == god) {
//                for (int j = 1; j < MahjongType.getMahjongTypeByType(1).getCardNum() + 1; j++) {
//                    if (j == god || (!cards.contains(j - 1) && !cards.contains(j) && !cards.contains(j + 1)))
//                        continue;//去除可能的替换
//                    List<Integer> newList = Lists.newArrayList(cards);
//                    newList.set(i, j);
//                    if (isHu(newList, god)){
//                        System.out.println(newList);
//                        return true;
//                    }
//                }
//                flat = 1;
//                break;
//            }
//        }
//        //若替换后执行到此则返回false
//        if (flat == 1)
//            return false;
//        //白板变财神
//        for (int i = 0; i < cards.size(); i++) {
//            if (cards.get(i) == 34)
//                cards.set(i, god);
//        }
//        return Mahjong.isHu(cards, null, Lists.<List<Integer>>newArrayList());
//    }

    public static void main(String[] args) {
//        PlayerService playerService = SessionManager.ini().getBean(PlayerService.class);
//        playerService.isMingGang2(479L);
//        shiSanYao(19, 597L, SessionManager.getRoom(456));
//        Map<Integer, List<Integer>> map=Maps.newHashMap();
//        isHuNew(Lists.newArrayList(8, 9, 11, 11, 7), 7, map);
        List<Integer> shouPai = Lists.newArrayList(1, 1, 2, 2, 3, 3, 4, 4);
        int god = 10;
        int[] cardN = new int[34];
        for (int i : shouPai) {
            cardN[i - 1]++;
        }
        for (int i = 0; i < 34; i++) {
            if (i == god)
                continue;
            switch (cardN[i]) {
                case 1: {
                    cardN[god]--;
                    break;
                }
                case 2:
                    break;
                case 3: {
                    cardN[god]--;
                    break;
                }
            }
        }
        System.out.print(cardN[god]);
    }
}
