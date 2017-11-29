package com.wasu.game.domain.response;

import com.wasu.game.domain.entity.Room;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/12/31.
 */
public class RoomResponse implements Serializable {
    private static final long serialVersionUID = 1714666080613254L;

    private long id;
    private int totalCount;
    private int roomNum;
    private long adminId;
    private int openCount;
    private int round;
    private int state;
    private List<UserResponse> player;
    private int banker;
    private int playerNum;
    private String[] rule;
    private int[] isDissolution;
    private int applyDissolution;
    private int currentRount;
    private int currentCount;
    private Long win;
    private long bigWiner;
    private long gunner;
    private Integer bao;
    private Integer huCard;
    private Long dissolutionId;


    public RoomResponse() {
    }

    public RoomResponse(Room room, List<UserResponse> player) {
        this.id = room.getId();
        this.totalCount = room.getTotalCount();
        this.adminId = room.getAdminId();
        this.openCount = room.getOpenCount();
        this.state = room.getState();
        this.roomNum = room.getRoomNum();
        this.round = room.getRound();
        this.banker = room.getBanker();
        this.player = player;
        this.currentRount = room.getCurrentRound();
        this.currentCount = room.getOpenCount();
        this.playerNum = room.getPlayerNum();
        if ("".equals(room.getRule()))
            rule = null;
        else
            this.rule = room.getRule().split(",");
    }

    public int[] getIsDissolution() {
        return isDissolution;
    }

    public void setIsDissolution(int[] isDissolution) {
        this.isDissolution = isDissolution;
    }

    public long getId() {
        return id;
    }

    public int getRoomNum() {
        return roomNum;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public void setRoomNum(int roomNum) {
        this.roomNum = roomNum;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public long getAdminId() {
        return adminId;
    }

    public void setAdminId(long userId) {
        this.adminId = userId;
    }

    public int getOpenCount() {
        return openCount;
    }

    public void setOpenCount(int openCount) {
        this.openCount = openCount;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public List<UserResponse> getPlayer() {
        return player;
    }

    public void setPlayer(List<UserResponse> player) {
        this.player = player;
    }

    public int getBanker() {
        return banker;
    }

    public void setBanker(int banker) {
        this.banker = banker;
    }

    public int getPlayerNum() {
        return playerNum;
    }

    public void setPlayerNum(int playerNum) {
        this.playerNum = playerNum;
    }

    public String[] getRule() {
        return rule;
    }

    public int getApplyDissolution() {
        return applyDissolution;
    }

    public void setApplyDissolution(int applyDissolution) {
        this.applyDissolution = applyDissolution;
    }

    public int getCurrentRount() {
        return currentRount;
    }

    public void setCurrentRount(int currentRount) {
        this.currentRount = currentRount;
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
    }

    public void setRule(String[] rule) {
        this.rule = rule;
    }

    public Long getWin() {
        return win;
    }

    public void setWin(Long win) {
        this.win = win;
    }

    public long getBigWiner() {
        return bigWiner;
    }

    public void setBigWiner(long bigWiner) {
        this.bigWiner = bigWiner;
    }

    public long getGunner() {
        return gunner;
    }

    public void setGunner(long gunner) {
        this.gunner = gunner;
    }

    public Integer getBao() {
        return bao;
    }

    public void setBao(Integer bao) {
        this.bao = bao;
    }

    public Integer getHuCard() {
        return huCard;
    }

    public void setHuCard(Integer huCard) {
        this.huCard = huCard;
    }

    public Long getDissolutionId() {
        return dissolutionId;
    }

    public void setDissolutionId(Long dissolutionId) {
        this.dissolutionId = dissolutionId;
    }

    @Override
    public String toString() {
        return "win=" + win + player;
    }
}
