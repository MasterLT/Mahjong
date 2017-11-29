package com.wasu.game.login.service;

import com.wasu.game.domain.response.ConfigResponse;
import com.wasu.game.domain.Session;
import com.wasu.game.domain.response.UserResponse;

import java.util.List;
import java.util.Map;

/**
 * 玩家服务
 *
 * @author -琴兽-
 */
public interface LoginService {

    /**
     * 微信登录
     *
     * @param session
     * @param wId
     * @return
     */
    public UserResponse weixinLogin(Session session, String wId, String face, String name, String gender) throws Exception;

    public List<Map<String,String>> config() throws Exception;

    public void leave(Long id) throws Exception;

    public void link(Session session,String wid);

    public int authen(int id,String realName,String idNum);

}
