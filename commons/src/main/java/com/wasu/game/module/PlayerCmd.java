package com.wasu.game.module;
/**
 * 玩家模块
 * @author -琴兽-
 *
 */
public interface PlayerCmd {
	/**
	 * 断线
	 */
	short Leave = 0;

	/**
	 * 微信登录
	 */
	short WeiXinLOGIN = 1;

	/**
	 * 进入房间
	 */
	short IntoRoom = 2;

	/**
	 * 创建房间
	 */
	short CreateRoom = 3;

	/**
	 * 准备
	 */
	short Ready = 4;

	/**
	 * 发牌
	 */
	short SendCard = 5;

	/**
	 * 退出房间
	 */
	short OutRoom = 6;

	/**
	 * 财神
	 */
	short GOD = 7;

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
	 * 配置
	 */
	short CONFIG = 13;
	/**
	 * 用户信息
	 */
	short USERINFO = 98;
	/**
	 * 房间信息
	 */
	short ROOMINFO = 200;

	/**
	 * 退出
	 */
	short OUT = 0;
	/**
	 * 断线重连
	 */
	short RECONNECTED = 99;

	/**
	 * 存储连接
	 */
	short LINK = 100;

	/**
	 * 距离提示
	 */
	short DISTANCE = 201;

	/**
	 * 进行实名认证
	 */
	short AUTHEN = 202;

	/**
	 * 当天是否抽过奖
	 */
//	short ISACTIVITY = 203;

	/**
	 * 抽奖
	 */
	short ACTIVITY = 203;

}
                                                  