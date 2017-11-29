package com.wasu.game.module.player.untils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wasu.game.domain.PlayerST;
import com.wasu.game.domain.RoomInf;
import com.wasu.game.domain.response.CardResponse;
import com.wasu.game.domain.response.PlayerInfoResponse;
import com.wasu.game.domain.response.RoomOperationInfoResponse;
import com.wasu.game.enums.CardType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by Administrator on 2017/2/8.
 */
public class RoomOperationResponseUntils {
    //发牌时 返回
    public static List<RoomOperationInfoResponse> returnShuffe(Queue<Integer> allQueue,Map<Integer, List<Integer>> positionAndCards,List<PlayerST> playerSTs){
        RoomOperationInfoResponse[] items=new RoomOperationInfoResponse[playerSTs.size()];
        List<RoomOperationInfoResponse> roomOperationInfoResponses=null;
        //发给每个玩家的信息 按位置position
        for (PlayerST playerST:playerSTs){
            items[playerST.getPosition()]=getShuffe(playerST,allQueue,positionAndCards,playerSTs);
        }
        roomOperationInfoResponses= Arrays.asList(items);
        return roomOperationInfoResponses;
    }

    private static RoomOperationInfoResponse getShuffe(PlayerST playerST, Queue<Integer> allQueue, Map<Integer, List<Integer>> positionAndCards,List<PlayerST> playerSTs) {
       //每个人的情况
        List<PlayerInfoResponse> playerInfoResponses= newArrayList();
        for (int i=0;i<playerSTs.size();i++){
            //p玩家的情况
            PlayerST p=playerSTs.get(i);
            //牌的类型和值
            Map<Integer,List<Integer>> cardsAndType= Maps.newHashMap();
            cardsAndType.put(CardType.SHOUPAI.getType(),positionAndCards.get(p.getPosition()));
            PlayerInfoResponse playerInfoResponse=getPlayerInfoResponse(playerST,p,cardsAndType);
            playerInfoResponses.add(playerInfoResponse);
        }
        RoomOperationInfoResponse roomOperationInfoResponse=new RoomOperationInfoResponse(allQueue.size(),playerInfoResponses);
        return roomOperationInfoResponse;
    }

    /**
     * 抓牌时返回
     * @param allQueue
     * @param cards 抓的那张牌
     * @param playerSTs
     * @return
     */
    public static List<RoomOperationInfoResponse> returnDeal(Queue<Integer> allQueue, List<Integer> cards, List<PlayerST> playerSTs,PlayerST currentPlayer) {
        RoomOperationInfoResponse[] items=new RoomOperationInfoResponse[playerSTs.size()];
        List<RoomOperationInfoResponse> roomOperationInfoResponses=null;
        //发给每个玩家的信息 按位置position
        for (PlayerST playerST:playerSTs){
            Map<Integer,List<Integer>> cardsAndType= Maps.newHashMap();
            cardsAndType.put(CardType.SHOUPAI.getType(),cards);
            PlayerInfoResponse playerInfoResponse=getPlayerInfoResponse(playerST,currentPlayer,cardsAndType);
            items[playerST.getPosition()]=new RoomOperationInfoResponse(allQueue.size(),Lists.<PlayerInfoResponse>newArrayList(playerInfoResponse));
        }
        roomOperationInfoResponses= Arrays.asList(items);
        return roomOperationInfoResponses;
    }

    /**
     *
     * @param cards 要出的牌
     * @param playerSTs
     * @param currentPlayer 出牌的人
     * @return
     */
    public static List<RoomOperationInfoResponse> returnOutHand(Queue<Integer> allQueue,List<Integer> cards, List<PlayerST> playerSTs, PlayerST currentPlayer) {
        RoomOperationInfoResponse[] items=new RoomOperationInfoResponse[playerSTs.size()];
        List<RoomOperationInfoResponse> roomOperationInfoResponses=null;

        //发给每个玩家的信息 按位置position
        for (PlayerST playerST:playerSTs){
            Map<Integer,List<Integer>> cardsAndType= Maps.newHashMap();
            cardsAndType.put(CardType.OUT.getType(),cards);
            PlayerInfoResponse playerInfoResponse=getPlayerInfoResponse1(playerST,currentPlayer,cardsAndType);
            items[playerST.getPosition()]=new RoomOperationInfoResponse(allQueue.size(),Lists.<PlayerInfoResponse>newArrayList(playerInfoResponse));
        }
        roomOperationInfoResponses= Arrays.asList(items);
        return roomOperationInfoResponses;
    }

    /**
     * 吃碰杠用到
     * @param roomInf
     * @param map key:position value:map<integer,list<integer>> key:type value 牌
     * @return
     */
    public static List<RoomOperationInfoResponse> returnRoomOperationInfoResponses(RoomInf roomInf, Map<Integer, Map<Integer, List<Integer>>> map) {
        List<PlayerST> playerSTs=roomInf.getPlayers();
        RoomOperationInfoResponse[] items=new RoomOperationInfoResponse[playerSTs.size()];
        List<RoomOperationInfoResponse> roomOperationInfoResponses=null;
        for (PlayerST playerST:playerSTs){
            items[playerST.getPosition()]=getRoomOperationInfoResponse(playerST,roomInf,map);
        }
        roomOperationInfoResponses=Arrays.asList(items);

        return roomOperationInfoResponses;
    }

    /**
     *
     * @param playerST 需要收到消息的玩家
     * @param roomInf
     * @param map key:position value:map<integer,list<integer>> key:type value 牌
     * @return
     */
    private static RoomOperationInfoResponse getRoomOperationInfoResponse(PlayerST playerST, RoomInf roomInf, Map<Integer, Map<Integer, List<Integer>>> map) {
        RoomOperationInfoResponse roomOperationInfoResponse=null;
        List<PlayerInfoResponse> playerInfoResponses=Lists.newArrayList();
        for(Map.Entry<Integer, Map<Integer, List<Integer>>> positionMap:map.entrySet()){
            PlayerInfoResponse playerInfoResponse=getPlayerInfoResponse1(playerST,roomInf.getPlayerByPosition(positionMap.getKey()),positionMap.getValue());
            playerInfoResponses.add(playerInfoResponse);
        }
        roomOperationInfoResponse=new RoomOperationInfoResponse(roomInf.getLeftDeskCards().size(),playerInfoResponses);
        return roomOperationInfoResponse;
    }


    /**
     * 牌是都可以见的
     * @param playerST
     * @param currentPlayer
     * @param cardsAndType
     * @return
     */
    private static PlayerInfoResponse getPlayerInfoResponse1(PlayerST playerST, PlayerST currentPlayer, Map<Integer, List<Integer>> cardsAndType) {
        PlayerInfoResponse playerInfoResponse=null;
        List<CardResponse> cardResponses=Lists.newArrayList();
        for (Map.Entry<Integer,List<Integer>> entry:cardsAndType.entrySet()){
            CardResponse cardResponse=getCardResponse(entry.getKey(),entry.getValue(),true);
            cardResponses.add(cardResponse);
        }
        playerInfoResponse=new PlayerInfoResponse(currentPlayer.getPlayerId(),currentPlayer.getPosition(),cardResponses);
        return playerInfoResponse;
    }

    /**
     *牌只有自己可见
     * @param playerST  被通知的玩家
     * @param p 玩家P的牌的情况
     * @param cardsAndType //p的牌和牌的类型
     * @return
     */
    private static PlayerInfoResponse getPlayerInfoResponse(PlayerST playerST,PlayerST p,Map<Integer,List<Integer>> cardsAndType) {
        PlayerInfoResponse playerInfoResponse=null;
        List<CardResponse> cardResponses=Lists.newArrayList();
        for (Map.Entry<Integer,List<Integer>> entry:cardsAndType.entrySet()){
            CardResponse cardResponse=getCardResponse(entry.getKey(),entry.getValue(),playerST.getPosition()==p.getPosition());
            cardResponses.add(cardResponse);
        }
        playerInfoResponse=new PlayerInfoResponse(p.getPlayerId(),p.getPosition(),cardResponses);
        return playerInfoResponse;
    }

    /**
     *
     * @param type 牌的类型
     * @param Cards 牌的数组的集合
     * @param b 牌是否可见
     * @return
     */
    private static CardResponse getCardResponse(int type, List<Integer> Cards, boolean b) {
        CardResponse cardResponse=null;
        if (b){
            cardResponse=new CardResponse(type,Cards);
        }else {
            cardResponse=new CardResponse(type,Cards.size());
        }
        return cardResponse;
    }


}
