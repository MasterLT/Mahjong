package com.wasu.game.domain;

import java.io.Serializable;
import java.util.List;

public class PlayerInfo implements Serializable{
	private static final long serialVersionUID = 8247814666081111111L;
	private Long userid;//id
	private int position;//位置
	private int status;
	private List<Card> cards;//牌
	
	public PlayerInfo() {
		super();
	}
	public PlayerInfo(PlayerST playerST,List<Card> cards) {
		super();
		this.position=playerST.getPosition();
		this.userid=playerST.getPlayerId();
		this.cards = cards;
	}
	public PlayerInfo(Long userid, int position, List<Card> cards) {
		super();
		this.userid = userid;
		this.position = position;
		this.cards = cards;
	}


	public static long getSerialVersionUID() {
		return serialVersionUID;
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}

	@Override
	public String toString() {
		return "PlayerInfo{" +
				"userid=" + userid +
				", position=" + position +
				", status=" + status +
				", cards=" + cards +
				'}';
	}
}
