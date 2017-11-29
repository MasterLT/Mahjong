package com.wasu.game.utilities;

import com.google.common.collect.Lists;
import com.wasu.game.domain.HuInfo;
import com.wasu.game.domain.PlayerST;
import com.wasu.game.domain.RoomInf;
import com.wasu.game.enums.CardType;
import com.wasu.game.enums.heb.HuType;
import com.wasu.game.enums.PlayerType;
import com.wasu.game.enums.heb.RuleType;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Administrator on 2017/1/13.
 */
public class RoomRuleUtils {
    private String[] rules;

    public RoomRuleUtils(String[] rules) {
        this.rules = rules;
    }

    public RoomRuleUtils() {
    }

    /**
     * 红中满天飞
     *
     * @return
     */
    public static boolean hongzhong(int inCard, long playerId, RoomInf roomInf) {
        if (inCard == 33)
            return true;
        return false;
    }

    /**
     * 刮大风
     *
     * @return
     */
    public static boolean feng(int inCard, long playerId, RoomInf roomInf) {
        PlayerST player = roomInf.getPlayerByPosition(roomInf.getPositionById(playerId));

        //碰牌
        if (player.getCardsByType(CardType.PENGPAI.getType()).contains(inCard))
            return true;
        //刻牌
        List<Integer> shoupai = player.getCardsByType(CardType.SHOUPAI.getType());
        HuInfo huInfo = player.getHuInfo();
        if (huInfo != null && huInfo.getHuCards() != null) {
            List<Integer> huCards = huInfo.getHuCards();
            for (Integer hu : huCards) {
                List<Integer> win = Lists.newArrayList(shoupai);
                win.remove(new Integer(inCard));
                win.add(hu);
                HashSet<List<Integer>> list = getKe(win);
                for (List<Integer> l : list) {
                    if (l.contains(inCard)) {
                        if (hu.equals(inCard) && countNum(win, inCard) != 4)
                            continue;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 摸宝
     *
     * @return
     */

    public static boolean moBao(int inCard, long playerId, RoomInf roomInf) {
        int bao = roomInf.getBaoCard();
        if (inCard == bao)
            return true;
        return false;
    }

    /**
     * 宝中宝
     *
     * @return
     */
    public static boolean baoZhongBao(int inCard, long playerId, RoomInf roomInf) {
        int bao = roomInf.getBaoCard();
        if (inCard == bao && roomInf.getPlayerByPosition(roomInf.getPositionById(playerId)).getHuInfo().isJia() && roomInf.getPlayerByPosition(roomInf.getPositionById(playerId)).getHuInfo().getHuCards().contains(inCard))
            return true;
        return false;
    }


    /**
     * 判断夹胡
     *
     * @param
     * @param cards   赢得所有牌
     * @param winCard 要赢得牌
     * @param is37    是否要求37夹，不要求传入null
     * @return
     */
    public static boolean isJia(List<Integer> cards, int winCard, Integer is37) {
        List<Integer> cardsCopy = Lists.newArrayList(cards);
        cardsCopy.add(winCard);
        HashSet<List<Integer>> line = getLine(cardsCopy, winCard);
        System.out.println("所有可能的顺子：" + line);
        List<List<Integer>> winList = Lists.newArrayList();
        // 包含赢牌的顺子
        for (List<Integer> l : line) {
            if (l != null && l.contains(winCard))
                winList.add(l);
        }
        if (winList.size() == 1) {
            if (winCard == winList.get(0).get(1))
                return true;
            if (is37 != null && (winCard % 9 == 3 || winCard % 9 == 7))
                return true;
        }
        return false;
    }

    private static boolean getLine(int[] cardN, int len, HashSet<List<Integer>> hasTriplet) {
        boolean res = false;
        //没有将牌是否  恰好组成顺子或者是三个的
        //递归结束
        if (len == 0) {
            return true;
        }
        //第一张牌位置
        int position = 0;
        for (int i = 0; i < cardN.length; i++) {
            if (cardN[i] > 0) {
                position = i;
                break;
            }
        }
        int p = position + 1;
        //该牌为刻牌
        if (cardN[position] >= 3) {
            cardN[position] -= 3;
            if (getLine(cardN, len - 3, hasTriplet)) {
                res = true;
            }
            cardN[position] += 3;
        }
        //该牌不为刻牌且为8,9或红中
        if (p % 9 == 8 || (p != 0 && p % 9 == 0) || p > 27) {//在边界8，9// 33为中
            return res;
        }
        //该牌组成顺子
        if (cardN[position] > 0 && cardN[position + 1] > 0 && cardN[position + 2] > 0) {
            cardN[position] -= 1;
            cardN[position + 1] -= 1;
            cardN[position + 2] -= 1;
            if (getLine(cardN, len - 3, hasTriplet)) {
                res = true;
                hasTriplet.add(Lists.<Integer>newArrayList(position + 1, position + 2, position + 3));
            }
            cardN[position] += 1;
            cardN[position + 1] += 1;
            cardN[position + 2] += 1;
        }
        return res;
    }

    private static boolean getKe(int[] cardN, int len, HashSet<List<Integer>> hasTriplet) {
        boolean res = false;
        //没有将牌是否  恰好组成顺子或者是三个的
        //递归结束
        if (len == 0) {
            return true;
        }
        //第一张牌位置
        int position = 0;
        for (int i = 0; i < cardN.length; i++) {
            if (cardN[i] > 0) {
                position = i;
                break;
            }
        }
        int p = position + 1;
        //该牌为刻牌
        if (cardN[position] >= 3) {
            cardN[position] -= 3;
            if (getKe(cardN, len - 3, hasTriplet)) {
                res = true;
                hasTriplet.add(Lists.<Integer>newArrayList(position + 1, position + 1, position + 1));
            }
            cardN[position] += 3;
        }
        //该牌不为刻牌且为8,9或红中
        if (p % 9 == 8 || (p != 0 && p % 9 == 0) || p > 27) {//在边界8，9// 33为中
            return res;
        }
        //该牌组成顺子
        if (cardN[position] > 0 && cardN[position + 1] > 0 && cardN[position + 2] > 0) {
            cardN[position] -= 1;
            cardN[position + 1] -= 1;
            cardN[position + 2] -= 1;
            if (getKe(cardN, len - 3, hasTriplet)) {
                res = true;
            }
            cardN[position] += 1;
            cardN[position + 1] += 1;
            cardN[position + 2] += 1;
        }
        return res;
    }

    private static HashSet<List<Integer>> getKe(List<Integer> cards) {
        HashSet<List<Integer>> hasTriplet = new HashSet<List<Integer>>();
        int[] cardN = new int[34];
        int length = cards.size();
        //牌转换
        for (int i = 0; i < cards.size(); i++) {
            cardN[cards.get(i) - 1]++;
        }
        List<Integer> duiJiang = new ArrayList<Integer>();
        for (int i = 0; i < cardN.length; i++) {
            if (cardN[i] >= 2) {
                duiJiang.add(i);
            }
        }
        //遍历每个可以成为一对将  是否胡牌
        for (int i = 0; i < duiJiang.size(); i++) {
            cardN[duiJiang.get(i)] -= 2;
            //计算所有赢牌组合中顺子
            getKe(cardN, length - 2, hasTriplet);
            cardN[duiJiang.get(i)] += 2;
        }
        return hasTriplet;
    }

    public static HashSet<List<Integer>> getLine(List<Integer> cards, int winCard) {
        HashSet<List<Integer>> hasTriplet = new HashSet<List<Integer>>();
        int[] cardN = new int[34];
        int length = cards.size();
        winCard = winCard - 1;
        //牌转换
        for (int i = 0; i < cards.size(); i++) {
            cardN[cards.get(i) - 1]++;
        }
        List<Integer> duiJiang = new ArrayList<Integer>();
        for (int i = 0; i < cardN.length; i++) {
            if (cardN[i] >= 2) {
                duiJiang.add(i);
            }
        }
        //遍历每个可以成为一对将  是否胡牌
        for (int i = 0; i < duiJiang.size(); i++) {
            if (duiJiang.get(i) == winCard)
                continue;
            cardN[duiJiang.get(i)] -= 2;
            //计算所有赢牌组合中顺子
            getLine(cardN, length - 2, hasTriplet);
            cardN[duiJiang.get(i)] += 2;
        }
        return hasTriplet;
    }

    public static int isLouHu(RoomInf roomInf, Long id, List<Integer> rules) {
        PlayerST playerST = roomInf.getPlayerByPosition(roomInf.getPositionById(id));
        if (CollectionUtils.isEmpty(rules) || !rules.contains(RuleType.LOU.getType())) {
            return -1;
        }
        if (playerST.getStatus() != PlayerType.TING.getType() || !playerST.getHuInfo().isJia()) {
            return -1;
        }
        if (feng(roomInf.getBaoCard(), id, roomInf)) {
            return HuType.LOUHU2.getType();
        }
        if (hongzhong(roomInf.getBaoCard(), id, roomInf))
            return HuType.LOUHU3.getType();
        if (roomInf.getBaoCard() == playerST.getHuInfo().getHuCards().get(0))
            return HuType.LOUHU1.getType();
        return -1;
    }

    public static void calculateStand(RoomInf roomInf) {
        for (int i = 0; i < roomInf.getPlayers().size(); i++) {
            PlayerST playerST = roomInf.getPlayers().get(i);
            if (stand(playerST)) {
                playerST.setStand(true);
            } else {
                playerST.setStand(false);
            }
        }
    }

    private static boolean stand(PlayerST playerST) {
        List<Integer> chi = playerST.getCardsByType(CardType.CHIPAI.getType());
        List<Integer> peng = playerST.getCardsByType(CardType.PENGPAI.getType());
        List<Integer> an = playerST.getCardsByType(CardType.ANGANG.getType());
        List<Integer> ming = playerST.getCardsByType(CardType.MINGGANG.getType());
        if (CollectionUtils.isEmpty(chi) && CollectionUtils.isEmpty(peng) && CollectionUtils.isEmpty(an) && CollectionUtils.isEmpty(ming))
            return true;
        return false;
    }

    private static int countNum(List<Integer> list, int card) {
        int num = 0;
        for (int i : list) {
            if (i == card)
                num++;
        }
        return num;
    }

    public static void main(String[] args) {
        List<Integer> cards = Lists.newArrayList(5, 5, 5, 6, 7, 7, 7, 7, 8, 9, 9);
        System.out.print(isJia(cards, 7, null));

    }
}
