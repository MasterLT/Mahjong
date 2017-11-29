package com.wasu.game.domain.response;

import com.wasu.game.domain.response.PlayerInfoResponse;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/2/6.
 */
public class RoomOperationInfoResponse implements Serializable {
    private static final long serialVersionUID = 8247814666081111444L;
    private int leftDeskCardNum;
    private int operation;
    private List<PlayerInfoResponse> playerInfos;
    private int status;

    public RoomOperationInfoResponse() {
    }

    public RoomOperationInfoResponse(int leftDeskCardNum, List<PlayerInfoResponse> playerInfos, int status) {
        this.leftDeskCardNum = leftDeskCardNum;
        this.playerInfos = playerInfos;
        this.status = status;
    }

    public RoomOperationInfoResponse(int leftDeskCardNum, List<PlayerInfoResponse> playerInfos) {
        this.leftDeskCardNum = leftDeskCardNum;
        this.playerInfos = playerInfos;
    }

    public int getLeftDeskCardNum() {
        return leftDeskCardNum;
    }

    public void setLeftDeskCardNum(int leftDeskCardNum) {
        this.leftDeskCardNum = leftDeskCardNum;
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public List<PlayerInfoResponse> getPlayerInfos() {
        return playerInfos;
    }

    public void setPlayerInfos(List<PlayerInfoResponse> playerInfos) {
        this.playerInfos = playerInfos;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
