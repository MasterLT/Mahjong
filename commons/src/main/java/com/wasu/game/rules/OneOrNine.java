package com.wasu.game.rules;

import com.wasu.game.domain.PlayerST;
import com.wasu.game.enums.CardType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/9.
 */
public class OneOrNine extends BaseRule {

    @Override
   public boolean handle(PlayerST playerST, Integer cardNum) {

        if (!isFlush(playerST,cardNum)){
            System.out.println("没有1和9");
            return false;
        }else {
            if (null!=nextRule){
                return  nextRule.handle(playerST,cardNum);
            }
           return true;
        }
    }

    boolean isFlush(PlayerST player, Integer cardNum){
        List<Integer> cards=getAllCards(player,cardNum);
        for(Integer c:cards){
            if(containOneOrNine(c))
                return true;
        }
        return false;
    }

    List<Integer> getAllCards(PlayerST player, Integer cardNum){
        List<Integer> cards=new ArrayList<Integer>();
        //手牌
        List<List<Integer>> hand=player.getCardsListByType(CardType.SHOUPAI.getType());
        //吃
        List<List<Integer>> chi=player.getCardsListByType(CardType.CHIPAI.getType());
        //碰
        List<List<Integer>> peng=player.getCardsListByType(CardType.PENGPAI.getType());
        //杠
        List<List<Integer>> gang=player.getCardsListByType(CardType.ANGANG.getType());
        List<List<Integer>> mgang=player.getCardsListByType(CardType.MINGGANG.getType());
        addAllCard(cards,hand);
        addAllCard(cards,chi);
        addAllCard(cards,peng);
        addAllCard(cards,gang);
        addAllCard(cards,mgang);
        if(cardNum!=null)
            cards.add(cardNum);
        return cards;
    }


    List<Integer> addAllCard(List<Integer> all,List<List<Integer>> cards){
        for(List<Integer> c:cards){
            if(cards.size()>0){
                all.addAll(c);
            }
        }
        return all;
    }

    boolean containOneOrNine(int card){
        if(card==33||card%9==1||card%9==0)
            return true;
        return false;
    }

}
