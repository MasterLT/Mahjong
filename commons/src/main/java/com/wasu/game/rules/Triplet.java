package com.wasu.game.rules;

import com.wasu.game.domain.PlayerST;
import com.wasu.game.enums.CardType;

import java.util.List;

/**
 * Created by Administrator on 2017/1/5.
 */

public class Triplet extends BaseRule{

    //是否含有刻牌
    public boolean containTriplet(List<Integer> shouPai){
        int[] cards=getCards(shouPai);
        for (int i=0;i<cards.length;i++){
            if (cards[i]==4){
                return true;
            }
        }
        return false;
    }


    @Override
   public boolean handle(PlayerST playerST,Integer cardNum) {
        List<Integer> shouPai=playerST.getCardsByType(CardType.SHOUPAI.getType());
        if (containTriplet(shouPai)){
            if (null!=nextRule)
            return nextRule.handle(playerST,cardNum);
        }
        return false;
    }
}
