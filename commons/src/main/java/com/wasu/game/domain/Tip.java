package com.wasu.game.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/12/27.
 */
public class Tip implements Comparable<Tip>,Serializable {
    private static final long serialVersionUID = 7247714666080613254L;
    private int  priority;
    private long userid;
    private int position; // position-0
    private int type;//吃 100  碰  200
    private List<TipInfo> tipInfos;

    public Tip() {
    }

    public Tip(PlayerST playerST,List<TipInfo> tipInfos) {
        this.userid=playerST.getPlayerId();
        this.position=playerST.getPosition();
        this.tipInfos=tipInfos;
    }

    public Tip(PlayerST playerST,List<TipInfo> tipInfos,int priority, int type) {
        this.userid=playerST.getPlayerId();
        this.position=playerST.getPosition();
        this.priority = priority;
        this.tipInfos = tipInfos;
        this.type = type;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<TipInfo> getTipInfos() {
        return tipInfos;
    }

    public void setTipInfos(List<TipInfo> tipInfos) {
        this.tipInfos = tipInfos;
    }

    public int compareTo(Tip o) {
        if(this.priority>o.priority)
            return -1;
        else
            return 1;
    }

    @Override
    public String toString() {
        return "Tip{" +
                "priority=" + priority +
                ", userid=" + userid +
                ", position=" + position +
                ", type=" + type +
                ", tipInfos=" + tipInfos +
                '}';
    }
}
