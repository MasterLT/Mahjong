package com.wasu.game.module.player.untils;

import com.google.common.collect.Lists;
import com.wasu.game.domain.Card;

import java.util.List;

/**
 * Created by Administrator on 2017/1/10.
 */
public class CardUntils {

    public static List<Card> creatCardList(List<Integer> cards,int type){
        List<Card> cardList= Lists.newArrayList(creatCard(cards,type));
        return cardList;
    }

    public static Card creatCard(List<Integer> cards,int type){
        return new Card(type,cards,cards.size());
    }
}
