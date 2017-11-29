package com.wasu.game.module;
/**
 * 玩家模块
 * @author -琴兽-
 *
 */
public interface CardCmd {

	/**
	 * 发牌
	 */
	short SendCard = 5;
	/**
	 * 准备出牌
	 */
	short READYCHUPAI = 10;
	/**
	 * 出牌
	 */
	short CHUPAI = 11;

	/**
	 * 抓牌
	 */
	short ZHUAPAI = 12;

	/**
	 * 强制出牌
	 */
	short AUTOCHUPAI = 13;

	/**
	 * 碰
	 */
	short PENG = 14;
	/**
	 * 碰后出牌
	 */
        short OUTAFTERPENG=141;
	/**
	 * 吃
	 */
	short CHI = 15;

	/**
	 * 明杠
	 */
	short GANG = 16;

	/**
	 * 暗杠
	 */
	short ANGANG = 17;
	/**
	 * 过
	 */
	short PASS = 18;
	/**
	 * 听
	 */
	short TING = 19;
	/**
	 * 宝牌
	 */
	short BAO = 20;
	/**
	 * 抓牌明杠
	 */
	short MGANG2 = 22;

	/**
	 * 总战绩
	 */
	short TOTALRECORD = 23;

	/**
	 * 战绩
	 */
	short RECORD = 24;

	/**
	 * 要抢杠
	 */
	short QIANGGANG = 25;

	/**
	 * 其他断线重连
	 */
	short ONLINE = 97;
	/**
	 * 断线重连
	 */
	short RECONNECTED = 99;
	/**
	 * 碰听
	 */
	short PENGTING = 101;
	/**
	 * 吃听
	 */
	short CHITING = 102;
	/**
	 * 胡
	 */
	short HU = 106;
	/**
	 * 积分
	 */
	short END = 107;
	/**
	 * 所有牌
	 */
	short SHOWCARDS = 108;
	/**
	 * 房间使用完毕
	 */
	short GAMEOVER = 109;





	/**
	 * 提示
	 */
	short TIP = 21;
//	/**
//	 * 可以吃
//	 */
//	short CANEAT = 22;

	/**
	 * 解散
	 */
	short DISSOLUTION = 31;

	/**
	 * 管理员解散
	 */
	short DISSOLUTION2 = 32;


}
                                                  