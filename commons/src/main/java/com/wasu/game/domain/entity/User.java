package com.wasu.game.domain.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 玩家实体对象
 * @author -琴兽-
 *
 */
@Entity
@Table(name = "user")
public class User implements Serializable {
	private static final long serialVersionUID = 87714666080613254L;
	/**
	 * 玩家id
	 */
	@Id
	@GeneratedValue
	private long id;
	
	private String name;
	
	private String password;
	
	private int gold;
	
	private int point;
	
	private String face;
	
	@Column(name="login_times")
	private int loginTimes;
	
	@Column(name="register_ip")
	private String registerIp;
	
	@Column(name="login_ip")
	private String loginIp;
	
	@Column(name="register_date")
	private Date registerDate;
	
	@Column(name="login_date")
	private Date loginDate;
	
	@Column(name="win_count")
	private int winCount;
	
	@Column(name="lost_count")
	private int lostCount;
	
	@Column(name="flee_count")
	private int fleeCount;
	
	@Column(name="draw_count")
	private int drawCount;
	
	@Column(name="w_id")
	private String wId;
	
	private int gender;//0男，1女

	private int state;

	private String token;
	
	@Column(name="room_id")
	private long roomId;
	
	private int position;

	private int type;//0玩家；1房主

	@Column(name="real_name")
	private String realName;

	@Column(name="id_num")
	private String idNum;

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public int getLoginTimes() {
		return loginTimes;
	}

	public void setLoginTimes(int loginTimes) {
		this.loginTimes = loginTimes;
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

	public Date getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
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

	public int getState() {
		return state;
	}

	public void setState(int state) {
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
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
}
