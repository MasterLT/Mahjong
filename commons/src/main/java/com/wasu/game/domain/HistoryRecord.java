package com.wasu.game.domain;

import com.wasu.game.domain.entity.Record;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by admin on 2017/5/9.
 */
public class HistoryRecord implements Serializable {
    private static final long serialVersionUID = 166080613254L;

    private int roomNum;
    private long roomId;
    private Date startTime;
    private List<UserRecord> userRecords;
    private List<List<Integer>> points;

    public HistoryRecord(){}

    public HistoryRecord(Record record){
        this.roomNum=record.getRoomNum();
        this.roomId=record.getRoomId();
        this.startTime=record.getStartTime();
    }

    public int getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(int roomNum) {
        this.roomNum = roomNum;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public List<UserRecord> getUserRecords() {
        return userRecords;
    }

    public void setUserRecords(List<UserRecord> userRecords) {
        this.userRecords = userRecords;
    }

    public List<List<Integer>> getPoints() {
        return points;
    }

    public void setPoints(List<List<Integer>> points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "HistoryRecord{" +
                "roomNum=" + roomNum +
                ", roomId=" + roomId +
                ", startTime=" + startTime +
                ", userRecords=" + userRecords +
                ", points=" + points +
                '}';
    }
}
