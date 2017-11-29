package com.wasu.game.domain;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

public class RoomOperationInfo implements Serializable{
	private static final long serialVersionUID = 8247814666080613251L;
	private int leftDeskCardNum;//剩余牌的个数
	private List<PlayerInfo> playerInfos;//玩家
       private int position;
	private long userId;
	private int operation;//操作 
	private int status;//状态

	public RoomOperationInfo() {
	}

	/**
	 *
	 * @param list 牌
	 * @param type 牌类型
	 * @param player 玩家
     * @param leftCarsNum 剩余牌
     */
	public static RoomOperationInfo build(List<Integer> list, int type, PlayerST player,int leftCarsNum){
		List<Integer> cards= Lists.newArrayList(list);
		Card card=new Card(type,cards,cards.size());
		List<Card> cardList=Lists.newArrayList(card);
		PlayerInfo playerInfo=new PlayerInfo(player,cardList);
		List<PlayerInfo> newPlayerInfos=Lists.newArrayList(playerInfo);
		return new RoomOperationInfo(leftCarsNum,newPlayerInfos,player.getPosition(),player.getPlayerId());
	}

	public RoomOperationInfo(int status, int leftDeskCardNum, List<PlayerInfo> playerInfos, int position, long userId, int operation) {
		this.status = status;
		this.leftDeskCardNum = leftDeskCardNum;
		this.playerInfos = playerInfos;
		this.position = position;
		this.userId = userId;
		this.operation = operation;
	}

	public RoomOperationInfo(int leftDeskCardNum, List<PlayerInfo> playerInfos, int position, long userId) {
		this.leftDeskCardNum = leftDeskCardNum;
		this.playerInfos = playerInfos;
		this.position = position;
		this.userId = userId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public int getLeftDeskCardNum() {
		return leftDeskCardNum;
	}

	public void setLeftDeskCardNum(int leftDeskCardNum) {
		this.leftDeskCardNum = leftDeskCardNum;
	}

	public List<PlayerInfo> getPlayerInfos() {
		return playerInfos;
	}

	public void setPlayerInfos(List<PlayerInfo> playerInfos) {
		this.playerInfos = playerInfos;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public int getOperation() {
		return operation;
	}

	public void setOperation(int operation) {
		this.operation = operation;
	}

	@Override
	public String toString() {
		return "RoomOperationInfo{" +
				"leftDeskCardNum=" + leftDeskCardNum +
				", playerInfos=" + playerInfos +
				", position=" + position +
				", userId=" + userId +
				", operation=" + operation +
				", status=" + status +
				'}';
	}
}
