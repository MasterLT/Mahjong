package com.wasu.game.domain.response;

import com.wasu.game.domain.entity.User;

import java.io.Serializable;
import java.util.Date;

public class UserResponse implements Serializable {
    private static final long serialVersionUID = 87714666080613254L;
    /**
     * 玩家id
     */
    private long id;

    private String name;

    private int gender;

    private int gold;

    private int point;

    private String face;

    private String registerIp;

    private String loginIp;

    private Date registerDate;

    private int winCount;

    private int lostCount;

    private int fleeCount;

    private int drawCount;

    private String wId;

    private boolean state;

    private String token;

    private long roomId;

    private int position;

    private int roomCard;

    private String realName;

    private String idNum;

    private RecordResponse record;

    private boolean[] isJoin;

    public UserResponse() {
    }

    public UserResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.gold = user.getGold();
        this.point = user.getPoint();
        this.face = user.getFace();
        this.registerIp = user.getRegisterIp();
        this.loginIp = user.getLoginIp();
        this.registerDate = user.getRegisterDate();
        this.winCount = user.getWinCount();
        this.drawCount = user.getDrawCount();
        this.fleeCount = user.getFleeCount();
        this.lostCount = user.getLostCount();
        this.wId = user.getwId();
        this.token = user.getToken();
        this.roomId = user.getRoomId();
        this.position = user.getPosition();
        this.gender = user.getGender();
        this.realName = user.getRealName();
        this.idNum = user.getIdNum();
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdNum() {
        return idNum;
    }

    public void setIdNum(String idNum) {
        this.idNum = idNum;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getRegisterIp() {
        return registerIp;
    }

    public void setRegisterIp(String registerIp) {
        this.registerIp = registerIp;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public int getWinCount() {
        return winCount;
    }

    public void setWinCount(int winCount) {
        this.winCount = winCount;
    }

    public int getLostCount() {
        return lostCount;
    }

    public void setLostCount(int lostCount) {
        this.lostCount = lostCount;
    }

    public int getFleeCount() {
        return fleeCount;
    }

    public void setFleeCount(int fleeCount) {
        this.fleeCount = fleeCount;
    }

    public int getDrawCount() {
        return drawCount;
    }

    public void setDrawCount(int drawCount) {
        this.drawCount = drawCount;
    }

    public String getwId() {
        return wId;
    }

    public void setwId(String wId) {
        this.wId = wId;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getRoomCard() {
        return roomCard;
    }

    public void setRoomCard(int roomCard) {
        this.roomCard = roomCard;
    }

    public RecordResponse getRecord() {
        return record;
    }

    public void setRecord(RecordResponse record) {
        this.record = record;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public boolean isState() {
        return state;
    }

    public boolean[] getIsJoin() {
        return isJoin;
    }

    public void setIsJoin(boolean[] isJoin) {
        this.isJoin = isJoin;
    }

    @Override
    public String toString() {
        return "****id=" + id + "," + record;
    }
}
