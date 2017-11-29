package com.wasu.game.domain;

import com.wasu.game.domain.entity.Record;

import java.io.Serializable;

/**
 * Created by admin on 2017/5/9.
 */
public class UserRecord implements Serializable {
    private static final long serialVersionUID = 266080613254L;

    private long userId;
    private String name;
    private int point;

    public UserRecord(){}

    public UserRecord(Record record){
        this.userId=record.getUserId();
        this.point=record.getPoint();
        this.name=record.getUserName();
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return "UserRecord{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", point=" + point +
                '}';
    }
}
