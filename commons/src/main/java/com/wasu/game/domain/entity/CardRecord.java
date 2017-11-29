package com.wasu.game.domain.entity;

import com.wasu.game.enums.CardChangeType;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "card_record")
public class CardRecord {

	@Id
	@GeneratedValue
	private Long id;

	@Column(name="player_id")
	private Long playerId;

	@Column(name="admin_id")
	private Long adminId;

	@Column(name="change_num")
	private int changeNum;

	@Column(name="card_num")
	private int cardNum;

	@Column(name="create_time")
	private Date createTime;

	private String text;

	private int type;

	public CardRecord() {
	}

	public CardRecord(long roomId, long adminId,int cardNum) {
		this.playerId=adminId;
		this.text=roomId+"";
		this.type= CardChangeType.KAI.getType();
		this.changeNum=-1;
		this.createTime=new Date();
		this.cardNum=cardNum;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	public Long getAdminId() {
		return adminId;
	}

	public void setAdminId(Long adminId) {
		this.adminId = adminId;
	}

	public int getChangeNum() {
		return changeNum;
	}

	public void setChangeNum(int changeNum) {
		this.changeNum = changeNum;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getCardNum() {
		return cardNum;
	}

	public void setCardNum(int cardNum) {
		this.cardNum = cardNum;
	}
}
