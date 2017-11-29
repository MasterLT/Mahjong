package com.wasu.game.login.service;

import com.wasu.game.domain.entity.User;

/**
 * Created by kale on 2016/12/13.
 */
public interface RongYunservice {
    /**
     * 获取token，如果有token直接返回，没有token调用接口获取token，保存后返回
     * @param user
     * @return
     */
    public String getToken(User user) throws  Exception;
}
