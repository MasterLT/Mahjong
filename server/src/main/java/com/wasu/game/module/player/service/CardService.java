package com.wasu.game.module.player.service;

import com.wasu.game.domain.RoomInf;
import com.wasu.game.domain.RoomOperationInfo;
import com.wasu.game.domain.response.RoomResponse;

/**
 * Created by Administrator on 2016/12/28.
 */
public interface CardService {

    public RoomResponse dissolution(long playerId,long roomId,int isDissolution);

    public void distroyRoom(long roomId);

    public void toDissolution(RoomInf room);

    public RoomOperationInfo getBao(long roomId);

    public RoomOperationInfo getGodWealth(long roomId);

    public RoomOperationInfo setGodWealth(long roomId);
}
