package com.wasu.game.module.player.handler;

import com.wasu.game.annotion.SocketCommand;
import com.wasu.game.annotion.SocketModule;
import com.wasu.game.domain.Request;
import com.wasu.game.module.AdminCmd;
import com.wasu.game.module.ModuleId;

/**
 * 玩家模块
 * @author -琴兽-
 *
 */
@SocketModule(module = ModuleId.ADMIN)
public interface AdminHandler {

	/**
	 * 管理员解散
	 * @return
	 */
	@SocketCommand(cmd = AdminCmd.DISSOLUTION)
	public void dissolution(Request data);

	/**
	 * 添加房卡
	 * @return
	 */
	@SocketCommand(cmd = AdminCmd.ADDCARD)
	public void addCard(Request data);

}
