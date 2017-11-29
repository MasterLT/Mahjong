package com.wasu.game.module.player.service;

import com.wasu.game.domain.HistoryRecord;
import com.wasu.game.domain.RoomInf;
import com.wasu.game.domain.Tip;
import com.wasu.game.domain.response.RoomOperationInfoResponse;
import com.wasu.game.domain.response.RoomResponse;

import java.util.List;

/**
 * 玩家服务
 *
 * @author -琴兽-
 */
public interface PlayerService {

    /**
     * 进入房间
     *
     * @param
     * @param playerId
     * @param roomNum
     * @return
     */
    public RoomResponse intoRoom(long playerId, int roomNum, String password) throws Exception;

    /**
     * 获取用户信息
     *
     * @param
     * @param roomId
     * @return
     */
    public RoomResponse getUserInfo(long roomId) throws Exception;

    /**
     * 退出房间
     *
     * @param
     * @param playerId
     * @param roomId
     * @return
     */
    public RoomResponse outRoom(long playerId, long roomId) throws Exception;

    /**
     * 创建房间
     *
     * @param
     * @param playerId
     * @param totalCount
     * @return
     */
    public RoomResponse createRoom(long playerId, int totalCount,int playerNum,String rule,String password) throws Exception;

    /**
     * 用户准备
     *
     * @param
     * @param playerId
     * @param roomId
     * @return
     */
    public RoomResponse ready(long playerId, long roomId);

    public RoomResponse roomInfo(long roomId);

    /**
     * 洗牌
     *
     * @return
     */
    public List<RoomOperationInfoResponse> shuffle1(RoomInf roomInf);

    /**
     * 抓牌
     *
     * @param roomInf
     * @param
     * @return
     */
    public List<RoomOperationInfoResponse> deal1(RoomInf roomInf);

    /**
     * 出牌
     *
     * @param roomInf
     * @param
     * @param cardNum
     * @return
     */
    public List<RoomOperationInfoResponse> outHand1(RoomInf roomInf, int cardNum);

    /**
     * 碰牌提示
     * @param
     * @param cardNum
     * @param position
     * @return
     */
    public Tip isPeng(Long roomId, int cardNum, int position);

    /**
     * 碰牌
     * @param roomInf
     * @param cardNum
     * @param prePosition
     * @return
     */
    public List<RoomOperationInfoResponse> peng1(RoomInf roomInf, int cardNum, int position, int prePosition);

    /**
     * 杠牌判断
     * @param
     * @param cardNum
     * @param position
     * @return
     */
    public Tip isGang(Long roomId, int cardNum, int position);
    public Tip isMingGang2(Long roomId);
    public List<RoomOperationInfoResponse> mingGang2(Long roomId,Integer card);

    /**
     * 暗杠
     * @param roomInf
     * @param cardNum
     * @param
     * @return
     */
    public List<RoomOperationInfoResponse> anGang1(RoomInf roomInf, int cardNum);
    /**
     * 明杠
     * @param roomInf
     * @param cardNum
     * @param prePosition
     * @return
     */
     public  List<RoomOperationInfoResponse> mingGang1(RoomInf roomInf, int cardNum, int position,int prePosition);

    /**
     * 判断下家是否能吃
     * @param
     * @param prePosition
     * @return
     */
    public Tip isChi(Long roomId, int prePosition) ;

    /**
     *
     * @param roomInf
     * @param cards
     * @param prePosition
     * @return
     */
    List<RoomOperationInfoResponse> chi1(RoomInf roomInf,List<Integer> cards, int prePosition);


    public Tip isAnGang(Long roomId, int cardNum, int position);

    /**
     *
     * @param roomId
     * @param cardNum
     * @param prePosition 出牌人位置或抓拍人位置
     * @return
     */
    public List<Tip> isHu(Long roomId, int cardNum,int prePosition);

    public List<Tip> isHu(Long roomId, int prePosition);

    public List<Tip> isQiangGang(Long roomId, int cardNum);

    /**
     *
     * @param
     * @param prePositin //打牌后才听 位置以改变  这个传的前一个位置
     * @return
     */
    public Tip isTing(Long roomId,int prePositin);

    /**
     *
     * @param roomInf
     * @param prePositin// 以为已出牌  听牌人的位置是前一个
     * @return
     */
    public List<Tip> ting(RoomInf roomInf, int prePositin, int outCard);

    /**
     *
     * @param
     * @param prePosition 被吃听人的位置
     * @return
     */
    public List<Tip> isChiTing(Long roomId,int prePosition);

    /**
     *
     * @param roomInf
     * @param cards 吃牌的组合
     * @param prePosition 被吃的 玩家的位置
     * @return
     */

    public List<Tip> chiTing(RoomInf roomInf,List<Integer> cards,int currentPosition, int prePosition,int outCard);

    /**
     *
     * @param
     * @param prePosition 被碰的  玩家的位置
     * @return
     */
    public List<Tip> isPengTing(Long roomId,int prePosition);


    /**
     *
     * @param roomInf
     * @param cards 碰听  打牌组合
     * @param currentPosition  pengting 玩家的位置
     * @param prePosition
     * @return
     */
    public List<Tip> pengTing(RoomInf roomInf,List<Integer> cards,int currentPosition, int prePosition,int outCard);

    public List<Tip> hu(RoomInf roomInf,int cardNum,int position);
    public List<Tip> over(long roomId);

    public RoomInf returnRoomInf(long roomId, Long playerId);

    public List<HistoryRecord> getHistoryRecord(long playerId);

    HistoryRecord getRecord(long playerId, long roomId);

    /**
     * 房卡
     *
     * @param
     * @param playerId
     * @return
     */
    public int getPermission(long playerId);

    /**
     * 距离
     *
     * @param
     * @param playerId
     * @return
     */
    public void distance(long roomId, long playerId);

    int activity(long playerId, int type);
}
