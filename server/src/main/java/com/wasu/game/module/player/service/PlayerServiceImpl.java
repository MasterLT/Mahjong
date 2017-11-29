package com.wasu.game.module.player.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wasu.game.activeMQ.JmsTypeEnum;
import com.wasu.game.activeMQ.MQProductor;
import com.wasu.game.domain.*;
import com.wasu.game.domain.entity.*;
import com.wasu.game.domain.response.RoomOperationInfoResponse;
import com.wasu.game.domain.response.RoomResponse;
import com.wasu.game.domain.response.UserResponse;
import com.wasu.game.enums.*;
import com.wasu.game.enums.jy.HuType;
import com.wasu.game.enums.jy.RuleType;
import com.wasu.game.exception.ErrorCodeException;
import com.wasu.game.module.CardCmd;
import com.wasu.game.module.ModuleId;
import com.wasu.game.module.PlayerCmd;
import com.wasu.game.module.player.dao.*;
import com.wasu.game.module.player.untils.*;
import com.wasu.game.service.LockService;
import com.wasu.game.service.OperationService;
import com.wasu.game.service.RoomService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 玩家服务
 *
 * @author -琴兽-
 */
@Component
public class PlayerServiceImpl implements PlayerService {

    private static Logger logger = Logger.getLogger(PlayerServiceImpl.class);

    @Autowired
    private UserDao userDao;
    @Autowired
    private PermissionDao permissionDao;
    @Autowired
    private RoomDao roomDao;
    @Autowired
    private RecordDao recordDao;
    @Autowired
    private CardRecordDao cardRecordDao;
    @Autowired
    private RoomUserDao roomUserDao;
    @Autowired
    private ActivityDao activityDao;
    @Autowired
    private ConfigDao configDao;
    @Autowired
    private CreateTipUtils createTipUtils;

    @Override
    public RoomResponse intoRoom(long playerId, int roomNum, String password) throws Exception {
        long start = System.currentTimeMillis();
        RoomResponse roomResponse = null;
        User user = userDao.getUserById(playerId);
        user.setPoint(0);
        Room room = roomDao.getRoomByRoomNum(roomNum);

        logger.info("intoRoom time:" + (System.currentTimeMillis() - start));

        // 检验房间的存在
        if (user == null || room == null || room.getTotalCount() - room.getOpenCount() < 0) {
            throw new ErrorCodeException(ResultCode.ROOM_UNDEFIND);
        }
        /*if (!password.equals(room.getPassword())) {
            throw new ErrorCodeException(ResultCode.ERROEPASSWORDR);
        }*/
        long roomId = room.getId();
        // 该用户加入房间
        if (RoomService.putRoom(roomId, playerId, room)) {
            RoomInf roomInf = RoomService.getRoom(roomId);
            // 更新用户状态
            user.setRoomId(roomId);
            userDao.updateUser(user);
            List<User> users = new ArrayList<User>();
            for (PlayerST p : roomInf.getPlayers()) {
                users.add(userDao.getUserById(p.getPlayerId()));
            }
            roomResponse = getRoomUserIfo(room, users, roomInf);
        } else {
            throw new ErrorCodeException(ResultCode.ROOMFUlL);
        }
        return roomResponse;
    }

    @Override
    public RoomResponse getUserInfo(long roomId) throws Exception {
        RoomResponse roomResponse = null;
        Room room = roomDao.getRoomById(roomId);
        RoomInf roomInf = RoomService.getRoom(roomId);
        List<Long> userIds = new ArrayList<Long>();
        for (PlayerST p : roomInf.getPlayers()) {
            userIds.add(p.getPlayerId());
        }
        List<User> users = userDao.getUserByIds(userIds);
        roomResponse = getRoomUserIfo(room, users, roomInf);
        return roomResponse;
    }

    @Override
    public RoomResponse createRoom(long playerId, int totalCount, int playerNum, String rule, String password) throws Exception {
        //查询房卡
        Permission permission = permissionDao.getPermissionByUserId(playerId);
        List<Room> rooms = roomDao.getRoomByAdminId(playerId, 0);
        if (permission != null && permission.getPermissionNum() > 0 && permission.getPermissionNum() > rooms.size()) {
            Room room = new Room();
            /*if (playerNum == 4)
                room.setRound(totalCount);
            else
                */
            room.setTotalCount(totalCount);
            room.setCreateTime(new Date());
            room.setAdminId(playerId);
            room.setState(0);
            room.setBanker(0);
            room.setPlayerNum(playerNum);
            room.setRule(rule);
            room.setPassword(password);
            int roomNum = 100000 + new Random().nextInt(900000);
            while (roomDao.getRoomByRoomNum(roomNum) != null) {
                roomNum = 100000 + new Random().nextInt(900000);
            }
            room.setRoomNum(roomNum);
            room = roomDao.createRoom(room);
            return intoRoom(playerId, room.getRoomNum(), password);
        }
        throw new ErrorCodeException(ResultCode.LACKROOMCARD);
    }

    @Override
    public RoomResponse ready(long playerId, long roomId) {
        RoomResponse roomResponse = null;
        Room room = roomDao.getRoomById(roomId);
        User user = userDao.getUserById(playerId);
        // 检验房间的存在
        if (user == null && room == null && room.getTotalCount() - room.getOpenCount() > 0)
            throw new ErrorCodeException(ResultCode.ROOM_UNDEFIND);

        Long currentTime=System.currentTimeMillis();
        //获取锁
        while (true){
            if (LockService.setLock(roomId,currentTime))
                break;
            //如果死锁超过2秒,释放锁
            if (currentTime-LockService.getLock(roomId)>2000){
                LockService.delLock(roomId);
                if (LockService.setLock(roomId,currentTime))
                    break;
            }
            logger.info("等待获取准备锁roomId"+roomId);
        }

        // 该用户加入房间
        if (RoomService.userReady(roomId, playerId)) {
            RoomInf roomInf = RoomService.getRoom(roomId);
            roomResponse = getRoomUserIfoSimple(roomInf, user);
            logger.info("用户:" + playerId + " ready方法:" + roomResponse.toString());
            if (RoomService.isRoomAllReady(roomId)) {
                if (room.getOpenCount() == 0) {
                    //修改房间状态
                    room.setState(1);
                    room.setStartTime(new Date());
                    //扣除房卡
                    Permission permission = permissionDao.getPermissionByUserId(room.getAdminId());
                    //根据局数动态扣除房卡
                    Config config=configDao.getConfigByPlayerNum(roomInf.getPlayerNum());
                    permission.setPermissionNum(permission.getPermissionNum() - roomInf.getTotalCount()/Integer.parseInt(config.getConfigValue()));
                    permissionDao.updateRoom(permission);
                    //房卡记录
                    CardRecord cardRecord=new CardRecord(roomInf.getRoomId(),roomInf.getAdminId(),permission.getPermissionNum());
                    cardRecordDao.createUser(cardRecord);
                    //房卡用户记录
                    for(PlayerST playerST:roomInf.getPlayers()){
                        RoomUser roomUser=new RoomUser(roomInf.getRoomId(),roomInf.getRoomNum(),playerST.getPlayerId());
                        roomUserDao.createUser(roomUser);
                    }
                    //積分制為0
                    for(PlayerST playerST:roomInf.getPlayers()){
                        User u=userDao.getUserById(playerST.getPlayerId());
                        u.setPoint(0);
                        userDao.updateUser(u);
                    }
                    //初始化庄家，并计算圈数
                    int banker = new Random().nextInt(room.getPlayerNum());
                    room.setBanker(banker);
                    roomInf.setBanker(banker);
                    roomInf.setCurrent(banker);
                    roomInf.setFirstBanker(banker);
                }
                for (PlayerST p : roomInf.getPlayers()) {
                    //添加积分记录
                    if (room.getOpenCount() == 0)
                        createRecord(1, room, p.getPlayerId(), null);
                    createRecord(2, room, p.getPlayerId(), room.getOpenCount() + 1);
                }
                //换庄家
                if (room.getOpenCount() != 0) {
                    if (roomInf.getWin() == null || roomInf.getPlayerByPosition(roomInf.getBanker()).getPlayerId() != roomInf.getWin() || isBankRule(roomInf)) {
                        roomInf.setBanker(roomInf.returnNextPositon(roomInf.getBanker()));
                        roomInf.setCurrent(roomInf.getBanker());
                        roomInf.setBankerScore(0);
                        room.setBanker(roomInf.getBanker());
                        //下一个坐庄的为第一个坐庄的则圈数+1
                        if (roomInf.getFirstBanker() == roomInf.getBanker() && room.getRound() > 0) {
                            roomInf.setCurrentRount(roomInf.getCurrentRount() + 1);
                            room.setCurrentRound(room.getCurrentRound() + 1);
                        }
                    }
                } else {
                    roomInf.setCurrentRount(1);
                    roomInf.setRound(room.getRound());
                    room.setCurrentRound(1);
                }
                //开局数加一
                room.setOpenCount(room.getOpenCount() + 1);
                roomInf.setCurrentCount(roomInf.getCurrentCount() + 1);
                roomInf.setStatus(1);
                roomInf.setWin(null);
                RoomService.setRoom(roomInf);
                roomDao.updateRoom(room);
            }

            //释放锁
            if (LockService.getLock(roomId).equals(currentTime))
                LockService.delLock(roomId);
        } else {
            throw new ErrorCodeException(ResultCode.ROOMFUlL);
        }
        return roomResponse;
    }

    private boolean isBankRule(RoomInf roomInf) {
        if (roomInf.getRule() != null && Lists.newArrayList(roomInf.getRule()).contains(RuleType.ZHUANG.getType() + "") && roomInf.getBankerScore() > 50)
            return true;
        return false;
    }

    @Override
    public RoomResponse roomInfo(long roomId) {
        RoomResponse roomResponse = new RoomResponse(roomDao.getRoomById(roomId), null);
        return roomResponse;
    }

    public void createRecord(int type, Room room, long playerId, Integer index) {
        Record record = new Record();
        record.setStartTime(new Date());
        record.setUserName(userDao.getUserById(playerId).getName());
        record.setRoomId(room.getId());
        record.setRoomNum(room.getRoomNum());
        record.setUserId(playerId);
        record.setType(type);
        record.setPoint(0);
        record.setBankerTime(0);
        record.setBaoTime(0);
        record.setGunTime(0);
        record.setZhanTime(0);
        record.setBigBaoTime(0);
        record.setWinTime(0);
        if (index != null)
            record.setInnings(index);
        recordDao.createRecord(record);
    }

    /**
     * 获取房间用户信息
     *
     * @return
     */
    public static RoomResponse getRoomUserIfo(Room room, List<User> all, RoomInf roomInf) {
        RoomResponse roomResponse = null;
        // 创建房间各玩家对象
        List<UserResponse> user = new ArrayList<UserResponse>();

        for (User s : all) {
            UserResponse u = new UserResponse(s);
            u.setRoomId(room.getId());
            u.setPosition(roomInf.getPositionById(u.getId()));
            u.setState(RoomService.getRoomUserState(room.getId(), u.getId()));
            user.add(u);
        }
        // 房间玩家放入房间对象
        roomResponse = new RoomResponse(room, user);
        roomResponse.setBanker(room.getBanker());
        return roomResponse;
    }

    /**
     * 获取房间用户简单信息
     *
     * @return
     */
    public static RoomResponse getRoomUserIfoSimple(RoomInf room, User u) {
        RoomResponse roomResponse = new RoomResponse();
        // 创建房间各玩家对象
        List<UserResponse> user = new ArrayList<UserResponse>();
        UserResponse player = new UserResponse();
        player.setRoomId(room.getRoomId());
        player.setPosition(room.getPositionById(u.getId()));
        player.setState(RoomService.getRoomUserState(room.getRoomId(), u.getId()));
        user.add(player);
        // 房间玩家放入房间对象
        roomResponse.setId(room.getRoomId());
        roomResponse.setState(room.getStatus());
        roomResponse.setPlayer(user);
        roomResponse.setBanker(room.getBanker());
        return roomResponse;
    }

    @Override
    public RoomResponse outRoom(long playerId, long roomId) throws Exception {
        Room room = roomDao.getRoomById(roomId);
        // 检验房间的存在
        if (room == null && room.getTotalCount() - room.getOpenCount() > 0) {
            RoomService.delRoom(playerId);
            throw new ErrorCodeException(ResultCode.ROOM_UNDEFIND);
        }
        List<Long> userIds = RoomService.getRoomUserIds(roomId);
        List<User> users = Lists.newArrayList();
        RoomInf roomInf = null;
        if (room.getAdminId() == playerId) {
            room.setState(4);
            userDao.clearRoomIdByIds(userIds);
            roomDao.updateRoom(room);
            RoomService.delRoom(roomId);
        } else {
            if (RoomService.removePlayer(roomId, playerId)) {
                userDao.clearRoomIdByIds(Lists.newArrayList(playerId));
            }
            List<Long> sessionUserIds = RoomService.getRoomUserIds(roomId);
            if (!CollectionUtils.isEmpty(sessionUserIds)) {
                users.addAll(userDao.getUserByIds(sessionUserIds));
            }
            roomInf = RoomService.getRoom(roomId);
        }
        return getRoomUserIfo(room, users, roomInf);
    }

    /**
     * 洗牌 发牌
     */
    @Override
    public List<RoomOperationInfoResponse> shuffle1(RoomInf roomInf) {
        LinkedList<PlayerST> playerSTs = roomInf.getPlayers();
        //返回发牌数据的list
        List<RoomOperationInfoResponse> roomOperationInfoResponses = Lists.newArrayList();
        List<Integer> all = new ArrayList<Integer>();
        Queue<Integer> allQueue = new LinkedList<Integer>();
        Map<Integer, Map<Integer, List<Integer>>> cardMap = new HashMap<Integer, Map<Integer, List<Integer>>>();
        allQueue = Mahjong.shuffle(1);// 洗牌 存入all中
        Map<Integer, List<Integer>> positionAndCards = Maps.newHashMap();
        for (int j = 0; j < playerSTs.size(); j++) {
            PlayerST playerST = playerSTs.get(j);
            List<Integer> list = new ArrayList<Integer>();
            for (int i = 0; i < 13; i++) {
                list.add(allQueue.poll());
            }
            positionAndCards.put(playerST.getPosition(), list);

            Card card = new Card(CardType.SHOUPAI.getType(), list, list.size());
            List<Card> cards = Lists.newArrayList(card);
            playerSTs.get(j).setCards(cards);
        }
        roomOperationInfoResponses = RoomOperationResponseUntils.returnShuffe(allQueue, positionAndCards, playerSTs);
        roomInf.setLeftDeskCards(allQueue);
        roomInf.setPlayers(playerSTs);
        roomInf.setCurrent(roomInf.getBanker());
        roomInf.setLeftDeskCardNum(allQueue.size());
        //设置记录
//        List<Operation> history = Lists.newArrayList();
        Operation op = new Operation(OperationType.FAPAI.getType(), roomInf.getBanker());
        List<Card> cards = op.getCards();
        for (int i = 0; i < roomInf.getPlayerNum(); i++) {
            cards.add(new Card(CardType.IN.getType(), positionAndCards.get(i)));
        }
//        history.add(op);
//        roomInf.setHistory(history);
        OperationService.addOperation(roomInf.getRoomId(),roomInf.getCurrentCount(),op);
        roomInf.setHistory(op);
        RoomService.setRoom(roomInf);
        return roomOperationInfoResponses;
    }

    @Override
    /**
     * 抓牌
     */
    public List<RoomOperationInfoResponse> deal1(RoomInf roomInf) {
        List<RoomOperationInfoResponse> roomOperationInfoResponses = null;
        //所以玩家
        List<PlayerST> playerSTs = roomInf.getPlayers();
        //抓牌玩家的位置
        int position = roomInf.getCurrent();
        //剩余的牌
        Queue<Integer> allQueue = roomInf.getLeftDeskCards();
        //发出的牌
        int card = allQueue.poll();
        //抓牌的玩家
        PlayerST currentPlayer = roomInf.getPlayerByPosition(position);
        //改变抓牌玩家的手牌
        currentPlayer.addCard(Lists.<Integer>newArrayList(card), CardType.SHOUPAI.getType());
        //改变抓入的牌
        currentPlayer.addCard(Lists.<Integer>newArrayList(card), CardType.IN.getType());
        roomOperationInfoResponses = RoomOperationResponseUntils.returnDeal(allQueue, Lists.<Integer>newArrayList(card), playerSTs, currentPlayer);
        roomInf.setOption(CardCmd.CHUPAI);
        //设置记录
//        List<Operation> history = roomInf.getHistory();
        Operation op = new Operation(OperationType.IN.getType(), position, CardType.IN.getType(), card);
//        history.add(op);
        OperationService.addOperation(roomInf.getRoomId(),roomInf.getCurrentCount(),op);
        save(roomInf);
        logger.info("起牌:" + roomInf.getPlayerByPosition(roomInf.getCurrent()));
        return roomOperationInfoResponses;
    }

    @Override
    /***
     * 出牌
     *
     * @param roomInf
     * @param cardNum
     * @return
     */
    public List<RoomOperationInfoResponse> outHand1(RoomInf roomInf, int cardNum) {
        List<RoomOperationInfoResponse> roomOperationInfoResponses = null;
        //所以玩家
        List<PlayerST> playerSTs = roomInf.getPlayers();
        //打牌玩家的位置
        int position = roomInf.getCurrent();
        //打牌的玩家
        PlayerST currentPlayer = roomInf.getPlayerByPosition(position);
        //删除该玩家的手牌
        currentPlayer.deleteCard(cardNum, CardType.SHOUPAI.getType());
        //把牌添加到已出的牌里面
        currentPlayer.addCard(cardNum, CardType.YICHUPAI.getType());
        currentPlayer.addCard(cardNum, CardType.OUT.getType());
        roomOperationInfoResponses = RoomOperationResponseUntils.returnOutHand(roomInf.getLeftDeskCards(), Lists.newArrayList(cardNum), playerSTs, currentPlayer);
        Integer s = null;
        if (currentPlayer.getStatus() == PlayerType.TING.getType() && currentPlayer.getCircleAfterTing() != null) {
            s = currentPlayer.getCircleAfterTing() == 0 ? currentPlayer.getCircleAfterTing() + 1 : null;
        }
        currentPlayer.setCircleAfterTing(s);
        roomInf.nextOne();
        roomInf.setOption(CardCmd.ZHUAPAI);
        //设置记录
//        List<Operation> history = roomInf.getHistory();
        Operation op = new Operation(OperationType.OUTHAND.getType(), position, CardType.YICHUPAI.getType(), cardNum);
//        history.add(op);
        OperationService.addOperation(roomInf.getRoomId(),roomInf.getCurrentCount(),op);
        roomInf.setHistory(op);
        save(roomInf);
        logger.info("打牌:" + roomInf.getPlayerByPosition(roomInf.returnBeforePositon()));
        return roomOperationInfoResponses;


    }

    @Override
    /**
     * 判断是否能吃
     *
     * @param
     * @param prePosition
     * @return
     */
    public Tip isChi(Long roomId, int prePosition) {
        RoomInf roomInf = RoomService.getRoom(roomId);
        Tip tip = null;
        int current = roomInf.getCurrent();
        PlayerST playerST = roomInf.getPlayerByPosition(current);//根据位置 得到玩家信息
        List<List<Integer>> chiList = Mahjong.isChi(roomInf, prePosition, playerST);
        if (chiList != null && chiList.size() > 0) {
            Map<Integer, Integer> map = new HashMap<Integer, Integer>();
            map.put(playerST.getPosition(), TipType.CANCHIPAI.getType());
            tip = createTipUtils.createTip(playerST, map, chiList, roomInf.returnBeforePositon(), TipType.CANCHIPAI.getType());
        }
        //被吃的玩家
        PlayerST prePlayerST = roomInf.getPlayerByPosition(prePosition);
        //被吃的牌
        int cardNum = prePlayerST.getCardsByType(CardType.YICHUPAI.getType()).get(prePlayerST.getCardsByType(CardType.YICHUPAI.getType()).size() - 1);
        if (tip != null) {
            Map<Integer, List<Integer>> map = Maps.newHashMap();
            //被吃的牌
            map.put(CardType.BECHI.getType(), Lists.<Integer>newArrayList(cardNum));
            TipInfo tipInfo = TipInfoUtils.createTipInfo(prePlayerST, map);
            tip.getTipInfos().add(tipInfo);
        }
        return tip;
    }

    /**
     * @param roomInf
     * @param cards       传上来 两张
     * @param prePosition
     * @return
     */
    @Override
    public List<RoomOperationInfoResponse> chi1(RoomInf roomInf, List<Integer> cards, int prePosition) {
        List<RoomOperationInfoResponse> roomOperationInfoResponses = null;
        //被吃的玩家
        PlayerST prePlayerST = roomInf.getPlayerByPosition(prePosition);
        //得到被吃的那张牌
        List<Integer> yiChuPai = prePlayerST.getCardsByType(CardType.YICHUPAI.getType());
        int cardNum = yiChuPai.get(yiChuPai.size() - 1);
        //在上家已打的牌中删除此牌
        prePlayerST.deleteCard(cardNum, CardType.YICHUPAI.getType());
        //当前玩家
        PlayerST currentPlayerST = roomInf.getPlayerByPosition(roomInf.getCurrent());
        //从手牌中删除 两个牌
        currentPlayerST.deleteCard(cards.get(0), CardType.SHOUPAI.getType());
        currentPlayerST.deleteCard(cards.get(1), CardType.SHOUPAI.getType());

        //吃后的组合
        List<Integer> chiList = new ArrayList<Integer>();
        chiList.addAll(cards);
        chiList.add(cardNum);
        //把吃的牌  加入到吃的 数组中
        currentPlayerST.addCard(chiList, CardType.CHIPAI.getType());

        //出牌玩家 出的牌
        List<Integer> outCardList = new ArrayList<Integer>();
        outCardList.add(cardNum);
        Map<Integer, List<Integer>> preTypeMap = new HashMap<Integer, List<Integer>>();//出牌的类型和牌
        Map<Integer, List<Integer>> currentTypeMap = new HashMap<Integer, List<Integer>>();//吃牌的类型和牌
        Map<Integer, Map<Integer, List<Integer>>> map = new HashMap<Integer, Map<Integer, List<Integer>>>();//用来构造返回的
        preTypeMap.put(CardType.BECHI.getType(), outCardList);
        currentTypeMap.put(CardType.CHIPAI.getType(), chiList);
        map.put(prePosition, preTypeMap);
        map.put(roomInf.getCurrent(), currentTypeMap);
        roomOperationInfoResponses = RoomOperationResponseUntils.returnRoomOperationInfoResponses(roomInf, map);
        roomInf.setTips(null);
        roomInf.setTempTips(null);
        roomInf.setOption(CardCmd.CHI);
        //设置记录
//        List<Operation> history = roomInf.getHistory();
        Operation op = new Operation(OperationType.CHI.getType(), roomInf.getCurrent(), CardType.CHIPAI.getType(), cards);
        op.getCards().add(new Card(CardType.YICHUPAI.getType(), cardNum));
//        history.add(op);
        OperationService.addOperation(roomInf.getRoomId(),roomInf.getCurrentCount(),op);
        roomInf.setHistory(op);
        save(roomInf);
        logger.info("被吃牌:" + roomInf.getPlayerByPosition(prePosition));
        logger.info("吃牌:" + roomInf.getPlayerByPosition(roomInf.getCurrent()));
        return roomOperationInfoResponses;
    }

    /**
     * 是否暗杠
     *
     * @param
     * @param cardNum
     * @param position 挖牌的位置
     * @return
     */
    @Override
    public Tip isAnGang(Long roomId, int cardNum, int position) {
        RoomInf roomInf = RoomService.getRoom(roomId);
        Tip tip = null;
        PlayerST playerST = roomInf.getPlayerByPosition(position);

        List<Integer> isAnGang = Mahjong.isAnGang(playerST, cardNum);
        if (!CollectionUtils.isEmpty(isAnGang)) {
            Map<Integer, Map<Integer, List<Integer>>> map = new HashMap<Integer, Map<Integer, List<Integer>>>();
            Map<Integer, List<Integer>> anGangMap = new HashMap<Integer, List<Integer>>();
            anGangMap.put(TipType.CANANGANG.getType(), isAnGang);
            map.put(playerST.getPosition(), anGangMap);
            tip = createTipUtils.createTip(playerST, map, position, TipType.CANANGANG.getType());
        }
        return tip;
    }

    @Override
    public List<Tip> isHu(Long roomId, int cardNum, int cardPosition) {
        RoomInf roomInf = RoomService.getRoom(roomId);
        List<Tip> tips = new ArrayList<Tip>();
        List<PlayerST> huPlayers = new ArrayList<PlayerST>();
        if (cardPosition == roomInf.getCurrent()) {
            //自摸的时候
            PlayerST playerST = roomInf.getPlayerByPosition(roomInf.getCurrent());
            List<Integer> huType = HuUtils.getHuType(null, playerST.getPlayerId(), roomId);
            if (huType.size() == 0)
                return tips;
            HuInfo huInfo = new HuInfo(huType, cardNum);
            playerST.setHuInfo(huInfo);
            huPlayers.add(playerST);
        } else {
            //财神过
            if (cardNum == roomInf.getGodWealth())
                return tips;
            //点炮
            huPlayers = isDianPao(roomInf, cardNum, roomInf.getPlayerByPosition(cardPosition).getPlayerId(), cardPosition);
        }
        if (huPlayers != null && huPlayers.size() > 0) {
            RoomService.setRoom(roomInf);
            tips = getHuTip(huPlayers, cardNum, cardPosition);
        }
        return tips;
    }

    public List<Tip> isHu(Long roomId, int cardPosition) {
        RoomInf roomInf = RoomService.getRoom(roomId);
        List<Tip> tips = new ArrayList<Tip>();
        List<PlayerST> huPlayers = new ArrayList<PlayerST>();
        for(PlayerST playerST:roomInf.getPlayers()){
            if (playerST.getPosition()==roomInf.getCurrent())
                continue;
            List<Integer> huType = HuUtils.getHuType(null, playerST.getPlayerId(), roomId);
            if (huType.size() == 0)
                return tips;
            HuInfo huInfo = new HuInfo(huType, roomInf.getGodWealth());
            playerST.setHuInfo(huInfo);
            huPlayers.add(playerST);
        }
        if (huPlayers != null && huPlayers.size() > 0) {
            RoomService.setRoom(roomInf);
            tips = getHuTip(huPlayers, roomInf.getGodWealth(), cardPosition);
        }
        return tips;
    }

    @Override
    public List<Tip> isQiangGang(Long roomId, int cardNum) {
        RoomInf roomInf = RoomService.getRoom(roomId);
        int gangPosition = roomInf.getCurrent();
        List<PlayerST> huPlayers = isDianPao(roomInf, cardNum, roomInf.getPlayerByPosition(gangPosition).getPlayerId(), gangPosition);
        List<Tip> tips = new ArrayList<Tip>();
        if (huPlayers != null && huPlayers.size() > 0) {
            //抢杠
            for (PlayerST playerST : huPlayers) {
                HuInfo huInfo = playerST.getHuInfo();
                huInfo.getHuCards().add(HuType.QIANGGANGHU.getType());
                //抢杠胡算自摸
                huInfo.setPao(null);
            }
            RoomService.setRoom(roomInf);
            tips = getHuTip(huPlayers, cardNum, roomInf.getCurrent());
        }
        return tips;
    }

    private List<PlayerST> isDianPao(RoomInf roomInf, int cardNum, long playerId, int cardPosition) {
        List<PlayerST> huPlayers = new ArrayList<PlayerST>();
        for (PlayerST playerST : roomInf.getPlayers()) {
            if (playerST.getPosition() == cardPosition)
                continue;
            List<Integer> huType = HuUtils.getHuType(cardNum, playerST.getPlayerId(), roomInf.getRoomId());
            if (huType.size() == 0)
                continue;
            HuInfo huInfo = new HuInfo(huType, cardNum, playerId);
            playerST.setHuInfo(huInfo);
            huPlayers.add(playerST);
        }
        return huPlayers;
    }

    private List<Tip> getHuTip(List<PlayerST> huPlayers, int cardNum, int cardPosition) {
        List<Tip> tips = new ArrayList<Tip>();
        for (int i = 0; i < huPlayers.size(); i++) {
            Map<Integer, Map<Integer, List<Integer>>> map = new HashMap<Integer, Map<Integer, List<Integer>>>();
            Map<Integer, List<Integer>> huMap = new HashMap<Integer, List<Integer>>();
            List<Integer> huList = new ArrayList<Integer>();
            huList.add(cardNum);
            huMap.put(CardType.BEHU.getType(), huList);
            map.put(cardPosition, huMap);
            Tip tip = createTipUtils.createTip(huPlayers.get(i), map, cardPosition, TipType.CANHU.getType());
            tips.add(tip);
        }
        return tips;
    }


    @Override
    public Tip isTing(Long roomId, int positin) {
        RoomInf roomInf = RoomService.getRoom(roomId);
        Tip tip = null;
        List<Integer> cards = new ArrayList<Integer>();
        List<Integer> shouPai = new ArrayList<Integer>();
        PlayerST playerST = roomInf.getPlayerByPosition(positin);
        shouPai = playerST.getCardsByType(CardType.SHOUPAI.getType());
        cards.addAll(shouPai);
        List<Integer> outCards = new ArrayList<Integer>();

        String[] rules = roomInf.getRule();
        outCards = Mahjong.isTing(cards, playerST, rules);
        if (!CollectionUtils.isEmpty(outCards)) {
            Map<Integer, List<Integer>> map = Maps.newHashMap();
            map.put(TipType.OUTOFTING.getType(), outCards);
            tip = createTipUtils.getTingTip(playerST, map, TipType.CANTING.getType());
        }
        save(roomInf);
        return tip;
    }

    /**
     * @param roomInf
     * @param prePositin// 因为已出牌  听牌人的位置是前一个
     * @return
     */
    @Override
    public List<Tip> ting(RoomInf roomInf, int prePositin, int outCard) {
        List<Tip> tips = new ArrayList<Tip>();
        PlayerST playerST = roomInf.getPlayerByPosition(prePositin);
        /**
         * 把赢的牌保存起来
         *
         */
        Map<Integer, HuInfo> tempHuInfo = playerST.getTempHuInfo();
        if (!CollectionUtils.isEmpty(tempHuInfo)) {
            HuInfo huInfo = tempHuInfo.get(outCard);
            if (huInfo == null || CollectionUtils.isEmpty(huInfo.getHuCards())) {
                throw new ErrorCodeException(ResultCode.UNTING);
            }
            playerST.setHuInfo(huInfo);
        }
        playerST.setCircleAfterTing(0);
        //改变玩家的状态
        playerST.setStatus(PlayerType.TING.getType());
        tips = createTipUtils.createTips(roomInf, playerST, TipType.TING.getType());
        roomInf.setTips(null);
        roomInf.setTempTips(null);

        // 保存宝牌
        if (roomInf.getBaoCard() == null || roomInf.getBaoCard() == 0) {
            int bao = roomInf.getLeftDeskCards().poll();
            roomInf.setBaoCard(bao);
        }
        save(roomInf);
        logger.info("听牌:" + playerST);
        return tips;
    }

    @Override
    /**
     * @param prePosition 被吃听人的位置
     * @return
     */
    public List<Tip> isChiTing(Long roomId, int prePosition) {
        RoomInf roomInf = RoomService.getRoom(roomId);
        List<Tip> tips = new ArrayList<Tip>();
        //被吃的玩家
        PlayerST prePlayerST = roomInf.getPlayerByPosition(prePosition);
        List<Integer> outCardsOfPreplayer = prePlayerST.getCardsByType(CardType.YICHUPAI.getType());
        int cardNum = outCardsOfPreplayer.get(outCardsOfPreplayer.size() - 1);
        Map<Integer, List<List<Card>>> map = Mahjong.isChiTing(roomInf.getPlayers(), cardNum, prePosition, roomInf.getRule());
        if (!CollectionUtils.isEmpty(map)) {
            tips = createTipUtils.createTip(roomInf, map, TipType.CANCHITING.getType());
        }
        //被吃的牌
        Map<Integer, List<Integer>> typeAndCardsMap = Maps.newHashMap();
        //被吃的牌
        typeAndCardsMap.put(CardType.BECHI.getType(), Lists.<Integer>newArrayList(cardNum));
        //被吃的玩家和被吃牌的tipInfo
        TipInfo tipInfo = TipInfoUtils.createTipInfo(prePlayerST, typeAndCardsMap);
        //给每个要吃听的玩家 加上要吃的那张牌的信息
        if (!CollectionUtils.isEmpty(tips)) {
            for (Tip tip : tips) {
                tip.getTipInfos().add(tipInfo);
            }
        }
        save(roomInf);
        return tips;
    }

    @Override
    public List<Tip> chiTing(RoomInf roomInf, List<Integer> cards, int currentPosition, int prePosition, int outCard) {
        List<Tip> tips = Lists.newArrayList();
        //被吃的玩家 和被吃的牌
        PlayerST playerST = roomInf.getPlayerByPosition(prePosition);
        List<Integer> outCardsOfPreplayer = playerST.getCardsByType(CardType.YICHUPAI.getType());
        int cardNum = outCardsOfPreplayer.get(outCardsOfPreplayer.size() - 1);
        //把出的牌从被吃玩家的  出牌数组中删除
        playerST.deleteCard(cardNum, CardType.YICHUPAI.getType());
        //吃听的玩家
        PlayerST currentPlayer = roomInf.getPlayerByPosition(currentPosition);

        /**
         * 判断赢的牌 并保存
         */
        currentPlayer.setStatus(PlayerType.TING.getType());
        List<ChiOrPengTingInfo> chiOrPengTingInfos = currentPlayer.getChiOrPengTingInfos();
        if (CollectionUtils.isEmpty(chiOrPengTingInfos)) {
            throw new ErrorCodeException(ResultCode.UNTING);
        }
        for (ChiOrPengTingInfo chiOrPengTingInfo : chiOrPengTingInfos) {
            if (!chiOrPengTingInfo.isEqual(cards)) {
                continue;
            }
//            for(OutAndWin w:chiOrPengTingInfo.getOutAndWins()){
//                if(w.getOutCard()==outCard){
//                    currentPlayer.setHuCard(w.getWinCards());
//                    break;
//                }
//            }
            for (OutAndWin w : chiOrPengTingInfo.getOutAndWins()) {
                if (w.getOutCard() == outCard) {
                    currentPlayer.setHuInfo(w.getHuInfo());
                    break;
                }
            }
        }
        if (currentPlayer.getHuInfo() == null || CollectionUtils.isEmpty(currentPlayer.getHuInfo().getHuCards())) {
            throw new ErrorCodeException(ResultCode.UNTING);
        }
//        if (CollectionUtils.isEmpty(currentPlayer.getHuCard())) {
//            throw new ErrorCodeException(ResultCode.UNTING);
//        }
        for (int i = 0; i < cards.size(); i++) {
            currentPlayer.deleteCard(cards.get(i), CardType.SHOUPAI.getType());//删除手中吃的牌
        }
        cards.add(cardNum);
        currentPlayer.addCard(cards, CardType.CHIPAI.getType());//把 吃牌加到吃牌玩家的 吃牌数组
        currentPlayer.setCircleAfterTing(0);//设置听后打牌几次  用就是判定是不是听当圈
        currentPlayer.setStatus(PlayerType.TING.getType());//保存玩家听牌状态
        List<Integer> beiChi = Lists.newArrayList(cardNum);//被吃的牌的list

        TipInfo tipInfo1 = new TipInfo(currentPlayer, PlayerType.TING.getType(), CardUntils.creatCardList(cards, CardType.CHIPAI.getType()));
        TipInfo tipInfo2 = new TipInfo(playerST, PlayerType.BASE.getType(), CardUntils.creatCardList(beiChi, CardType.BECHI.getType()));
        List<TipInfo> tipInfos = Lists.newArrayList(tipInfo1, tipInfo2);

        tips = createTipUtils.getChiOrPengTingResult(roomInf, tipInfos);

        roomInf.setCurrent(currentPosition);
        roomInf.setTips(null);
        roomInf.setTempTips(null);
        currentPlayer.setChiOrPengTingInfos(null);
        //保存宝牌
        if (roomInf.getBaoCard() == null || roomInf.getBaoCard() == 0) {
            int bao = roomInf.getLeftDeskCards().poll();
            roomInf.setBaoCard(bao);
        }
        save(roomInf);
        logger.info("被吃听牌:" + roomInf.getPlayerByPosition(prePosition));
        logger.info("吃听牌:" + roomInf.getPlayerByPosition(roomInf.getCurrent()));
        return tips;
    }

    @Override
    public List<Tip> isPengTing(Long roomId, int prePosition) {
        RoomInf roomInf = RoomService.getRoom(roomId);
        List<Tip> tip = new ArrayList<Tip>();
        PlayerST playerST = roomInf.getPlayerByPosition(prePosition);
        List<Integer> outCardsOfPreplayer = playerST.getCardsByType(CardType.YICHUPAI.getType());
        int cardNum = outCardsOfPreplayer.get(outCardsOfPreplayer.size() - 1);

        Map<Integer, List<List<Card>>> map = Mahjong.isPengTing(roomInf.getPlayers(), cardNum, prePosition, roomInf.getRule());
        if (!CollectionUtils.isEmpty(map)) {
            tip = createTipUtils.createTip(roomInf, map, TipType.CANPENGTING.getType());
        }
        save(roomInf);
        return tip;
    }

    @Override
    public List<Tip> pengTing(RoomInf roomInf, List<Integer> cards, int currentPosition, int prePosition, int outCard) {
        List<Tip> tips = null;
        //被碰的玩家 和被碰的牌
        PlayerST playerST = roomInf.getPlayerByPosition(prePosition);
        List<Integer> outCardsOfPreplayer = playerST.getCardsByType(CardType.YICHUPAI.getType());
        int pengPai = outCardsOfPreplayer.get(outCardsOfPreplayer.size() - 1);
        //把出的牌从被吃玩家的  出牌数组中删除
        playerST.deleteCard(pengPai, CardType.YICHUPAI.getType());
        //碰听的玩家
        PlayerST currentPlayer = roomInf.getPlayerByPosition(currentPosition);
        /**
         * 判断赢的牌 并保存
         */
        currentPlayer.setStatus(PlayerType.TING.getType());
        List<ChiOrPengTingInfo> chiOrPengTingInfos = currentPlayer.getChiOrPengTingInfos();
        if (CollectionUtils.isEmpty(chiOrPengTingInfos)) {
            throw new ErrorCodeException(ResultCode.UNTING);
        }
        for (ChiOrPengTingInfo chiOrPengTingInfo : chiOrPengTingInfos) {
            if (!chiOrPengTingInfo.isEqual(cards)) {
                continue;
            }
            for (OutAndWin w : chiOrPengTingInfo.getOutAndWins()) {
                if (w.getOutCard() == outCard) {
                    currentPlayer.setHuInfo(w.getHuInfo());
                    break;
                }
            }
//            for(OutAndWin w:chiOrPengTingInfo.getOutAndWins()){
//                if(w.getOutCard()==outCard){
//                    currentPlayer.setHuCard(w.getWinCards());
//                    break;
//                }
//            }
        }
//        if (CollectionUtils.isEmpty(currentPlayer.getHuCard())) {
//            throw new ErrorCodeException(ResultCode.UNTING);
//        }

        if (currentPlayer.getHuInfo() == null || CollectionUtils.isEmpty(currentPlayer.getHuInfo().getHuCards())) {
            throw new ErrorCodeException(ResultCode.UNTING);
        }
        for (int i = 0; i < cards.size(); i++) {
            currentPlayer.deleteCard(cards.get(i), CardType.SHOUPAI.getType());//删除手中吃的牌
        }
        cards.add(pengPai);
        currentPlayer.addCard(cards, CardType.PENGPAI.getType());//把 peng牌加到玩家的 碰牌数组
        currentPlayer.setStatus(PlayerType.TING.getType());//保存玩家听牌状态
        currentPlayer.setCircleAfterTing(0);//设置听后打牌几次  用就是判定是不是听当圈
        List<Integer> beiChi = Lists.newArrayList(pengPai);//被碰的牌的list

        TipInfo tipInfo1 = new TipInfo(currentPlayer, PlayerType.TING.getType(), CardUntils.creatCardList(cards, CardType.PENGPAI.getType()));
        TipInfo tipInfo2 = new TipInfo(playerST, PlayerType.BASE.getType(), CardUntils.creatCardList(beiChi, CardType.BEPENG.getType()));
        List<TipInfo> tipInfos = Lists.newArrayList(tipInfo1, tipInfo2);

        tips = createTipUtils.getChiOrPengTingResult(roomInf, tipInfos);


        currentPlayer.setStatus(PlayerType.TING.getType());
        roomInf.setCurrent(currentPosition);
        roomInf.setTips(null);
        roomInf.setTempTips(null);
        currentPlayer.setChiOrPengTingInfos(null);
        //保存宝牌
        if (roomInf.getBaoCard() == null || roomInf.getBaoCard() == 0) {
            int bao = roomInf.getLeftDeskCards().poll();
            roomInf.setBaoCard(bao);
        }
        save(roomInf);
        logger.info("被吃听牌:" + roomInf.getPlayerByPosition(prePosition));
        logger.info("吃听牌:" + roomInf.getPlayerByPosition(roomInf.getCurrent()));
        return tips;
    }

    @Override
    public List<Tip> hu(RoomInf roomInf, int cardNum, int position) {
        PlayerST playerST = roomInf.getPlayerByPosition(position);
        playerST.addCard(cardNum, CardType.BEHU.getType());
        roomInf.setWin(playerST.getPlayerId());
        playerST.setStatus(PlayerType.HU.getType());
        roomInf.setTips(null);
        roomInf.setTempTips(null);
        //设置胡的那张牌
        //自摸的情况下 删除手里摸到的那张牌
        List<Integer> shouPai = playerST.getCardsByType(CardType.SHOUPAI.getType());
        int inCard = shouPai.get(shouPai.size() - 1);
        HuInfo huInfo = playerST.getHuInfo();
        if (huInfo != null && huInfo.getPao() != null)
            roomInf.setPao(huInfo.getPao());
        else if (!huInfo.getHuCards().contains(HuType.QIANGGANGHU.getType()))
            playerST.deleteCard(inCard, CardType.SHOUPAI.getType());
        roomInf.setHuCard(cardNum);
        //设置站立  非站立
//        calculateStand(roomInf);
        save(roomInf);
        return null;
    }

    @Override
    public List<Tip> over(long roomId) {
        RoomInf roomInf = RoomService.getRoom(roomId);
        List<Tip> tips = new ArrayList<Tip>();
        tips = createTipUtils.creatTips(roomInf);
        clearRoomInfo(roomInf);
        return tips;
    }

    @Override
    public RoomInf returnRoomInf(long roomId, Long playerId) {
        RoomInf roomInf = RoomService.getRoom(roomId);
        if (roomInf == null) {
            PlayerST playerST = roomInf.getPlayerByPosition(roomInf.getPositionById(playerId));
            playerST.setOnline(true);
            RoomService.setRoom(roomInf);
//        if (playerST.getStatus() != PlayerType.TING.getType())
//            roomInf.setBaoCard(null);
//        roomInf.setLeftDeskCards(null);
            if (roomInf.getStatus() != 0)
                for (PlayerST playerST1 : roomInf.getPlayers()) {
                    playerST1.setCircleAfterTing(null);
                    playerST1.setChiOrPengTingInfos(null);
                    playerST1.setHuInfo(null);
                    playerST1.setTempHuInfo(null);
                    if (playerST1.getPlayerId() != playerId) {
                        playerST1.updateCards(null, CardType.SHOUPAI.getType());
                    }
                }
        }
        return roomInf;
    }

    @Override
    public List<HistoryRecord> getHistoryRecord(long playerId) {
//        List<Record> list = recordDao.getLimitRoomRecordByUserId(playerId, 1, 20);
        Calendar calendar = Calendar.getInstance();
        Date time = new Date(System.currentTimeMillis());
        calendar.setTime(time);
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        time = calendar.getTime();
        List<Record> list = recordDao.getRoomRecordByUserIdAndTime(playerId,1,time);
        List<HistoryRecord> recordResponses = Lists.newArrayList();
        if (list == null || list.size() < 0)
            return recordResponses;
        for (Record r : list) {
            HistoryRecord historyRecord = new HistoryRecord(r);
            List<UserRecord> userRecords = Lists.newArrayList();
            List<Record> records = recordDao.getRecordByRoomId(r.getRoomId(), 1);
            for (Record record : records) {
                UserRecord userRecord = new UserRecord(record);
                userRecords.add(userRecord);
            }
            historyRecord.setUserRecords(userRecords);
            recordResponses.add(historyRecord);
        }
        return recordResponses;
    }

    @Override
    public HistoryRecord getRecord(long playerId, long roomId) {
        //总记录
        List<Record> list = recordDao.getRecordByRoomId(roomId, 1);
        HistoryRecord historyRecord = new HistoryRecord(list.get(0));
        List<UserRecord> userRecords = Lists.newArrayList();
        for (Record record : list) {
            UserRecord userRecord = new UserRecord(record);
            userRecords.add(userRecord);
        }
        historyRecord.setUserRecords(userRecords);
        //分积分
        List<List<Integer>> points = Lists.newArrayList();
        for (UserRecord u : userRecords) {
            List<Record> records = recordDao.getRoomRecordByUserId(u.getUserId(), roomId, 2);
            List<Integer> point = Lists.newArrayList();
            for (Record record : records) {
                point.add(record.getPoint());
            }
            points.add(point);
        }
        historyRecord.setPoints(points);
        return historyRecord;
    }

    @Override
    public int getPermission(long playerId){
        Permission permission = permissionDao.getPermissionByUserId(playerId);
        if (permission!=null)
            return permission.getPermissionNum();
        else
            return 0;
    }

    @Override
    public void distance(long roomId, long playerId) {
        RoomInf roomInf=RoomService.getRoom(roomId);
        List<User> users=Lists.newArrayList();
        for(PlayerST playerST:roomInf.getPlayers()){
            users.add(userDao.getUserById(playerST.getPlayerId()));
        }
        List<List<Long>> nears=Lists.newArrayList();
        for(int i=0;i<users.size();i++){
            List<Long> near=Lists.newArrayList();
            for(int j=i+1;j<users.size();j++){
                String ip1=users.get(i).getLoginIp();
                String ip2=users.get(j).getLoginIp();
                if(ip1!=null&&ip2!=null&&ip1.equals(ip2)){
                    near.add(users.get(j).getId());
                    if (!near.contains(users.get(i).getId()))
                        near.add(users.get(i).getId());
                }
            }
            if (near.size()>0)
                nears.add(near);
        }
        if (nears.size()>0){
            boolean sendAll=false;
            for (List<Long> n:nears){
                if (n.contains(playerId))
                    sendAll=true;
            }
            if (sendAll){
                List<Long> receiver = new ArrayList<Long>();
                for (PlayerST s : RoomService.getRoomInf(roomId)) {
                    receiver.add(s.getPlayerId());
                }
                MQProductor.build().send(Result.SUCCESS(null, Request.valueOf(ModuleId.PLAYER, PlayerCmd.DISTANCE,null), receiver), JmsTypeEnum.RADIATE);
            }
            else
                MQProductor.build().send(Result.SUCCESS(null, Request.valueOf(ModuleId.PLAYER, PlayerCmd.DISTANCE,null), playerId), JmsTypeEnum.RADIATE);
        }
    }

    @Override
    public int activity(long playerId, int type) {
        Activity activity=new Activity();
        activity.setUserId(playerId);
        activity.setType(type);
        activity.setCreateTime(new Date());
        if (type==1){
            int random=new Random().nextInt(100);
            if (0<=random && random<80)
                activity.setCardNum(1);
            else if (80<=random && random<95)
                activity.setCardNum(3);
            else
                activity.setCardNum(5);
        }else
            activity.setCardNum(1);
        activity.setType(type);
        activityDao.createActivity(activity);
        Permission permission=permissionDao.getPermissionByUserId(playerId);
        permission.setPermissionNum(permission.getPermissionNum()+activity.getCardNum());
        permissionDao.updateRoom(permission);
        //房卡变化记录
        CardRecord cardRecord=new CardRecord();
        cardRecord.setCreateTime(new Date());
        cardRecord.setPlayerId(playerId);
        cardRecord.setType(CardChangeType.HUO.getType());
        cardRecord.setCardNum(permission.getPermissionNum());
        cardRecord.setChangeNum(activity.getCardNum());
        if (type==1)
            cardRecord.setText("每日抽奖");
        else cardRecord.setText("每日分享");
        cardRecordDao.createUser(cardRecord);
        return activity.getCardNum();
    }

    private void clearRoomInfo(RoomInf roomInf) {
        roomInf.setLeftDeskCardNum(0);
        roomInf.setLeftDeskCards(null);
        roomInf.setStatus(0);
        roomInf.setDiscard(35);//流局牌数
        roomInf.setTips(null);
        roomInf.setPao(null);
        roomInf.setBaoCard(null);
        roomInf.setHuCard(null);
        for (int i = 0; i < roomInf.getPlayers().size(); i++) {
            roomInf.getPlayers().get(i).setState(false);
            roomInf.getPlayers().get(i).setCards(null);
            roomInf.getPlayers().get(i).setChiOrPengTingInfos(null);
            roomInf.getPlayers().get(i).setHuInfo(null);
            roomInf.getPlayers().get(i).setTempHuInfo(null);
            roomInf.getPlayers().get(i).setStatus(PlayerType.BASE.getType());
        }
        RoomService.setRoom(roomInf);
    }

    /**
     * 发给需要可以碰牌的玩家 让他判定
     *
     * @param cardNum
     * @param position
     * @return
     */
    public Tip isPeng(Long roomId, int cardNum, int position) {
        RoomInf roomInf = RoomService.getRoom(roomId);
        if (cardNum == roomInf.getGodWealth())//出财神不能碰
            return null;
        PlayerST playerInfo = Mahjong.isPeng(roomInf.getPlayers(), cardNum, position);
        // 产生返回方法时所需要的参数
        Tip tip = null;
        if (playerInfo != null) {
            Map<Integer, Map<Integer, List<Integer>>> map = new HashMap<Integer, Map<Integer, List<Integer>>>();
            Map<Integer, List<Integer>> pengMap = new HashMap<Integer, List<Integer>>();
            List<Integer> pengCardList = new ArrayList<Integer>();
            pengCardList.add(cardNum);
            pengCardList.add(cardNum);
            pengMap.put(TipType.CANPENGPAI.getType(), pengCardList);
            map.put(playerInfo.getPosition(), pengMap);
            tip = createTipUtils.createTip(playerInfo, map, roomInf.returnBeforePositon(), TipType.CANPENGPAI.getType());

        }
        return tip;
    }

    @Override
    public List<RoomOperationInfoResponse> peng1(RoomInf roomInf, int cardNum, int current, int prePosition) {
        List<RoomOperationInfoResponse> roomOperationInfoResponses = null;
        //被碰的玩家
        PlayerST prePlayerST = roomInf.getPlayerByPosition(prePosition);

        //在上家已打的牌中删除此牌
        prePlayerST.deleteCard(cardNum, CardType.YICHUPAI.getType());
        //被碰的牌
        List<Integer> outCardList = Lists.newArrayList(cardNum);
        //当前玩家
        PlayerST currentPlayerST = roomInf.getPlayerByPosition(current);
        //从手牌中删除 两个牌
        currentPlayerST.deleteCard(cardNum, CardType.SHOUPAI.getType());
        currentPlayerST.deleteCard(cardNum, CardType.SHOUPAI.getType());
        //碰的组合
        List<Integer> pengCardList = Lists.newArrayList(cardNum, cardNum, cardNum);
        //把碰牌加到碰牌数组中
        currentPlayerST.addCard(pengCardList, CardType.PENGPAI.getType());

        Map<Integer, Map<Integer, List<Integer>>> map = new HashMap<Integer, Map<Integer, List<Integer>>>();
        //被碰的玩家
        Map<Integer, List<Integer>> preTypeMap = new HashMap<Integer, List<Integer>>();
        preTypeMap.put(CardType.BEPENG.getType(), outCardList);
        map.put(prePosition, preTypeMap);// 被碰的类型
        //当前碰的玩家
        Map<Integer, List<Integer>> currentTypeMap = new HashMap<Integer, List<Integer>>();
        currentTypeMap.put(CardType.PENGPAI.getType(), pengCardList);
        map.put(current, currentTypeMap);// 碰的类型
        roomOperationInfoResponses = RoomOperationResponseUntils.returnRoomOperationInfoResponses(roomInf, map);


        roomInf.setCurrent(current);
        roomInf.setTips(null);
        roomInf.setTempTips(null);
        roomInf.setOption(CardCmd.PENG);
        //设置记录
//        List<Operation> history = roomInf.getHistory();
        Operation op = new Operation(OperationType.PENG.getType(), current, CardType.PENGPAI.getType(), cardNum);
//        history.add(op);
        OperationService.addOperation(roomInf.getRoomId(),roomInf.getCurrentCount(),op);
        roomInf.setHistory(op);
        save(roomInf);
        logger.info("被碰牌:" + roomInf.getPlayerByPosition(prePosition));
        logger.info("碰牌:" + roomInf.getPlayerByPosition(roomInf.getCurrent()));
        return roomOperationInfoResponses;
    }

    /**
     * 杠提示
     *
     * @param cardNum
     * @param position
     * @return
     */
    @Override
    public Tip isGang(Long roomId, int cardNum, int position) {
        RoomInf roomInf = RoomService.getRoom(roomId);
        PlayerST playerInfo = Mahjong.isMingGang(roomInf.getPlayers(), cardNum, position);
        // 产生返回方法时所需要的参数
        Tip tip = null;
        if (playerInfo != null) {
            Map<Integer, Map<Integer, List<Integer>>> map = new HashMap<Integer, Map<Integer, List<Integer>>>();
            Map<Integer, List<Integer>> cardMap = new HashMap<Integer, List<Integer>>();
            //杠的
            List<Integer> gangCardList = new ArrayList<Integer>();
            gangCardList.add(cardNum);
            gangCardList.add(cardNum);
            gangCardList.add(cardNum);
            cardMap.put(TipType.CANMINGGANG.getType(), gangCardList);

            map.put(playerInfo.getPosition(), cardMap);
            tip = createTipUtils.createTip(playerInfo, map, roomInf.returnBeforePositon(), TipType.CANMINGGANG.getType());
        }
        return tip;
    }

    @Override
    public Tip isMingGang2(Long roomId) {
        Tip tip = null;
        RoomInf roomInf = RoomService.getRoom(roomId);
        PlayerST currentPlayer = roomInf.getPlayerByPosition(roomInf.getCurrent());
        if (currentPlayer.getStatus() == PlayerType.TING.getType())
            return null;
        List<Integer> pengList = currentPlayer.getCardsByType(CardType.PENGPAI.getType());
        List<Integer> shouPai = currentPlayer.getCardsByType(CardType.SHOUPAI.getType());
        List<Integer> canMingGang = Lists.newArrayList();
        Set<Integer> pengCard = Sets.newHashSet(pengList);
        if (!CollectionUtils.isEmpty(pengList)) {
            for (Integer card : pengCard) {
                if (shouPai.contains(card)) {
                    canMingGang.add(card);
                }
            }
        }
        if (!CollectionUtils.isEmpty(canMingGang))
            tip = createTipUtils.createMingGangTip2(currentPlayer, canMingGang, TipType.CANMINGGANG2.getType());
        return tip;
    }

    @Override
    public List<RoomOperationInfoResponse> mingGang2(Long roomId, Integer cardNum) {
        List<RoomOperationInfoResponse> roomOperationInfoResponses = null;
//        List<Tip> tips = Lists.newArrayList();
        RoomInf roomInf = RoomService.getRoom(roomId);
        int position = roomInf.getCurrent();
        PlayerST currentPlayer = roomInf.getPlayerByPosition(position);
        //删除手中的牌
        currentPlayer.deleteCard(cardNum, CardType.SHOUPAI.getType());
        //删除碰牌
        currentPlayer.deleteCard(cardNum, CardType.PENGPAI.getType());
        currentPlayer.deleteCard(cardNum, CardType.PENGPAI.getType());
        currentPlayer.deleteCard(cardNum, CardType.PENGPAI.getType());

        //加入杠牌
        List<Integer> gangPai = Lists.newArrayList(cardNum, cardNum, cardNum, cardNum);
        currentPlayer.addCard(gangPai, CardType.MINGGANG.getType());

//        tips = CreateTipUtils.createMingGangResult2(roomInf, cardNum, CardType.BEMINGGANG2.getType());
        Map<Integer, Map<Integer, List<Integer>>> map = new HashMap<Integer, Map<Integer, List<Integer>>>();
        Map<Integer, List<Integer>> currentTypeMap = new HashMap<Integer, List<Integer>>();
        currentTypeMap.put(CardType.BEMINGGANG2.getType(), gangPai);
        map.put(roomInf.getCurrent(), currentTypeMap);// 杠的类型
        roomOperationInfoResponses = RoomOperationResponseUntils.returnRoomOperationInfoResponses(roomInf, map);
        //设置记录
//        List<Operation> history = roomInf.getHistory();
        Operation op = new Operation(OperationType.MINGGANG2.getType(), position, CardType.MINGGANG.getType(), cardNum);
//        history.add(op);
        OperationService.addOperation(roomInf.getRoomId(),roomInf.getCurrentCount(),op);
        roomInf.setTips(null);
        roomInf.setTempTips(null);
        roomInf.setHistory(op);
        save(roomInf);
        return roomOperationInfoResponses;
    }

    @Override
    public List<RoomOperationInfoResponse> anGang1(RoomInf roomInf, int cardNum) {
        List<RoomOperationInfoResponse> roomOperationInfoResponses = null;
        //当前玩家
        PlayerST currentPlayerST = roomInf.getPlayerByPosition(roomInf.getCurrent());
        //从手牌中删除 杠牌
        currentPlayerST.deleteCard(cardNum, CardType.SHOUPAI.getType());
        currentPlayerST.deleteCard(cardNum, CardType.SHOUPAI.getType());
        currentPlayerST.deleteCard(cardNum, CardType.SHOUPAI.getType());
        currentPlayerST.deleteCard(cardNum, CardType.SHOUPAI.getType());
        //杠的组合
        List<Integer> gangCardList = Lists.newArrayList(cardNum, cardNum, cardNum, cardNum);
        //把杠牌加到杠牌数组中
        currentPlayerST.addCard(gangCardList, CardType.ANGANG.getType());

        Map<Integer, Map<Integer, List<Integer>>> map = new HashMap<Integer, Map<Integer, List<Integer>>>();
        //当前杠的玩家
        Map<Integer, List<Integer>> currentTypeMap = new HashMap<Integer, List<Integer>>();
        currentTypeMap.put(CardType.ANGANG.getType(), gangCardList);
        map.put(roomInf.getCurrent(), currentTypeMap);// 杠的类型
        roomOperationInfoResponses = RoomOperationResponseUntils.returnRoomOperationInfoResponses(roomInf, map);
        roomInf.setTips(null);
        roomInf.setTempTips(null);
        //设置记录
//        List<Operation> history = roomInf.getHistory();
        Operation op = new Operation(OperationType.ANGANG.getType(), roomInf.getCurrent(), CardType.ANGANG.getType(), cardNum);
//        history.add(op);
        OperationService.addOperation(roomInf.getRoomId(),roomInf.getCurrentCount(),op);
        roomInf.setHistory(op);
        save(roomInf);
        logger.info("暗杠牌:" + currentPlayerST);
        return roomOperationInfoResponses;
    }

    @Override
    public List<RoomOperationInfoResponse> mingGang1(RoomInf roomInf, int cardNum, int current, int prePosition) {
        List<RoomOperationInfoResponse> roomOperationInfoResponses = null;
        //被杠的玩家
        PlayerST prePlayerST = roomInf.getPlayerByPosition(prePosition);
        //在上家已打的牌中删除此牌
        prePlayerST.deleteCard(cardNum, CardType.YICHUPAI.getType());
        //被杠的牌
        List<Integer> outCardList = Lists.newArrayList(cardNum);
        //当前玩家
        PlayerST currentPlayerST = roomInf.getPlayerByPosition(current);
        //从手牌中删除牌
        currentPlayerST.deleteCard(cardNum, CardType.SHOUPAI.getType());
        currentPlayerST.deleteCard(cardNum, CardType.SHOUPAI.getType());
        currentPlayerST.deleteCard(cardNum, CardType.SHOUPAI.getType());
        //杠的组合
        List<Integer> gangCardList = Lists.newArrayList(cardNum, cardNum, cardNum, cardNum);
        //把杠牌加到杠牌数组中
        currentPlayerST.addCard(gangCardList, CardType.MINGGANG.getType());

        Map<Integer, Map<Integer, List<Integer>>> map = new HashMap<Integer, Map<Integer, List<Integer>>>();
        //被杠的玩家
        Map<Integer, List<Integer>> preTypeMap = new HashMap<Integer, List<Integer>>();
        preTypeMap.put(CardType.BEMINGGANG.getType(), outCardList);
        map.put(prePosition, preTypeMap);// 被杠的类型
        //当前杠的玩家
        Map<Integer, List<Integer>> currentTypeMap = new HashMap<Integer, List<Integer>>();
        currentTypeMap.put(CardType.MINGGANG.getType(), gangCardList);
        map.put(current, currentTypeMap);// 杠的类型
        roomOperationInfoResponses = RoomOperationResponseUntils.returnRoomOperationInfoResponses(roomInf, map);
        roomInf.setTips(null);
        roomInf.setTempTips(null);
        roomInf.setCurrent(current);
        //设置记录
//        List<Operation> history = roomInf.getHistory();
        Operation op = new Operation(OperationType.MINGGANG1.getType(), current, CardType.MINGGANG.getType(), cardNum);
//        history.add(op);
        OperationService.addOperation(roomInf.getRoomId(),roomInf.getCurrentCount(),op);
        roomInf.setHistory(op);
        save(roomInf);
        logger.info("被杠牌:" + roomInf.getPlayerByPosition(prePosition));
        logger.info("明杠牌:" + roomInf.getPlayerByPosition(roomInf.getCurrent()));
        return roomOperationInfoResponses;
    }

    /**
     * 根据roomInf 保存到数据库
     *
     * @param roomInf
     */
    private void save(RoomInf roomInf) {
        roomInf.setLeftDeskCardNum(roomInf.getLeftDeskCards() == null ? 0 : roomInf.getLeftDeskCards().size());
        for (int i = 0; i < roomInf.getPlayers().size(); i++) {
            if (roomInf.getPlayers().get(i).getCards() == null && roomInf.getPlayers().get(i).getCards().size() > 0)
                continue;
            for (int j = 0; j < roomInf.getPlayers().get(i).getCards().size(); j++) {
                roomInf.getPlayers().get(i).getCards().get(j)
                        .setCardNum(roomInf.getPlayers().get(i).getCards().get(j).getCards().size());
            }
        }
        RoomService.setRoom(roomInf);
    }
}
