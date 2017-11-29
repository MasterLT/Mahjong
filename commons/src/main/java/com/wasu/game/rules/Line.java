package com.wasu.game.rules;

import com.wasu.game.domain.PlayerST;
import com.wasu.game.enums.CardType;
import com.wasu.game.utilities.RoomRuleUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Administrator on 2017/1/9.
 */
public class Line extends BaseRule {

    @Override
   public boolean handle(PlayerST playerST, Integer cardNum) {

        if (!isFlush(playerST,cardNum)){
            System.out.println("没有顺子");
            return false;
        }else {
            if (null!=nextRule){
                return  nextRule.handle(playerST,cardNum);
            }
            return true;
        }
    }

    boolean isFlush(PlayerST player, Integer cardNum){
        if(player.getCardsByType(CardType.CHIPAI.getType()).size()>0)
            return true;
        List<Integer> org=player.getCardsByType(CardType.SHOUPAI.getType());
        List<Integer> hand=new ArrayList<Integer>(org);
        if(cardNum!=null)
            hand.add(cardNum);
        HashSet<List<Integer>> line= RoomRuleUtils.getLine(hand,Integer.MAX_VALUE);
        if(line.size()>0)
            return true;
        return false;
    }

}
