package com.wasu.game.module.player.untils;

import com.google.common.collect.Lists;
import com.wasu.game.domain.Card;
import com.wasu.game.domain.PlayerST;
import com.wasu.game.domain.TipInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/9.
 */
public class TipInfoUtils {
    public static TipInfo createTipInfo(PlayerST playerST, Map<Integer,List<Integer>> typeAndCardsMap){
        TipInfo tipInfo=null;
        List<Card> cards= Lists.newArrayList();
        for (Map.Entry<Integer,List<Integer>> entry:typeAndCardsMap.entrySet()){
            Card card=new Card(entry.getKey(),entry.getValue(),entry.getValue().size());
            cards.add(card);
        }
        tipInfo=new TipInfo(playerST,0,cards);
        return tipInfo;
    }
}
