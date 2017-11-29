package com.wasu.game.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/12/28.
 */
public class TipInfo implements Serializable {
    private static final long serialVersionUID = 14666080613251L;
    private long userid;
    private int position;
    private int status;//状态
    private List<Card> cards;

    private List<List<Card>> cardsList;

    public TipInfo(PlayerST playerST) {
        this.userid = playerST.getPlayerId();
        this.position=playerST.getPosition();
    }

    public TipInfo(long userid, int position, List<Card> cards, int status, List<List<Card>> cardsList) {
        this.userid = userid;
        this.position = position;
        this.cards = cards;
        this.status = status;
        this.cardsList = cardsList;
    }

    public TipInfo(PlayerST playerST,int status, List<Card> cards) {
        this.userid=playerST.getPlayerId();
        this.position=playerST.getPosition();
        this.status = status;
        this.cards = cards;
    }

    public TipInfo(List<Card> cards, int status, int position, long userid) {
        this.cards = cards;
        this.status = status;
        this.position = position;
        this.userid = userid;
    }

    public TipInfo() {
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
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

    public List<List<Card>> getCardsList() {
        return cardsList;
    }

    public void setCardsList(List<List<Card>> cardsList) {
        this.cardsList = cardsList;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TipInfo{" +
                "userid=" + userid +
                ", position=" + position +
                ", status=" + status +
                ", cards=" + cards +
                ", cardsList=" + cardsList +
                '}';
    }
}
