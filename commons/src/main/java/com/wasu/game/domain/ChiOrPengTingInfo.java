package com.wasu.game.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2017/1/12.
 */
public class ChiOrPengTingInfo implements Serializable {
    private static final long serialVersionUID = 111257L;
    private List<Integer> cards;
    private int beChi;
    private List<OutAndWin> outAndWins;

    public ChiOrPengTingInfo(List<Integer> cards,List<OutAndWin> outAndWins) {
        this.cards = cards;
        this.outAndWins=outAndWins;
    }

    public ChiOrPengTingInfo() {
    }

    public List<Integer> getCards() {
        return cards;
    }

    public void setCards(List<Integer> cards) {
        this.cards = cards;
    }

    public List<OutAndWin> getOutAndWins() {
        return outAndWins;
    }

    public void setOutAndWins(List<OutAndWin> outAndWins) {
        this.outAndWins = outAndWins;
    }

    public int getBeChi() {
        return beChi;
    }

    public void setBeChi(int beChi) {
        this.beChi = beChi;
    }


    public boolean isEqual(List<Integer> cards){
        Collections.sort(cards);
        Collections.sort(this.cards);
        return this.cards.equals(cards);
    }

    @Override
    public String toString() {
        return "ChiOrPengTingInfo{" +
                "cards=" + cards +
                ", beChi=" + beChi +
                ", outAndWins=" + outAndWins +
                '}';
    }
}
