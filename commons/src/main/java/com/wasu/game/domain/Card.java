package com.wasu.game.domain;

import com.google.common.collect.Lists;
import com.wasu.game.exception.ErrorCodeException;

import java.io.Serializable;
import java.util.List;

public class Card implements Serializable {
    private static final long serialVersionUID = 10613251L;
    private int type;
    private List<Integer> cards;
    private int cardNum;


    public Card() {
        super();
    }

    public Card(int type, List<Integer> cards, int cardNum) {
        this.type = type;
        this.cards = cards;
        this.cardNum = cardNum;
    }

    public Card(int type, List<Integer> cards) {
        this.type = type;
        this.cards = cards;
        this.cardNum = cards.size();
    }

    public Card(int type, Integer card) {
        this.type = type;
        this.cards = Lists.newArrayList(card);
        this.cardNum = cards.size();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
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

    public void addCard(int cardNum) {
        cards.add(cardNum);
    }

    public void addCard(List<Integer> cardlist) {
        cards.addAll(cardlist);
    }

    public void remove(int cardNum) {
        if (cards.contains(cardNum)) {
            for (int i = 0; i < cards.size(); i++) {
                if (cardNum == cards.get(i)) {
                    cards.remove(i);
                    break;
                }
            }
        }else
            throw new ErrorCodeException(ResultCode.UNUSEABLECARD);

    }

    public void removeLast() {
        cards.remove(cards.size() - 1);
    }

    @Override
    public String toString() {
        return "Card{" +
                "type=" + type +
                ", cards=" + cards +
                ", cardNum=" + cardNum +
                '}';
    }
}
