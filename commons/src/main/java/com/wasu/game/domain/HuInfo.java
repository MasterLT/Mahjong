package com.wasu.game.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/1/16.
 */
public class HuInfo implements Serializable{
    private static final long serialVersionUID = 111256L;

    private List<Integer> huCards;
    private int huType;
    private Long pao;

    public HuInfo() {
    }
    private boolean jia;

    public HuInfo(List<Integer> huCards, int huType) {
        this.huCards = huCards;
        this.huType = huType;
    }

    public HuInfo(List<Integer> huCards, int huType, long pao) {
        this.huCards = huCards;
        this.huType = huType;
        this.pao =pao;
    }

    public HuInfo(List<Integer> huCards, int huType, boolean isJia) {
        this.huCards = huCards;
        this.huType = huType;
        this.jia= isJia;
    }

    public HuInfo(List<Integer> huCards, boolean isJia) {
        this.huCards = huCards;
        this.jia = isJia;
    }

    public List<Integer> getHuCards() {
        return huCards;
    }

    public void setHuCards(List<Integer> huCards) {
        this.huCards = huCards;
    }

    public int getHuType() {
        return huType;
    }

    public void setHuType(int huType) {
        this.huType = huType;
    }

    public boolean isJia() {
        return jia;
    }

    public void setJia(boolean jia) {
        this.jia = jia;
    }

    public Long getPao() {
        return pao;
    }

    public void setPao(Long pao) {
        this.pao = pao;
    }

    @Override
    public String toString() {
        return "HuInfo{" +
                "huCards=" + huCards +
                ", huType=" + huType +
                ", jia=" + jia +
                '}';
    }
}
