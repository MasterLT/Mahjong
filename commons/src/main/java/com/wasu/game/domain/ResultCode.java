package com.wasu.game.domain;

public interface ResultCode {
	
	/**
	 * 成功
	 */
	int SUCCESS = 0;
	
	/**
	 * 找不到命令
	 */
	int NO_INVOKER = 1;
	
	/**
	 * 参数异常
	 */
	int AGRUMENT_ERROR = 2;
	
	/**
	 * 未知异常
	 */
	int UNKOWN_EXCEPTION = 3;
	
	/**
	 * 玩家名或密码不能为空
	 */
	int PLAYERNAME_NULL = 4;
	
	/**
	 * 玩家名已使用
	 */
	int PLAYER_EXIST = 5;
	
	/**
	 * 玩家不存在
	 */
	int PLAYER_NO_EXIST = 6;
	
	/**
	 * 未找到房间
	 */
	int ROOM_UNDEFIND = 7;
	
	/**
	 * 您已登录
	 */
	int HAS_LOGIN = 8;
	
	/**
	 * 登录失败
	 */
	int LOGIN_FAIL = 9;
	
	/**
	 * 玩家不在线
	 */
	int PLAYER_NO_ONLINE = 10;
	
	/**
	 * 请先登录
	 */
	int LOGIN_PLEASE = 11;
	
	/**
	 * 房间已满或该用户已进入
	 */
	int ROOMFUlL = 12;
	
	/**
	 * 未准备
	 */
	int UNREADY = 13;

	/**
	 * 无法删除
	 */
	int UNUSEABLECARD=14;
	/**
	 * 无房卡
	 */
	int LACKROOMCARD=15;
	/**
	 * 不是房主
	 */
	int WITHOUTPERMISSION=20;

	/**
	 *听出错
	 */
	int UNTING=16;

	/**
	 *胡牌出错出错
	 */
	int UNWIN=17;

	/**
	 *胡牌出错出错
	 */
	int ERROEPASSWORDR=18;

	/**
	 *不该这名玩家出牌
	 */
	int ERROROUTHAND=19;
}
