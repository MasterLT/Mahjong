package com.wasu.game.login.handler;

import com.alibaba.fastjson.JSONObject;
import com.wasu.game.annotion.SocketCommand;
import com.wasu.game.annotion.SocketModule;
import com.wasu.game.domain.response.ConfigResponse;
import com.wasu.game.domain.Result;
import com.wasu.game.domain.Session;
import com.wasu.game.domain.response.UserResponse;
import com.wasu.game.module.ModuleId;
import com.wasu.game.module.PlayerCmd;

import java.util.List;
import java.util.Map;


/**
 * 玩家模块
 * @author -琴兽-
 *
 */
@SocketModule(module = ModuleId.Login)
public interface PlayerHandler {

	/**
	 * 离线
	 * @return
	 */
	@SocketCommand(cmd = PlayerCmd.Leave)
	public void leave(Long id);

	/**
	 * 登录帐号
	 * @return
	 */
	@SocketCommand(cmd = PlayerCmd.WeiXinLOGIN)
	public Result<UserResponse> weixinLogin(Session session, JSONObject data);

	/**
	 * 链接处理
	 * @return
	 */
	@SocketCommand(cmd = PlayerCmd.LINK)
	public void link(Session session, String wid);

	/**
	 * 配置信息
	 * @return
	 */
	@SocketCommand(cmd = PlayerCmd.CONFIG)
	public Result<List<Map<String,String>>> config(Session session, JSONObject data);

	/**
	 * 进行实名认证
	 * @return
	 */
	@SocketCommand(cmd = PlayerCmd.AUTHEN)
	public Result<Integer> authen(Session session, JSONObject data);

}
