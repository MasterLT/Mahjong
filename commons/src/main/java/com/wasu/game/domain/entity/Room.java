package com.wasu.game.domain.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "room")
public class Room {

	@Id
	@GeneratedValue
	private Long id;

	@Column(name="admin_id")
	private Long adminId;

	private int state;

	@Column(name="create_time")
	private Date createTime;

	@Column(name="destroy_time")
	private Date destroyTime;

	@Column(name="start_time")
	private Date startTime;

	@Column(name="total_count")
	private int totalCount;

	@Column(name="current_round")
	private int currentRound;

	private int round;

	private String password;

	@Column(name="room_num")
	private int roomNum;

	@Column(name="open_count")
	private int openCount;

	private int banker;

	@Column(name="player_num")
	private int playerNum;

	@Column(name="rule")
	private String rule;

	private Integer type;

	@Column(name="end_point")
	private Integer endPoint;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAdminId() {
		return adminId;
	}

	public void setAdminId(Long adminId) {
		this.adminId = adminId;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getDestroyTime() {
		return destroyTime;
	}

	public void setDestroyTime(Date destroyTime) {
		this.destroyTime = destroyTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public int getRoomNum() {
		return roomNum;
	}

	public void setRoomNum(int roomNum) {
		this.roomNum = roomNum;
	}

	public int getOpenCount() {
		return openCount;
	}

	public void setOpenCount(int openCount) {
		this.openCount = openCount;
	}

	public int getPlayerNum() {
		return playerNum;
	}

	public void setPlayerNum(int playerNum) {
		this.playerNum = playerNum;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public int getCurrentRound() {
		return currentRound;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setCurrentRound(int currentRound) {
		this.currentRound = currentRound;
	}

	public int getBanker() {
		return banker;
	}

	public void setBanker(int banker) {
		this.banker = banker;
	}

	public Integer getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(Integer endPoint) {
		this.endPoint = endPoint;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
}
