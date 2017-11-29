package com.wasu.game.rules;

import com.google.common.collect.Lists;
import com.wasu.game.domain.Card;
import com.wasu.game.domain.PlayerST;
import com.wasu.game.enums.CardType;

import java.util.List;

/**
 * Created by Administrator on 2017/1/5.
 */
public abstract class BaseRule {

    protected BaseRule nextRule;

    public BaseRule setNextRule(BaseRule nextRule) {
        this.nextRule = nextRule;
        return nextRule;
    }
    public boolean handle(PlayerST playerST,Integer cardNum,Integer inCard,Integer outCard){
        PlayerST playerST1=new PlayerST();
        List<Integer> shouPai=Lists.newArrayList(playerST.getCardsByType(CardType.SHOUPAI.getType()));
        Card card=new Card(CardType.SHOUPAI.getType(),shouPai,shouPai.size());
        List<Card> cardList=Lists.newArrayList(card);
        playerST1.setCards(cardList);
        playerST1.addCard(Lists.newArrayList(playerST.getCardsByType(CardType.CHIPAI.getType())),CardType.CHIPAI.getType());
        playerST1.addCard(Lists.newArrayList(playerST.getCardsByType(CardType.PENGPAI.getType())),CardType.PENGPAI.getType());
        playerST1.addCard(Lists.newArrayList(playerST.getCardsByType(CardType.ANGANG.getType())),CardType.ANGANG.getType());
        playerST1.addCard(Lists.newArrayList(playerST.getCardsByType(CardType.MINGGANG.getType())),CardType.MINGGANG.getType());
        if (outCard!=null)
            playerST1.deleteCard(outCard,CardType.SHOUPAI.getType());
        if(inCard!=null)
            playerST1.addCard(inCard,CardType.SHOUPAI.getType());
        return handle(playerST1,cardNum);
    }
    public  abstract  boolean handle(PlayerST playerST,Integer cardNum);
    public int[] getCards(List<Integer> shouPai) {
        int[] cards=new int[34];
        if (shouPai!=null&&shouPai.size()>0){
            for (int i=0;i<shouPai.size();i++){
                cards[shouPai.get(i)-1] ++;
            }
        }
        return cards;
    }

}
