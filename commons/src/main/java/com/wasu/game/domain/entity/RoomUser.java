package com.wasu.game.domain.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "room_user")
public class RoomUser {

	@Id
	@GeneratedValue
	private Long id;

	@Column(name="room_id")
	private Long roomId;

	@Column(name="user_id")
	private Long userId;

	private int state;

	@Column(name="create_time")
	private Date createTime;

	@Column(name="room_num")
	private int roomNum;

	public RoomUser() {
	}

    public RoomUser(long roomId, int roomNum, long playerId) {
		this.roomId=roomId;
		this.roomNum=roomNum;
		this.userId=playerId;
		this.createTime=new Date();
    }

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public int getRoomNum() {
		return roomNum;
	}

	public void setRoomNum(int roomNum) {
		this.roomNum = roomNum;
	}
}
