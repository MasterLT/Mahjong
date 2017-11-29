package com.wasu.game.module.player.untils;

import com.google.common.collect.Lists;
import com.wasu.game.domain.*;
import com.wasu.game.enums.CardType;
import com.wasu.game.enums.PlayerType;
import com.wasu.game.enums.TypePriority;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/28.
 */

@Component
public class CreateTipUtils {
   //提示碰的生成
    public Tip createTip(PlayerST playerST, Map<Integer,Integer> map, List<List<Integer>> cardsList,int position,int type) {
       Tip tip=new Tip();
       tip.setPosition(playerST.getPosition());
       tip.setUserid(playerST.getPlayerId());
        tip.setType(type);
       tip.setPriority(getPriority(type,playerST.getPosition(),position));
       if(map!=null&&map.size()>0){
           List<TipInfo> tipInfos=new ArrayList<TipInfo>();
           for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
               TipInfo tipInfo=new TipInfo();
               List<Card> cards=new ArrayList<Card>();
               for (int i=0;i<cardsList.size();i++){
                   Card card=new Card();
                   card=getCard(cardsList.get(i),type,true);
                   cards.add(card);
                   tipInfo.setCards(cards);
               }
               tipInfos.add(tipInfo);
           }
           tip.setTipInfos(tipInfos);
       }
       return tip;
   }
    public Tip createTip(PlayerST playerST, Map<Integer, Map<Integer, List<Integer>>> map, int position, int type) {
        Tip tip=new Tip();
        tip.setPosition(playerST.getPosition());
        tip.setUserid(playerST.getPlayerId());
        tip.setPriority(getPriority(type,playerST.getPosition(),position));
        tip.setType(type);
        if (!CollectionUtils.isEmpty(map)){
            List<TipInfo> tipInfos=new ArrayList<TipInfo>();
            for (Map.Entry<Integer, Map<Integer, List<Integer>>> entry : map.entrySet()) {
                TipInfo tipInfo=new TipInfo();
                List<Card> cards=null;
                cards=getCards(entry.getValue(),true);
                tipInfo.setPosition(entry.getKey());
                tipInfo.setCards(cards);
                tipInfos.add(tipInfo);
            }
            tip.setTipInfos(tipInfos);
        }
        return tip;
    }
    /**
     * 碰听、吃听
     * @param roomInfo
     * @param map
     * @param type
     * @return
     */
    public List<Tip> createTip(RoomInf roomInfo, Map<Integer, List<List<Card>>> map, int type) {
        int prePosition=roomInfo.returnBeforePositon();
        List<Tip> tips=new ArrayList<Tip>();
        if (!CollectionUtils.isEmpty(map)){
            for (Map.Entry<Integer, List<List<Card>>> entry:map.entrySet()){
                Tip tip=new Tip();
                int position=entry.getKey();
                Long  userId=roomInfo.getPlayerByPosition(position).getPlayerId();
                tip.setPosition(position);
                tip.setUserid(userId);
                tip.setType(type);
                tip.setPriority(getPriority(type,position,prePosition));

                List<TipInfo> tipInfos=new ArrayList<TipInfo>();
                TipInfo tipInfo=new TipInfo();

                tipInfo.setPosition(position);
                tipInfo.setUserid(userId);
                tipInfo.setCardsList(entry.getValue());
                tipInfos.add(tipInfo);
                tip.setTipInfos(tipInfos);
                tips.add(tip);
            }
        }
        return tips;

    }
    public List<Tip> createTips(RoomInf roomInf,PlayerST playerST,int type) {
        List<Tip> tips=new ArrayList<Tip>();
        Tip[] tips1=new Tip[roomInf.getPlayers().size()];
        for (int i=0;i<roomInf.getPlayers().size();i++){
            tips1[roomInf.getPlayers().get(i).getPosition()]=getTingTip(roomInf.getPlayers().get(i),playerST,type);
        }
        tips=Arrays.asList(tips1);
     return  tips;
    }
    /**
     *
     * @param playerST  被通知的玩家
     * @param relevantPlayer   //和通知有关的玩家
     * @param type
     * @return
     */
    private Tip getTingTip(PlayerST playerST, PlayerST relevantPlayer,int type) {
        Tip tip=new Tip();
        List<TipInfo> tipInfos=new ArrayList<TipInfo>();


        tip.setPosition(playerST.getPosition());
        tip.setType(type);
        tip.setUserid(playerST.getPlayerId());

        TipInfo tipInfo=new TipInfo();
        tipInfo.setPosition(relevantPlayer.getPosition());
        tipInfo.setUserid(relevantPlayer.getPlayerId());

        tipInfos.add(tipInfo);
        tip.setTipInfos(tipInfos);
        return tip;
    }
    /**
     * 生成牌组合
     * @param typeMap
     * @param isVisible
     * @return
     */
    private List<Card> getCards(Map<Integer, List<Integer>> typeMap, boolean isVisible) {
        List<Card> cards=new ArrayList<Card>();
        if (!CollectionUtils.isEmpty(typeMap)) {
            for (Map.Entry<Integer, List<Integer>> entry : typeMap.entrySet()) {
                Card card = null;
                card = getCard(entry.getValue(), entry.getKey(), isVisible);
                cards.add(card);
            }
        }
       return cards;
    }
    /**
     *
     * @param cards
     * @param type
     * @param isVisible
     * @return
     */
    private Card getCard(List<Integer> cards, int  type,boolean isVisible ) {
        Card card=new Card();
        if (isVisible){
            card.setType(type);
            card.setCards(cards);
            card.setCardNum(cards.size());
        }else {
            card.setType(type);
            card.setCardNum(cards.size());
        }
        return card;
    }
    /**
     * 获得提示级别
     * @param type
     * @param position
     * @param operationPrestion
     * @return
     */
    private int getPriority(int type, int position, int operationPrestion) {
        return getPositionPriority(position,operationPrestion)+TypePriority.getTypePriotityByType(type).getPriority();
    }
    /**
     * 位置权限  position 被提示的玩家位置  operationPrestion
     * @param position
     * @param operationPrestion//出牌玩家
     * @return
     */
    private int getPositionPriority(int position, int operationPrestion) {

        return position-operationPrestion>0?position-operationPrestion:position-operationPrestion+4;
    }
    /**
     * 结束时 返回的tips
     * @param roomInf
     * @return
     */
    public  List<Tip> creatTips(RoomInf roomInf) {
        List<Tip> tips=new ArrayList<Tip>();
        Tip[] tips1=new Tip[roomInf.getPlayers().size()];
        for (int i=0;i<roomInf.getPlayers().size();i++){
            tips1[roomInf.getPlayers().get(i).getPosition()]=getOverTip(roomInf,roomInf.getPlayers().get(i));
        }
        tips= Arrays.asList(tips1);
        return tips;
    }
    private Tip getOverTip(RoomInf roomInf,PlayerST player) {
        Tip tip=new Tip();
        List<TipInfo> tipInfos=new ArrayList<TipInfo>();
        tip.setUserid(player.getPlayerId());
        tip.setPosition(player.getPosition());
        for (int i=0;i<roomInf.getPlayers().size();i++){
            TipInfo tipInfo=new TipInfo();
            tipInfo.setPosition(roomInf.getPlayers().get(i).getPosition());
            tipInfo.setUserid(roomInf.getPlayers().get(i).getPlayerId());
            List<Card> cards=roomInf.getPlayers().get(i).getCards();
            Card delCard=null;
            for(Card c:cards){
                if (c.getCards().size()==0)
                    delCard=c;
            }
            if (delCard!=null)
                cards.remove(delCard);
            tipInfo.setCards(getCards(cards));
            tipInfos.add(tipInfo);
        }
        tip.setTipInfos(tipInfos);
        return tip;
    }
    private List<Card> getCards(List<Card> cards) {
        List<Card> result=new ArrayList<Card>();
        if (cards!=null&&cards.size()>0){
            for (int i=0;i<cards.size();i++){
                if (isUseful(cards.get(i).getType())){
                    result.add(cards.get(i));
                }
            }
        }
        return result;
    }

    private boolean isUseful(int type) {
        return type== CardType.SHOUPAI.getType()||type==CardType.PENGPAI.getType()||type==CardType.ANGANG.getType()||type==CardType.MINGGANG.getType()||type==CardType.CHIPAI.getType();
    }

    /**
     *   构造的返回值
     * @param playerST 当前玩家
     * @param map  key-牌的类型  value- 该类型牌的数组
     * @param type 此操作的类型
     * @return
     */
    public Tip getTingTip(PlayerST playerST, Map<Integer, List<Integer>> map, int type) {
        Tip tip=new Tip();
        if (map!=null&&map.size()>0){
            int position=playerST.getPosition();
            tip.setPosition(position);
            tip.setType(type);
            tip.setPriority(getPriority(type,position,position));
            tip.setUserid(playerST.getPlayerId());

            List<TipInfo> tipInfos= Lists.newArrayList();
            TipInfo tipInfo=new TipInfo();
            tipInfo.setPosition(position);
            tipInfo.setUserid(playerST.getPlayerId());
            tipInfo.setCards(getCards(map,true));
            tipInfos.add(tipInfo);
            tip.setTipInfos(tipInfos);

        }
        return tip;
    }

    /**
     * 构造tipInfo
     * @param position
     * @param userId
     * @param value
     * @return
     */
    private TipInfo getTipInfo(int position, Long userId, List<List<Card>> value) {
        return null;
    }


    //吃听的返回
    public List<Tip> getChiOrPengTingResult(RoomInf roomInf,List<TipInfo> tipInfos) {
        Tip[] tiplist=new Tip[roomInf.getPlayers().size()];
        for (int i=0;i<roomInf.getPlayers().size();i++){
            PlayerST playerST=roomInf.getPlayers().get(i);
            Tip tip=new Tip(playerST,tipInfos);
            tiplist[playerST.getPosition()]=tip;
        }
        List<Tip> tips =Lists.newArrayList(tiplist);

        return tips;
    }


    public Tip createMingGangTip2(PlayerST currentPlayer, List<Integer> cardL, int type) {
        Tip tip=null;
        List<Integer> cardSS=Lists.newArrayList();
       if (!CollectionUtils.isEmpty(cardL)){
           for (Integer card:cardL){
               cardSS.add(card);
           }
       }
        Card card1=new Card(type,cardSS,cardSS.size());
        List<Card> cards=Lists.newArrayList(card1);

        TipInfo tipInfo=new TipInfo(currentPlayer, PlayerType.BASE.getType(),cards);
        List<TipInfo> tipInfos=Lists.newArrayList(tipInfo);
        tip=new Tip(currentPlayer,tipInfos,getPriority(type,currentPlayer.getPosition(),currentPlayer.getPosition()),type);

        return tip;

    }

    public static List<Tip> createMingGangResult2(RoomInf roomInf, Integer cardNum, int type) {
        PlayerST currentPlayer=roomInf.getPlayerByPosition(roomInf.getCurrent());
        Tip[] tips=new Tip[roomInf.getPlayers().size()];
        List<Integer> cardList=Lists.newArrayList(cardNum);

        for (PlayerST player:roomInf.getPlayers()){
            Card card=new Card(type,cardList,cardList.size());
            TipInfo tipInfo=new TipInfo(currentPlayer,PlayerType.BASE.getType(),Lists.<Card>newArrayList(card));
            List<TipInfo> tipInfos=Lists.newArrayList(tipInfo);
            Tip tip=new Tip(player,tipInfos);
            tips[player.getPosition()]=tip;
        }
        return Lists.newArrayList(tips);
    }
}
