package com.wasu.game.domain.response;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/2/6.
 */
public class CardResponse implements Serializable {
    private static final long serialVersionUID = 8247814666081111333L;
    private Integer type;
    private int cardNum;
    private List<Integer> cards;

    public CardResponse(Integer type, List<Integer> cards) {
        this.type = type;
        this.cards = cards;
        this.cardNum = cards.size();
    }

    public CardResponse(Integer type, int cardNum) {
        this.type = type;
        this.cardNum = cardNum;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<Integer> getCards() {
        return cards;
    }

    public void setCards(List<Integer> cards) {
        this.cards = cards;
    }

    public int getCardNum() {
        return cardNum;
    }

    public void setCardNum(int cardNum) {
        this.cardNum = cardNum;
    }
}
