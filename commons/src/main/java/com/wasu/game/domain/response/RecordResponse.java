package com.wasu.game.domain.response;

import com.google.common.collect.Lists;
import com.wasu.game.domain.entity.Record;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/12/29.
 */
public class RecordResponse implements Serializable {
    private static final long serialVersionUID = 55L;
    private int point;
    private Date startTime = new Date();
    private Integer winTime = 0;
    private Integer bankerTime = 0;
    private Integer gunTime = 0;
    private Integer baoTime = 0;
    private Integer bigBaoTime = 0;
    private boolean ting;
    private boolean zhan;
    //    private int huType;
    private List<Integer> huType;

    public RecordResponse() {
    }

    public RecordResponse(Record r) {
        this.point = r.getPoint();
        this.startTime = r.getStartTime();
        this.winTime = r.getWinTime();
        this.bankerTime = r.getBankerTime();
        this.gunTime = r.getGunTime();
        this.bigBaoTime = r.getBigBaoTime();
        if (r.getHutype() != null) {
            String[] huType = r.getHutype().split(",");
            Integer array[] = new Integer[huType.length];
            for (Integer i = 0; i < huType.length; i++)
                array[i] = Integer.parseInt(huType[i]);
            this.huType = Lists.newArrayList(array);
        }
        this.zhan = r.getZhanTime() > 0;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Integer getWinTime() {
        return winTime;
    }

    public void setWinTime(Integer winTime) {
        this.winTime = winTime;
    }

    public Integer getBankerTime() {
        return bankerTime;
    }

    public void setBankerTime(Integer bankerTime) {
        this.bankerTime = bankerTime;
    }

    public Integer getGunTime() {
        return gunTime;
    }

    public void setGunTime(Integer gunTime) {
        this.gunTime = gunTime;
    }

    public Integer getBaoTime() {
        return baoTime;
    }

    public void setBaoTime(Integer baoTime) {
        this.baoTime = baoTime;
    }

    public Integer getBigBaoTime() {
        return bigBaoTime;
    }

    public void setBigBaoTime(Integer bigBaoTime) {
        this.bigBaoTime = bigBaoTime;
    }

    public boolean isTing() {
        return ting;
    }

    public void setTing(boolean ting) {
        this.ting = ting;
    }

    public boolean isZhan() {
        return zhan;
    }

    public void setZhan(boolean zhan) {
        this.zhan = zhan;
    }

    public List<Integer> getHuType() {
        return huType;
    }

    public void setHuType(List<Integer> huType) {
        this.huType = huType;
    }

    @Override
    public String toString() {
        return "积分=" + point +
                ", 听=" + ting +
                ", 站=" + zhan;
    }
}
