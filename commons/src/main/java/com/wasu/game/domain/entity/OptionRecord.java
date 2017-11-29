package com.wasu.game.domain.entity;

import com.alibaba.fastjson.JSON;
import com.wasu.game.domain.RoomInf;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 玩家实体对象
 * @author -琴兽-
 *
 */
@Entity
@Table(name = "option_record")
public class OptionRecord implements Serializable {
	private static final long serialVersionUID = 17714666080613254L;

	@Id
	@GeneratedValue
	private long id;

	@Column(name="room_id")
	private long roomId;

	@Column(name="room_num")
	private int roomNum;

	@Column(name="option_record",length = 20000)
	private String optionRecord;
	
	@Column(name="create_time")
	private Date createTime;

	private int round;

	private int openCount;

	public OptionRecord() {}

	public OptionRecord(Room roomdb, RoomInf room) {
		this.setCreateTime(new Date());
		this.setOpenCount(roomdb.getOpenCount());
		this.setRoomNum(roomdb.getRoomNum());
		this.setRound(roomdb.getRound());
		this.setOptionRecord(JSON.toJSONString(room));
		this.roomId=roomdb.getId();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getRoomId() {
		return roomId;
	}

	public void setRoomId(long roomId) {
		this.roomId = roomId;
	}

	public int getRoomNum() {
		return roomNum;
	}

	public void setRoomNum(int roomNum) {
		this.roomNum = roomNum;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getOptionRecord() {
		return optionRecord;
	}

	public void setOptionRecord(String optionRecord) {
		this.optionRecord = optionRecord;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public int getOpenCount() {
		return openCount;
	}

	public void setOpenCount(int openCount) {
		this.openCount = openCount;
	}
}
