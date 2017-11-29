package com.wasu.game.domain.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "record")
public class Record {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name="room_id")
    private Long roomId;

    @Column(name="room_num")
    private Integer roomNum;

    @Column(name="start_time")
    private Date startTime;

    @Column(name="user_id")
    private Long userId;

    @Column(name="user_name")
    private String userName;

    @Column(name="win_time")
    private Integer winTime;

    @Column(name="banker_time")
    private Integer bankerTime;

    @Column(name="gun_time")
    private Integer gunTime;

    @Column(name="bao_time")
    private Integer baoTime;

    @Column(name="big_bao_time")
    private Integer bigBaoTime;

    @Column(name="zhan_time")
    private Integer zhanTime;

    private Integer innings;

    private long banker;

    private Integer point;

    private Long win;

    private Integer type;

    private String hutype;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Integer getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(Integer roomNum) {
        this.roomNum = roomNum;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getInnings() {
        return innings;
    }

    public void setInnings(Integer innings) {
        this.innings = innings;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public Long getWin() {
        return win;
    }

    public void setWin(Long win) {
        this.win = win;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public Integer getZhanTime() {
        return zhanTime;
    }

    public void setZhanTime(Integer zhanTime) {
        this.zhanTime = zhanTime;
    }

    public long getBanker() {
        return banker;
    }

    public void setBanker(long banker) {
        this.banker = banker;
    }

    public String getHutype() {
        return hutype;
    }

    public void setHutype(String hutype) {
        this.hutype = hutype;
    }
}
