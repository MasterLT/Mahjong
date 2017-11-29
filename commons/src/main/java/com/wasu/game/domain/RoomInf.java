package com.wasu.game.domain;

import com.wasu.game.domain.entity.Room;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class RoomInf implements Serializable {
    private static final long serialVersionUID = 111254L;
    private Integer[] roll;//0吨数1财神位置
    private int godWealth;//财神
    private long orderId;
    private int leftDeskCardNum;//剩余的牌
    private int discard;//流局牌数
    private long roomId;
    private int roomNum;
    private int firstBanker;
    private int currentRount;
    private int round;
    private int totalCount;
    private int currentCount;
    private int banker;
    private int bankerScore;
    private int status;//状态0:准备，1：开始，2：结束，3：申请解散，4：解散
    private List<Tip> tips;//提示
    private List<Tip> tempTips;//当前发送的提示
    private int current;
    private short option;
    private Long win;
    private int playerNum;
    private String[] rule;
    private long adminId;
    private int[] isDissolution;
    private int applyDissolution;
    private Integer baoCard;
    private Long pao;
    private Long dissolutionId;
    private Integer huCard;
    private Operation history;
    private LinkedList<Integer> posistions = new LinkedList<Integer>();
    private LinkedList<PlayerST> players = new LinkedList<PlayerST>();
    private Queue<Integer> leftDeskCards;

    public void nextBanker(int banker) {
        this.banker = getNext(banker);
    }

    public int getNext(int value) {
        return (value - 1) < 0 ? this.getPlayerNum() - 1 : value - 1;
    }

    public void nextOne() {
        this.current = (current - 1) < 0 ? this.getPlayerNum() - 1 : current - 1;
    }

    public RoomInf() {
    }

    public RoomInf(Room r) {
        this.roomId = r.getId();
        for (int i = 0; i < r.getPlayerNum(); i++) {
            posistions.add(i);
        }
        if ("".equals(r.getRule()))
            this.rule = null;
        else
            this.rule = r.getRule().split(",");
        this.playerNum = r.getPlayerNum();
        this.adminId = r.getAdminId();
        this.banker = new Random().nextInt(playerNum);
        this.round = r.getRound();
        this.totalCount = r.getTotalCount();
        this.roomNum = r.getRoomNum();
        this.discard = 34;
    }

    public int[] getIsDissolution() {
        return isDissolution;
    }

    public void setIsDissolution(int[] isDissolution) {
        this.isDissolution = isDissolution;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public LinkedList<PlayerST> getPlayers() {
        return players;
    }

    public void setPlayers(LinkedList<PlayerST> players) {
        this.players = players;
    }

    public short getOption() {
        return option;
    }

    public Long getWin() {
        return win;
    }

    public void setWin(Long win) {
        this.win = win;
    }

    public void setOption(short option) {
        this.option = option;
    }

    public int getBanker() {
        return banker;
    }

    public List<Tip> getTips() {
        return tips;
    }

    public void setTips(List<Tip> tips) {
        this.tips = tips;
    }

    public void setBanker(int banker) {
        this.banker = banker;
    }

    public LinkedList<Integer> getPosistions() {
        return posistions;
    }

    public void setPosistions(LinkedList<Integer> posistions) {
        this.posistions = posistions;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public Long getPao() {
        return pao;
    }

    public void setPao(Long pao) {
        this.pao = pao;
    }

    public int getLeftDeskCardNum() {
        return leftDeskCardNum;
    }

    public void setLeftDeskCardNum(int leftDeskCardNum) {
        this.leftDeskCardNum = leftDeskCardNum;
    }

    public Queue<Integer> getLeftDeskCards() {
        return leftDeskCards;
    }

    public void setLeftDeskCards(Queue<Integer> leftDeskCards) {
        this.leftDeskCards = leftDeskCards;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int returnBeforePositon() {
        return current == this.playerNum - 1 ? 0 : current + 1;
    }

    public int returnNextPositon() {
        return (current - 1) < 0 ? this.playerNum - 1 : current - 1;
    }

    public int returnNextPositon(int p) {
        return (p - 1) < 0 ? this.playerNum - 1 : p - 1;
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

    public void setRule(String[] rule) {
        this.rule = rule;
    }

    public long getAdminId() {
        return adminId;
    }

    public void setAdminId(long adminId) {
        this.adminId = adminId;
    }

    public int getFirstBanker() {
        return firstBanker;
    }

    public void setFirstBanker(int firstBanker) {
        this.firstBanker = firstBanker;
    }

    public int getCurrentRount() {
        return currentRount;
    }

    public void setCurrentRount(int currentRount) {
        this.currentRount = currentRount;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
    }

    public int getApplyDissolution() {
        return applyDissolution;
    }

    public void setApplyDissolution(int applyDissolution) {
        this.applyDissolution = applyDissolution;
    }

    public Integer getBaoCard() {
        return baoCard;
    }

    public void setBaoCard(Integer baoCard) {
        this.baoCard = baoCard;
    }

    public List<Tip> getTempTips() {
        return tempTips;
    }

    public void setTempTips(List<Tip> tempTips) {
        this.tempTips = tempTips;
    }

    public Integer getHuCard() {
        return huCard;
    }

    public void setHuCard(Integer huCard) {
        this.huCard = huCard;
    }

    public int getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(int roomNum) {
        this.roomNum = roomNum;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public Integer[] getRoll() {
        return roll;
    }

    public void setRoll(Integer[] roll) {
        this.roll = roll;
    }

    public int getGodWealth() {
        return godWealth;
    }

    public void setGodWealth(int godWealth) {
        this.godWealth = godWealth;
    }

    public Operation getHistory() {
        return history;
    }

    public void setHistory(Operation history) {
        this.history = history;
    }

    public int getDiscard() {
        return discard;
    }

    public void setDiscard(int discard) {
        this.discard = discard;
    }

    public int getBankerScore() {
        return bankerScore;
    }

    public void setBankerScore(int bankerScore) {
        this.bankerScore = bankerScore;
    }

    public Long getDissolutionId() {
        return dissolutionId;
    }

    public void setDissolutionId(Long dissolutionId) {
        this.dissolutionId = dissolutionId;
    }

    public Integer getPositionById(long userId) {
        for (PlayerST p : this.getPlayers()) {
            if (p.getPlayerId() == userId)
                return p.getPosition();
        }
        return null;
    }

    public PlayerST getPlayerByPosition(int position) {
        for (PlayerST p : this.getPlayers()) {
            if (p.getPosition() == position) {
                return p;
            }
        }
        return null;
    }

    public PlayerST getPlayerById(long userId){
        for (PlayerST p : this.getPlayers()) {
            if (p.getPlayerId() == userId) {
                return p;
            }
        }
        return null;
    }
}
