package com.wasu.game.module.player.service;

import com.google.common.collect.Lists;
import com.wasu.game.domain.*;
import com.wasu.game.domain.entity.OptionRecord;
import com.wasu.game.domain.entity.Record;
import com.wasu.game.domain.entity.Room;
import com.wasu.game.domain.entity.User;
import com.wasu.game.domain.response.RoomResponse;
import com.wasu.game.enums.CardType;
import com.wasu.game.exception.ErrorCodeException;
import com.wasu.game.module.player.dao.OptionRecordDao;
import com.wasu.game.module.player.dao.RecordDao;
import com.wasu.game.module.player.dao.RoomDao;
import com.wasu.game.module.player.dao.UserDao;
import com.wasu.game.service.OperationService;
import com.wasu.game.service.RoomService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * Created by Administrator on 2016/12/28.
 */
@Component
public class CardServiceImpl implements CardService {
    private static Logger logger = Logger.getLogger(PlayerServiceImpl.class);
    @Autowired
    private RoomDao roomDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private RecordDao recordDao;
    @Autowired
    private OptionRecordDao optionRecordDao;

    @Override
    public RoomResponse dissolution(long playerId, long roomId, int isDissolution) {
        RoomResponse res = new RoomResponse();
        res.setId(roomId);
        res.setDissolutionId(playerId);
        RoomInf room = RoomService.getRoom(roomId);
        if (isDissolution != 1) {//不同意解散
            room.setStatus(room.getLeftDeskCardNum() != 0 ? 1 : 0);
        } else {
            if (room.getStatus() == 3) {//解散状态
                room.getIsDissolution()[room.getPositionById(playerId)] = isDissolution;
                int count = 0;
                for (int i : room.getIsDissolution()) {
                    count = count + i;
                }
                if (count == room.getPlayerNum()) {
                    //解散
                    toDissolution(room);
                }
            } /*else if (room.getStatus() == 0) {//未开始
                if (room.getAdminId() != playerId) {
                    throw new ErrorCodeException(ResultCode.WITHOUTPERMISSION);
                } else {
                    //解散
                    toDissolution(room);
                }
            }*/ else {
                room.setApplyDissolution(room.getPositionById(playerId));
                room.setStatus(3);//待解散状态
                room.setDissolutionId(playerId);//申请解散人
                room.setIsDissolution(new int[room.getPlayerNum()]);
                room.getIsDissolution()[room.getPositionById(playerId)] = isDissolution;
            }
        }
        res.setState(room.getStatus());
        res.setApplyDissolution(room.getApplyDissolution());
        res.setIsDissolution(room.getIsDissolution());
        RoomService.setRoom(room);
        return res;
    }

    @Override
    public void distroyRoom(long roomId) {
        RoomInf roomInf = RoomService.getRoom(roomId);
        if (roomInf != null) {
            for (PlayerST playerST : roomInf.getPlayers()) {
                User user = userDao.getUserById(playerST.getPlayerId());
                user.setRoomId(0);
                userDao.updateUser(user);
            }
            RoomService.delRoom(roomId);
        }
        Room room = roomDao.getRoomById(roomId);
        room.setDestroyTime(new Date());
        room.setState(4);
        roomDao.updateRoom(room);
    }

    @Override
    public RoomOperationInfo getBao(long roomId) {
        RoomOperationInfo roomOperationInfo = null;
        List<PlayerInfo> playerInfos = null;
        PlayerInfo playerInfo = null;
        RoomInf roomInf = RoomService.getRoom(roomId);
        Integer bao = roomInf.getBaoCard();
        if (bao == null)
            return null;
        int leftCarsNum = roomInf.getLeftDeskCardNum();
        int poistion = roomInf.returnBeforePositon();
        List<Integer> cards = Lists.newArrayList(bao);

        Card card = new Card(CardType.BAO.getType(), cards, cards.size());

        List<Card> cardList = Lists.newArrayList(card);
        playerInfo = new PlayerInfo(roomInf.getPlayerByPosition(poistion), cardList);
        playerInfos = Lists.newArrayList(playerInfo);

        roomOperationInfo = new RoomOperationInfo(leftCarsNum, playerInfos, poistion, roomInf.getPlayerByPosition(poistion).getPlayerId());
        logger.info("宝牌:" + bao);
        return roomOperationInfo;
    }

    @Override
    public RoomOperationInfo getGodWealth(long roomId) {
        RoomOperationInfo roomOperationInfo = null;
        List<PlayerInfo> playerInfos = null;
        PlayerInfo playerInfo = null;
        RoomInf roomInf = RoomService.getRoom(roomId);
        Integer godWealth = roomInf.getGodWealth();
        if (godWealth == null)
            return null;
        int leftCarsNum = roomInf.getLeftDeskCardNum();
        int poistion = roomInf.getCurrent();
//        List<Integer> cards=Lists.newArrayList(godWealth);
//        Card card=new Card(CardType.GODWEALTH.getType(),cards,cards.size());
//        List<Card> cardList=Lists.newArrayList(card);
//        playerInfo=new PlayerInfo(roomInf.getPlayerByPosition(poistion),cardList);
//        playerInfos=Lists.newArrayList(playerInfo);
//        roomOperationInfo=new RoomOperationInfo(leftCarsNum,playerInfos,poistion,roomInf.getPlayerByPosition(poistion).getPlayerId());
        roomOperationInfo = RoomOperationInfo.build(Lists.newArrayList(godWealth), CardType.GODWEALTH.getType(), roomInf.getPlayerByPosition(poistion), leftCarsNum);
        logger.info("财神:" + godWealth);
        return roomOperationInfo;
    }

    @Override
    public RoomOperationInfo setGodWealth(long roomId) {
        RoomInf roomInf = RoomService.getRoom(roomId);
        Random random = new Random();
        Integer[] roll = new Integer[2];
        roll[0] = random.nextInt(6) + 1;
        roll[1] = random.nextInt(6) + 1;
        roomInf.setRoll(roll);
        Queue<Integer> leftDeskCards = roomInf.getLeftDeskCards();
        List<Integer> list = Lists.newArrayList(leftDeskCards);
        roomInf.setGodWealth(list.get(list.size() - 2));
        RoomService.setRoom(roomInf);
        RoomOperationInfo roomOperationInfo = RoomOperationInfo.build(Lists.newArrayList(roomInf.getGodWealth()), CardType.GODWEALTH.getType(), roomInf.getPlayerByPosition(roomInf.getCurrent()), roomInf.getLeftDeskCardNum());
        Card card = new Card(CardType.ROLL.getType(), Lists.newArrayList(roll), 2);
        roomOperationInfo.getPlayerInfos().get(0).getCards().add(card);
        return roomOperationInfo;
    }

    public void toDissolution(RoomInf room) {
        //解散房间
        Room r = roomDao.getRoomById(room.getRoomId());
        r.setDestroyTime(new Date());
        r.setState(4);//解散状态
        roomDao.updateRoom(r);
        //积分统计
        if (room.getStatus() != 0) {
            for (PlayerST p : room.getPlayers()) {
                //更新用户信息
                User userdb = userDao.getUserById(p.getPlayerId());
                userdb.setRoomId(0);
                userdb.setPoint(0);
                userDao.updateUser(userdb);
                //当前房间当前用户的积分汇总
                List<Record> totalList = recordDao.getRoomRecordByUserId(p.getPlayerId(), room.getRoomId(), 1);
                //当前房间当前用户每一句的积分
                List<Record> everyList = recordDao.getRoomRecordByUserId(p.getPlayerId(), room.getRoomId(), 2);
                if (totalList != null && everyList != null) {
                    Record total = totalList.get(0);
                    int point = 0;
                    for (Record record : everyList) {
                        point += record.getPoint();
                    }
                    total.setPoint(point);
                    recordDao.updateRecord(total);
                }
            }
//            OperationService.delOperation(room.getRoomId());
        }
        room.setStatus(4);
        RoomService.setRoom(room);
        //保存roominfo
        optionRecordDao.createOptionRecord(new OptionRecord(r, room));
        //删除记录
        RoomService.delRoom(room.getRoomId());
//        OperationService.delOperation(room.getRoomId());
    }
}
