package com.wasu.game.domain;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wasu.game.enums.*;
import com.wasu.game.enums.heb.RuleType;
import com.wasu.game.exception.ErrorCodeException;
import com.wasu.game.rules.*;
import com.wasu.game.utilities.RoomRuleUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;

public class Mahjong {

    private int type;
    private List<Card> cards;
    private String name;
    private int playerCardNum;


    @Override
    public String toString() {
        return "Mahjong [type=" + type + ", cards=" + cards + ", name=" + name + "]";
    }

    public Mahjong(int type, List<Card> cards, String name) {
        super();
        this.type = type;
        this.cards = cards;
        this.name = name;
    }

    public Mahjong() {
        super();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 洗牌
     *
     * @param type
     * @return
     */
    public static Queue<Integer> shuffle(int type) {
        Queue<Integer> all1 = new LinkedList<Integer>();
        List<Integer> all = new ArrayList<Integer>();
        for (int i = 0; i < 4; i++) {
//            all.add(33);//哈尔滨麻将  中
            for (int j = 1; j < MahjongType.getMahjongTypeByType(type).getCardNum() + 1; j++) {
                all.add(j);
            }
        }
        Collections.shuffle(all);
        for (int i = 0; i < all.size(); i++) {
            all1.add(all.get(i));
        }
        return all1;
    }

    /**
     * 判断是否有碰  如果有返回可以碰的用户
     *
     * @param playerSTs
     * @param cardNum
     * @param position
     * @return
     */
    public static PlayerST isPeng(List<PlayerST> playerSTs, int cardNum, int position) {
        PlayerST playerST = null;
        for (int i = 0; i < playerSTs.size(); i++) {
            PlayerST tempPlayerST = playerSTs.get(i);
            if (tempPlayerST.getPosition() == position) {
                continue;
            }
            //如果听了  跳过
//            if (tempPlayerST.getStatus() == PlayerType.TING.getType())
//                continue;
            //获取手牌
            List<Integer> cards = tempPlayerST.getCardsByType(CardType.SHOUPAI.getType());
            //手牌数不大于四 不能碰
//            if (cards.size() <= 4)
//                continue;
            if (isPeng(cards, cardNum)) {
                playerST = tempPlayerST;
            }
        }
        return playerST;
    }


    public static List<List<Integer>> isChi(RoomInf roomInf, int prePosition, PlayerST playerST) {

        List<List<Integer>> chiList = new ArrayList<List<Integer>>();
        PlayerST prePlayerST = roomInf.getPlayerByPosition(prePosition);
        int cardNum = prePlayerST.getCardsByType(CardType.YICHUPAI.getType()).get(prePlayerST.getCardsByType(CardType.YICHUPAI.getType()).size() - 1);
        List<Integer> shoupai=playerST.getCardsByType(CardType.SHOUPAI.getType());
        if (cardNum == roomInf.getGodWealth()) {//出财神不能吃碰
            return null;
        }
        //修改手牌白板变财神
        if(cardNum==34)
            cardNum=roomInf.getGodWealth();
        shoupai=conversionBlank(shoupai,roomInf.getGodWealth());
        //判断
        chiList = isChi(shoupai, cardNum);
        //返回值中财神变白板
        for(int i=0;i<chiList.size();i++){
            for(int j=0;j<chiList.get(i).size();j++){
                if(chiList.get(i).get(j)==roomInf.getGodWealth())
                    chiList.get(i).set(j,34);
            }
        }
        return chiList;

    }

    /**
     * @param cards
     * @param cardNum
     * @return
     */
    public static List<List<Integer>> isChi(List<Integer> cards, int cardNum) {
        List<List<Integer>> chiList = new ArrayList<List<Integer>>();
        if(cardNum>27)
            return chiList;
        switch (cardNum % 9) {

            case 0:

                if (getNearCard(cards, cardNum - 1, cardNum - 2) != null && getNearCard(cards, cardNum - 1, cardNum - 2).size() > 0) {

                    chiList.add(getNearCard(cards, cardNum - 1, cardNum - 2));
                }
                break;
            case 1:
                if (getNearCard(cards, cardNum + 1, cardNum + 2) != null && getNearCard(cards, cardNum + 1, cardNum + 2).size() > 0) {
                    chiList.add(getNearCard(cards, cardNum + 1, cardNum + 2));
                }
                break;
            case 2:
                if (getNearCard(cards, cardNum - 1, cardNum + 1) != null && getNearCard(cards, cardNum - 1, cardNum + 1).size() > 0) {
                    chiList.add(getNearCard(cards, cardNum - 1, cardNum + 1));
                }
                if (getNearCard(cards, cardNum + 1, cardNum + 2) != null && getNearCard(cards, cardNum + 1, cardNum + 2).size() > 0) {
                    chiList.add(getNearCard(cards, cardNum + 1, cardNum + 2));
                }
                break;
            case 8:
                if (getNearCard(cards, cardNum - 1, cardNum + 1) != null && getNearCard(cards, cardNum - 1, cardNum + 1).size() > 0) {
                    chiList.add(getNearCard(cards, cardNum - 1, cardNum + 1));
                }
                if (getNearCard(cards, cardNum - 1, cardNum - 2) != null && getNearCard(cards, cardNum - 1, cardNum - 2).size() > 0) {
                    chiList.add(getNearCard(cards, cardNum - 1, cardNum - 2));
                }
                break;
            default:
                if (getNearCard(cards, cardNum - 1, cardNum - 2) != null && getNearCard(cards, cardNum - 1, cardNum - 2).size() > 0) {
                    chiList.add(getNearCard(cards, cardNum - 1, cardNum - 2));
                }
                if (getNearCard(cards, cardNum - 1, cardNum + 1) != null && getNearCard(cards, cardNum - 1, cardNum + 1).size() > 0) {
                    chiList.add(getNearCard(cards, cardNum - 1, cardNum + 1));
                }
                if (getNearCard(cards, cardNum + 1, cardNum + 2) != null && getNearCard(cards, cardNum + 1, cardNum + 2).size() > 0) {
                    chiList.add(getNearCard(cards, cardNum + 1, cardNum + 2));
                }

        }


        return chiList;

    }

    /**
     * 集合cards 中是否同时包含int1  int2
     *
     * @param cards
     * @param int1
     * @param int2
     * @return
     */
    public static List<Integer> getNearCard(List<Integer> cards, Integer int1, Integer int2) {
        List<Integer> cardList = new ArrayList<Integer>();
        if (cards.contains(int1) && cards.contains(int2)) {
            cardList.add(int1);
            cardList.add(int2);
        }
        return cardList;
    }

    /**
     * 判断打的牌是否可以杠是否可以杠
     * 别家打的牌 判断是否可以杠
     *
     * @param playerSTs
     * @param cardNum
     * @param position
     * @return
     */
    public static PlayerST isMingGang(List<PlayerST> playerSTs, int cardNum, int position) {
        PlayerST playerST = null;
        for (int i = 0; i < playerSTs.size(); i++) {
            PlayerST tempPlayerST = playerSTs.get(i);
            //获取手牌
            List<Integer> cards = tempPlayerST.getCardsByType(CardType.SHOUPAI.getType());
            if (tempPlayerST.getPosition() == position) {
                continue;
            }
//            if (tempPlayerST.getStatus() == PlayerType.TING.getType()) {
//                continue;
//            }
//            if (cards.size()<5){
//                continue;
//            }
            if (isMingGang(cards, cardNum)) {
                playerST = tempPlayerST;
            }
        }
        return playerST;
    }

    /**
     * 判断是否可以碰
     *
     * @param cards
     * @param cardNum
     * @return
     */
    private static boolean isPeng(List<Integer> cards, int cardNum) {
        boolean isPeng = false;
        int num = 0;
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i) == cardNum) {
                num += 1;
            }
        }
        isPeng = num >= 2 ? true : false;
        return isPeng;
    }

    /**
     * 判断是否可以杠
     *
     * @param cards
     * @param cardNum
     * @return
     */
    private static boolean isMingGang(List<Integer> cards, int cardNum) {
        boolean isMingGang = false;
        int num = 0;
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i) == cardNum) {
                num += 1;
            }
        }
        isMingGang = num == 3 ? true : false;
        return isMingGang;
    }

//    public static List<PlayerST> isHu(LinkedList<PlayerST> playerSTs, Integer cardNum, int prePostion) {
//        List<PlayerST> playerSTList = new ArrayList<PlayerST>();
//        for (int i = 0; i < playerSTs.size(); i++) {
//
//            PlayerST playerST = playerSTs.get(i);
//            if (playerST.getPosition() == prePostion)
//                continue;
//            if (playerST.getStatus() != PlayerType.TING.getType())
//                continue;
//            //获取手牌
//            List<Integer> cards = playerST.getCardsByType(CardType.SHOUPAI.getType());
//
//            if (isHu(cards, cardNum, playerST)) {
//                playerSTList.add(playerST);
//            }
//        }
//        return playerSTList;
//    }

    //不是碰听的时候使用
    public static boolean isHu(List<Integer> cards, Integer cardNum, PlayerST playerST,int tingType,Integer addCard,Integer outCard) {
        List<List<Integer>> hasTriplet = Lists.newArrayList();
        if (isHu(cards, cardNum, hasTriplet)) {
            BaseRule rule = getHuRule(tingType,hasTriplet);//根据听的类型 获得判定的规则
            if (rule.handle(playerST, cardNum,addCard,outCard)) {
                return true;
            }
        }
        return false;
    }

    //碰听的时候判断
    public static boolean isPengTingHu(List<Integer> cards, Integer cardNum, PlayerST playerST,BaseRule rule) {
        List<List<Integer>> hasTriplet = Lists.newArrayList();
        if (rule.handle(playerST, cardNum)) {
            if (isHu(cards, cardNum, hasTriplet)) {
                return true;
            }
        }
        return false;
    }
    public static BaseRule getHuRule(int tingType,List<List<Integer>> hasTriplet){
        BaseRule rule=null;
        if (tingType==TipType.CANTING.getType()){
            rule = new Line();
            rule.setNextRule(new OneOrNine()).setNextRule(new Suit()).setNextRule(new PengGang(hasTriplet));
        }else if (tingType==TipType.CANCHITING.getType()){
            rule = new OneOrNine();
            rule.setNextRule(new Suit()).setNextRule(new PengGang(hasTriplet));
        }else if (tingType==TipType.CANPENGTING.getType()){
            rule = new Line();
            rule.setNextRule(new Suit()).setNextRule(new OneOrNine());
        }
        return rule;
    }

    public static boolean isHu(List<Integer> cards, Integer cardNum, List<List<Integer>> hasTriplet) {
        boolean isHu = false;
        int[] cardN = new int[34];
        int length = 0;
        for (int i = 0; i < cards.size(); i++) {
            cardN[cards.get(i) - 1]++;
        }
        if (cardNum != null) {
            cardN[cardNum - 1]++;
            length = cards.size() + 1;
        } else {
            length = cards.size();
        }
        List<Integer> duiJiang = new ArrayList<Integer>();
        for (int i = 0; i < cardN.length; i++) {
            if (cardN[i] >= 2) {
                duiJiang.add(i);
            }
        }
        if (duiJiang.size() > 0) {
            //遍历每个可以成为一对将  是否胡牌
            for (int i = 0; i < duiJiang.size(); i++) {
                cardN[duiJiang.get(i)] -= 2;
                if (isComplete(cardN, length - 2, hasTriplet)) {
                    //可以判定最大翻数的 赢法   未做
                    cardN[duiJiang.get(i)] += 2;
                    return true;
                }
                cardN[duiJiang.get(i)] += 2;
            }
        }
        return isHu;
    }

    public static boolean isComplete(int[] cardN, int len) {
        return isComplete(cardN, len, null);
    }


    private static boolean isComplete(int[] cardN, int len, List<List<Integer>> hasTriplet) {

        //没有将牌是否  恰好组成顺子或者是三个的
        // TODO Auto-generated method stub
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
            if (isComplete(cardN, len - 3, hasTriplet)) {
                hasTriplet.add(Lists.<Integer>newArrayList(position, position, position));
                return true;
            }
            cardN[position] += 3;
        }
        //该牌不为刻牌且为8,9或红中
        if (p % 9 == 8 || (p != 0 && p % 9 == 0) || p > 27) {//在边界8，9// 33为中
            return false;
        }
        //该牌组成顺子
        if (cardN[position] > 0 && cardN[position + 1] > 0 && cardN[position + 2] > 0) {
            cardN[position] -= 1;
            cardN[position + 1] -= 1;
            cardN[position + 2] -= 1;

            if (isComplete(cardN, len - 3, hasTriplet)) {
//                hasTriplet.add(Lists.<Integer>newArrayList(position,position+1,position+2));
                return true;
            }
            cardN[position] += 1;
            cardN[position + 1] += 1;
            cardN[position + 2] += 1;
        }
        return false;
    }

    public static List<Integer> isAnGang(PlayerST player, int cardNum) {
        if (player.getStatus() == PlayerType.TING.getType())
            return null;
        List<Integer> cards = player.getCardsByType(CardType.SHOUPAI.getType());
        if(cards.size()<=4)
            return null;
        return isAnGang(cards, cardNum);
    }

    private static List<Integer> isAnGang(List<Integer> cards, int cardNum) {
        List<Integer> isAnGang = Lists.newArrayList();
        List<Integer> shouPai=Lists.newArrayList(cards);
//        shouPai.add(cardNum);
        int[] cardN = new int[34];
        for (int i = 0; i < shouPai.size(); i++) {
            cardN[shouPai.get(i) - 1]++;
        }
        for (int i=0;i<cardN.length;i++){
            if (cardN[i]==4){
                isAnGang.add(i+1);
            }
        }
        return isAnGang;
    }


    public static List<Integer> isTing(List<Integer> cards, PlayerST player, String[] rules) {
        List<Integer> outCard = null;
        if (player.getStatus() == PlayerType.TING.getType())
            return null;

        outCard = chiOrPengTing(cards, player,rules,TipType.CANTING.getType(),null);
        return outCard;
    }


    public static List<Integer> getWinCards(List<Integer> cards, PlayerST playerST,int tingType,Integer addCard,Integer outCard) {
        List<Integer> winCards = new ArrayList<Integer>();
        for (int i = 1; i < MahjongType.BASE.getCardNum() + 1; i++) {
//            List<List<Integer>> hasTriplet = Lists.newArrayList();  //刻牌 缓存
            if (isHu(cards,i,playerST,tingType,addCard,outCard)){
                winCards.add(i);
            }
        }
        if (isHu(cards,33,playerST,tingType,addCard,outCard)) {//是否赢中
            winCards.add(33);
        }
        return winCards;
    }

//    public static List<Integer> getWinCards(List<Integer> cards, PlayerST playerST) {
//        List<Integer> winCards = new ArrayList<Integer>();
//        for (int i = 1; i < MahjongType.BASE.getCardNum() + 1; i++) {
//            List<List<Integer>> hasTriplet = Lists.newArrayList();  //刻牌 缓存
//            if (isHu(cards, i, hasTriplet)) {
//                winCards.add(i);
//            }
//        }
//        List<List<Integer>> hasTriplet1 = Lists.newArrayList();
//        if (isHu(cards, 33, hasTriplet1)) {//是否赢中
//            winCards.add(33);
//        }
//        return winCards;
//    }

//    private static boolean huAfterRules(PlayerST playerST,Integer cardNum,List<Integer> cards,List<List<Integer>> hasTriplet ) {
//        BaseRule line = new Line();
//        line.setNextRule(new OneOrNine()).setNextRule(new Triplet()).setNextRule(new Suit());
//        if (line.handle(playerST, cardNum)) {
//            if (isHu(cards, cardNum, hasTriplet)) {
//                BaseRule pengGang = new PengGang();
//                if (CollectionUtils.isEmpty(hasTriplet) && !pengGang.handle(playerST, cardNum)) {
//                    return false;
//                }
//                return true;
//            }
//        }
//        return false;
//    }


    public static Map<Integer, List<List<Card>>> isChiTing(LinkedList<PlayerST> players, int cardNum, int prePosition, String[] rules) {
        Map<Integer, List<List<Card>>> map = new HashMap<Integer, List<List<Card>>>();
        List<List<Integer>> cardList = new ArrayList<List<Integer>>();
        for (int i = 0; i < players.size(); i++) {
            //需要判定的玩家
            PlayerST player = players.get(i);
            //该玩家手牌
            List<Integer> shouPai = player.getCardsByType(CardType.SHOUPAI.getType());
            if (player.getPosition() == prePosition)//出牌的那个人  跳过
                continue;
            //如果已经听了  跳过
            if (player.getStatus() == PlayerType.TING.getType()) {
                continue;
            }
            if (!(shouPai.size() > 4)) {//手牌为四张的时候不能吃或者碰
                continue;
            }

            //克隆手牌  单独去判定
            List<Integer> temp = new ArrayList<Integer>(shouPai);
            List<List<Integer>> list = isChi(shouPai, cardNum);
            if (CollectionUtils.isEmpty(list)) //判定吃时 返回的list 是否为空
                continue;

            //吃听判断
            List<List<Card>> result = chiOrPengTing(list, temp, player, TipType.CANCHITING.getType(), rules, cardNum);
            if (result != null && result.size() > 0) {
                map.put(player.getPosition(), result);
            }
        }
        return map;
    }

    /**
     * 判定可以吃或者碰后  判定可不可以听
     *
     * @param list    吃或者碰的组合
     * @param shouPai //手牌
     * @param player  //该玩家
     * @param type    是吃还是碰
     * @return
     */
    private static List<List<Card>> chiOrPengTing(List<List<Integer>> list, List<Integer> shouPai, PlayerST player, int type, String[] rules, Integer addCard) {
        List<ChiOrPengTingInfo> chiOrPengTingInfolist = player.getChiOrPengTingInfos();
        if (CollectionUtils.isEmpty(chiOrPengTingInfolist))
            chiOrPengTingInfolist = Lists.newArrayList();
        List<List<Card>> result = new ArrayList<List<Card>>();
        if (CollectionUtils.isEmpty(list))// 碰或者吃 为空时
            return null;
        for (int i = 0; i < list.size(); i++) {
            //获取手牌
            List<Integer> inHandCards = player.getCardsByType(CardType.SHOUPAI.getType());
            //用temp代替手牌进行操作
            List<Integer> temp = new ArrayList<Integer>(inHandCards);//手牌
            List<Integer> cards = list.get(i);//获得第一组 吃或者碰的牌
            List<Card> cardList = new ArrayList<Card>();
            for (int j = 0; j < cards.size(); j++) {
                Integer card = cards.get(j);
                if (temp.contains(card)) {
                    temp.remove(card);// 组合从手牌数组中删除
                }
            }
            //outCard 要出的牌
            List<Integer> outCard = chiOrPengTing(temp, player,rules,type,addCard);
            if (!CollectionUtils.isEmpty(outCard)) {
                Map<Integer, HuInfo> tempMap = player.getTempHuInfo();
                if (CollectionUtils.isEmpty(tempMap)) {
                    throw new ErrorCodeException(ResultCode.UNTING);
                }
                List<OutAndWin> outAndWins = Lists.newArrayList();
                for (Map.Entry<Integer, HuInfo> entry : tempMap.entrySet()) {
                    OutAndWin outAndWin = new OutAndWin(entry.getKey(), entry.getValue());
                    outAndWins.add(outAndWin);
                }
                ChiOrPengTingInfo chiOrPengTingInfo = new ChiOrPengTingInfo(cards, outAndWins);
                chiOrPengTingInfolist.add(chiOrPengTingInfo);
//                chiOrPeng.put(cards,tempMap);

                //构成Card牌
                Card chiPai = new Card(type, cards, cards.size());
                Card outPaiCards = new Card(TipType.OUTOFTING.getType(), outCard, outCard.size());
//                System.out.print(outCard);
                cardList.add(chiPai);
                cardList.add(outPaiCards);
                result.add(cardList);
            }
        }
        if (!CollectionUtils.isEmpty(chiOrPengTingInfolist))
            player.setChiOrPengTingInfos(chiOrPengTingInfolist);
        return result;
    }

    /**
     * 判定pai中 出那张牌可以听   如果没有返回null
     *
     * @param pai
     * @return
     */
    private static List<Integer> chiOrPengTing(List<Integer> pai, PlayerST playerST, String[] rules,int tingType, Integer addCard) {

        //判断是否是夹胡  和规则
        boolean isJia = false;//不夹不胡
        Integer jia2 = null;//三七夹
        //获得rules
        List<Integer> intRules = getRules(rules);
        if (!CollectionUtils.isEmpty(intRules)) {
            if (intRules.contains(RuleType.JIA.getType()))
                isJia = true;
            if (intRules.contains(RuleType.JIA2.getType()))
                jia2 = RuleType.JIA2.getType();
        }

        List<Integer> outCards = Lists.newArrayList();//出什么牌就可以听 的组合
//        //临时保存赢的牌
//        Map<Integer,List<Integer>> tempHuMap= Maps.newHashMap();
        //临时保存是否夹牌
        Map<Integer, HuInfo> tempHuInfo = Maps.newHashMap();

        if (CollectionUtils.isEmpty(pai))//判断牌是否为空
            return null;
        for (Integer card : pai) {
            //如果card 此牌已经在outCards中了 跳过
            if (outCards.contains(card))
                continue;
            //复制pai中的数 到temp1中
            List<Integer> temp1 = new ArrayList<Integer>(pai);
            /**
             * 删除此牌  然后判定是否可以听
             */
            temp1.remove(card);

            List<Integer> winCards = getWinCards(temp1, playerST,tingType,addCard,card);

            if (isJia) {  //有没有不胡不夹的规则
                if (winCards != null && winCards.size() == 1) { //赢一张牌
                    if (RoomRuleUtils.isJia(temp1, winCards.get(0), jia2)) {
                        HuInfo huInfo = null;
                        huInfo = new HuInfo(winCards, true);
                        tempHuInfo.put(card, huInfo);
//                        tempHuMap.put(card,winCards);
                        outCards.add(card);
                    }
                }
            } else {
                if (!CollectionUtils.isEmpty(winCards)) {
                    HuInfo huInfo = null;
                    int winCard = winCards.get(0);
                    if (winCards.size()==1&&RoomRuleUtils.isJia(temp1, winCard, jia2)) {
                        huInfo = new HuInfo(winCards, true);
                    } else {
                        huInfo = new HuInfo(winCards, false);
                    }
                    tempHuInfo.put(card, huInfo);
//                    tempHuMap.put(card,winCards);
                    outCards.add(card);
                }
            }
        }
//        if(!CollectionUtils.isEmpty(tempHuMap))
//            playerST.setTempHuCard(tempHuMap);
        if (!CollectionUtils.isEmpty(tempHuInfo))
            playerST.setTempHuInfo(tempHuInfo);

        return outCards;
    }
    //先打后抓 听的提示
    public static boolean isTingOfOutFrist(PlayerST playerST, String[] rules) {
        boolean ting=false;
        //判断是否是夹胡  和规则
        boolean isJia = false;//不夹不胡
        Integer jia2 = null;//三七夹
        //获得rules
        List<Integer> intRules = getRules(rules);
        if (!CollectionUtils.isEmpty(intRules)) {
            if (intRules.contains(RuleType.JIA.getType()))
                isJia = true;
            if (intRules.contains(RuleType.JIA2.getType()))
                jia2 = RuleType.JIA2.getType();
        }

        List<Integer> shouPai=playerST.getCardsByType(CardType.SHOUPAI.getType());
        if (shouPai.size()<4)
            return false;

        //保存临时的牌
        Map<Integer, HuInfo> tempHuInfo = Maps.newHashMap();
        List<Integer> temp1=Lists.newArrayList(shouPai);
        List<Integer> winCards = getWinCards(temp1, playerST,TipType.CANTING.getType(),null,null);

        if (isJia) {  //有没有不胡不夹的规则
            if (winCards != null && winCards.size() == 1) { //赢一张牌
                if (RoomRuleUtils.isJia(temp1, winCards.get(0), jia2)) {
                    HuInfo huInfo = null;
                    Integer card=0;
                    huInfo = new HuInfo(winCards, true);
                    tempHuInfo.put(card, huInfo);
                    ting=true;
                }
            }
        } else {
            if (!CollectionUtils.isEmpty(winCards)) {
                Integer card=0;
                HuInfo huInfo = null;
                int winCard = winCards.get(0);
                if (winCards.size()==1&&RoomRuleUtils.isJia(temp1, winCard, jia2)) {
                    huInfo = new HuInfo(winCards, true);
                } else {
                    huInfo = new HuInfo(winCards, false);
                }
                tempHuInfo.put(card, huInfo);
                ting=true;
            }
        }
        if (!CollectionUtils.isEmpty(tempHuInfo))
            playerST.setTempHuInfo(tempHuInfo);
        return ting;
    }

    /**
     * 判断是否碰听
     *
     * @param players
     * @param cardNum
     * @param prePosition
     * @return
     */
    public static Map<Integer, List<List<Card>>> isPengTing(LinkedList<PlayerST> players, int cardNum, int prePosition, String[] rules) {
        Map<Integer, List<List<Card>>> map = new HashMap<Integer, List<List<Card>>>();
        for (int i = 0; i < players.size(); i++) {
            //需要判定的玩家
            PlayerST player = players.get(i);
            //该玩家手牌
            List<Integer> shouPai = player.getCardsByType(CardType.SHOUPAI.getType());
            if (player.getPosition() == prePosition)//出牌的那个人  跳过
                continue;
            //如果已经听了  跳过
            if (player.getStatus() == PlayerType.TING.getType()) {
                continue;
            }
            if (!(shouPai.size() > 4)) {//手牌为四张的时候不能吃或者碰
                continue;
            }
            List<Integer> temp = new ArrayList<Integer>(shouPai);//临时的牌
            boolean isPeng = isPeng(shouPai, cardNum);

            List<List<Integer>> list = new ArrayList<List<Integer>>();//碰牌的list   由于吃牌是个list 所以写一样了
            if (isPeng) {
                List<Integer> pengList = Lists.newArrayList(cardNum, cardNum);
                list.add(pengList);//碰牌的list
                List<List<Card>> result = chiOrPengTing(list, temp, player, TipType.CANPENGTING.getType(), rules,cardNum);
                if (result != null && result.size() > 0) {
                    map.put(player.getPosition(), result);
                }
            }
        }
        return map;
    }

    public static List<Integer> getRules(String[] rules) {
        List<Integer> intRules = Lists.newArrayList();
        if (rules == null)
            return null;
        for (int i = 0; i < rules.length; i++) {
            int rule = Integer.parseInt(rules[i]);
            intRules.add(rule);
        }
        return intRules;
    }

    /**
     * 手牌将白板34转为财神,财神转为0
     * @param cards 手牌
     * @param god 财神
     * @return
     */
    public static List<Integer> conversionBlank(List<Integer> cards, int god){
        List<Integer> conversion=new ArrayList();
        for(int card:cards){
            if(card==god)
                card=0;
            if(card==34)
                card=god;
            conversion.add(card);
        }
        return conversion;
    }

    public static void main(String[] args) {
        String[] rules = new String[]{"1", "2", "3"};
        System.out.print(getRules(rules));


    }


}
