package com.wasu.game.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/1/12.
 */
public class OutAndWin implements Serializable {
    private static final long serialVersionUID = 111258L;

    private int outCard;
    private List<Integer> winCards;
    private HuInfo huInfo;

    public OutAndWin(int outCard, List<Integer> winCards) {
        this.outCard = outCard;
        this.winCards = winCards;

    }

    public OutAndWin(int outCard,HuInfo huInfo) {
        this.huInfo = huInfo;
        this.outCard = outCard;
    }

    public OutAndWin() {
    }

    @Override
    public String toString() {
        return "OutAndWin{" +
                "outCard=" + outCard +
                ", winCards=" + winCards +
                '}';
    }

    public int getOutCard() {
        return outCard;
    }

    public void setOutCard(int outCard) {
        this.outCard = outCard;
    }

    public List<Integer> getWinCards() {
        return winCards;
    }

    public void setWinCards(List<Integer> winCards) {
        this.winCards = winCards;
    }

    public HuInfo getHuInfo() {
        return huInfo;
    }

    public void setHuInfo(HuInfo huInfo) {
        this.huInfo = huInfo;
    }
}
