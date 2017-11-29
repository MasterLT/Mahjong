package com.wasu.game.domain.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by 琴兽 on 2016/12/13.
 */
@Entity
@Table(name = "activity")
public class Activity {
    @Id
    @GeneratedValue
    private long id;

    @Column(name="user_id")
    private long userId;

    @Column(name="card_num")
    private int cardNum;

    @Column(name="create_time")
    private Date createTime;

    private int type;//1:抽奖，2：分享

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getCardNum() {
        return cardNum;
    }

    public void setCardNum(int cardNum) {
        this.cardNum = cardNum;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
