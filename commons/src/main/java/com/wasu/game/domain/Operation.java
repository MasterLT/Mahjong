package com.wasu.game.domain;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/4/13.
 */
public class Operation implements Serializable {
    private static final long serialVersionUID = 10613252L;

    private int type;
    private int position;
    private List<Card> cards;

    public Operation() {
    }

    public Operation(int type, int position) {
        this.type=type;
        this.position=position;
        this.cards= Lists.newArrayList();
    }

    public Operation(int type, int position, int cardType, List<Integer> cards) {
        this.type=type;
        this.position=position;
        this.cards=Lists.newArrayList();
        Card card=new Card(cardType,cards,cards.size());
        this.cards.add(card);
    }

    public Operation(int type, int position, int cardType, Integer oneCard) {
        this.type=type;
        this.position=position;
        this.cards=Lists.newArrayList();
        Card card=new Card(cardType,Lists.newArrayList(oneCard),1);
        this.cards.add(card);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
