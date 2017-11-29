package com.wasu.game.module.player.core;

import com.wasu.game.proto.PlayerModule;

/**
 * Created by Administrator on 2016/12/28.
 */

public interface LinkedHandler  {

    void pop(long roomId,Integer outCard);

    void push(long roomId);

    void push1(long roomId);

}
