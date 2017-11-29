package com.wasu.game.domain.response;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/2/6.
 */
public class PlayerInfoResponse implements Serializable {
    private static final long serialVersionUID = 8247814666081111222L;
    private Long userid;
    private int position;
    private List<CardResponse> cards;

    public PlayerInfoResponse(Long userid, int position, List<CardResponse> cards) {
        this.userid = userid;
        this.position = position;
        this.cards = cards;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<CardResponse> getCards() {
        return cards;
    }

    public void setCards(List<CardResponse> cards) {
        this.cards = cards;
    }
}
