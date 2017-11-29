package com.wasu.game.module.player.handler;

import com.wasu.game.annotion.SocketCommand;
import com.wasu.game.annotion.SocketModule;
import com.wasu.game.domain.Request;
import com.wasu.game.module.CardCmd;
import com.wasu.game.module.ModuleId;

/**
 * 玩家模块
 * @author -琴兽-
 *
 */
@SocketModule(module = ModuleId.CARD)
public interface CardHandler {

	/**
	 * 出牌
	 * @return
	 */
	@SocketCommand(cmd = CardCmd.CHUPAI)
	public void chuPai(Request data);

	/**
	 * 碰牌
	 * @return
	 */
	@SocketCommand(cmd = CardCmd.PENG)
	public void peng(Request data);

	/**
	 * 吃牌
	 * @return
	 */
	@SocketCommand(cmd = CardCmd.CHI)
	public void chi(Request data);

	/**
	 * 明杠
	 * @return
	 */
	@SocketCommand(cmd = CardCmd.GANG)
	public void mgang(Request data);

	/**
	 * 抓牌明杠
	 * @return
	 */
	@SocketCommand(cmd = CardCmd.MGANG2)
	public void mgang2(Request data);

	/**
	 * 暗杠
	 * @return
	 */
	@SocketCommand(cmd = CardCmd.ANGANG)
	public void agang(Request data);

	/**
	 * 过
	 * @return
	 */
	@SocketCommand(cmd = CardCmd.PASS)
	public void pass(Request data);

	/**
	 * 胡
	 * @return
	 */
	@SocketCommand(cmd = CardCmd.HU)
	public void hu(Request data);

	/**
	 * 听
	 * @return
	 */
	@SocketCommand(cmd = CardCmd.TING)
	public void ting(Request data);

	/**
	 * 吃听
	 * @return
	 */
	@SocketCommand(cmd = CardCmd.CHITING)
	public void chiTing(Request data);

	/**
	 * 碰听
	 * @return
	 */
	@SocketCommand(cmd = CardCmd.PENGTING)
	public void pengTing(Request data);

	/**
	 * 解散
	 * @return
	 */
	@SocketCommand(cmd = CardCmd.DISSOLUTION)
	public void dissolution(Request data);

	/**
	 * 断线重连
	 * @return
	 */
	@SocketCommand(cmd = CardCmd.RECONNECTED)
	public void reconnected(Request data);

	/**
	 * 总战绩
	 * @return
	 */
	@SocketCommand(cmd = CardCmd.TOTALRECORD)
	public void totalRecord(Request data);

	/**
	 * 分战绩
	 * @return
	 */
	@SocketCommand(cmd = CardCmd.RECORD)
	public void record(Request data);
}
