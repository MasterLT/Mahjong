package com.wasu.game.module.player.handler;

import com.wasu.game.annotion.SocketCommand;
import com.wasu.game.annotion.SocketModule;
import com.wasu.game.domain.Request;
import com.wasu.game.module.ModuleId;
import com.wasu.game.module.PlayerCmd;

/**
 * 玩家模块
 * @author -琴兽-
 *
 */
@SocketModule(module = ModuleId.PLAYER)
public interface PlayerHandler {

	/**
	 * 进入房间
	 * @return
	 */
	@SocketCommand(cmd = PlayerCmd.IntoRoom)
	public void intoRoom(Request data);

	/**
	 * 退出房间
	 * @return
	 */
	@SocketCommand(cmd = PlayerCmd.OutRoom)
	public void outRoome(Request data);

	/**
	 * 创建房间
	 * @return
	 */
	@SocketCommand(cmd = PlayerCmd.CreateRoom)
	public void createRoom(Request data);

	/**
	 * 准备
	 * @return
	 */
	@SocketCommand(cmd = PlayerCmd.Ready)
	public void ready(Request request);

	/**
	 * 房间信息
	 * @return
	 */
	@SocketCommand(cmd = PlayerCmd.ROOMINFO)
	public void roomInfo(Request request);

	/**
	 * 用户信息
	 * @return
	 */
	@SocketCommand(cmd = PlayerCmd.USERINFO)
	public void getUserInfo(Request data);

	/**
	 * 抽奖
	 * @return
	 */
	@SocketCommand(cmd = PlayerCmd.ACTIVITY)
	public void activity(Request data);

//	/**
//	 * 强制出牌
//	 * @return
//	 */
//	@SocketCommand(cmd = PlayerCmd.AUTOCHUPAI)
//	public void autoChuPai(long roomId, int position);
//
//	/**
//	 * 手动出牌
//	 * @return
//	 */
//	@SocketCommand(cmd = PlayerCmd.CHUPAI)
//	public void chuPai(Request data);
//
//	/**
//	 * 退出
//	 * @return
//	 */
//	@SocketCommand(cmd = PlayerCmd.OUT)
//	public void out(Request data);

//	/**
//	 * 碰牌
//	 * @return
//	 */
//	@SocketCommand(cmd = PlayerCmd.PENG)
//	public void peng(Request data);
//
//	/**
//	 * 判断能否吃
//	 * @return
//	 */
//	@SocketCommand(cmd = PlayerCmd.CANEAT)
//	public void isEat(Request data);
//
//	/**
//	 * 吃牌
//	 * @return
//	 */
//	@SocketCommand(cmd = PlayerCmd.CHI)
//	public void chi(Request data);
//
//	/**
//	 * 明杠
//	 * @return
//	 */
//	@SocketCommand(cmd = PlayerCmd.GANG)
//	public void gang(Request data);
//
//	/**
//	 * 抓牌
//	 * @return
//	 */
//	@SocketCommand(cmd = PlayerCmd.ZHUAPAI)
//	public void toZhuaPai(long roomId);

//	/**
//	 * 不吃
//	 * @return
//	 */
//	@SocketCommand(cmd = PlayerCmd.NOCHI)
//	public void noChi(Request data);
//
//	/**
//	 * 不碰
//	 * @return
//	 */
//	@SocketCommand(cmd = PlayerCmd.NOPENG)
//	public void noPeng(Request data);
}
