package com.wasu.game.login.service;


import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wasu.game.activeMQ.JmsTypeEnum;
import com.wasu.game.activeMQ.MQProductor;
import com.wasu.game.domain.*;
import com.wasu.game.domain.entity.*;
import com.wasu.game.domain.response.ConfigResponse;
import com.wasu.game.domain.response.UserResponse;
import com.wasu.game.enums.heb.RuleType;
import com.wasu.game.exception.ErrorCodeException;
import com.wasu.game.login.dao.*;
import com.wasu.game.module.CardCmd;
import com.wasu.game.module.ModuleId;
import com.wasu.game.module.PlayerCmd;
import com.wasu.game.netty.ServerHandler;
import com.wasu.game.service.RoomService;
import com.wasu.game.service.UserSessionService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 玩家服务
 *
 * @author -琴兽-
 */
@Component
public class LoginServiceImpl implements LoginService {

    private static Logger logger = Logger.getLogger(LoginServiceImpl.class);

    @Autowired
    private UserDao userDao;
    @Autowired
    private RoomDao roomDao;
    @Autowired
    private PermissionDao permissionDao;
    @Autowired
    private ActivityDao activityDao;
    @Autowired
    private ConfigDao configDao;
    @Autowired
    private RongYunservice rongYunservice;


    @Override
    public UserResponse weixinLogin(Session session, String wId, String face, String name, String gender) throws Exception {
        JSONObject data = null;
        // 判断当前会话是否已登录
//        if (session.getAttachment() != null) {
//            throw new ErrorCodeException(ResultCode.HAS_LOGIN);
//        }

        // 玩家不存在则新建
        User user = userDao.getUserByWId(wId);
        if (user == null) {
            user = new User();
            user.setwId(wId);
            user.setType(0);//玩家
            user.setName(name);
            user.setFace(face);
            user.setRegisterDate(new Date());
            user.setRegisterIp(session.getIp());
            user.setGender("男".equals(gender)?0:1);
            userDao.createUser(user);
            Permission p = new Permission();
            p.setPermissionNum(1000);//初始送10张房卡
            p.setUserId(user.getId());
            permissionDao.createRoom(p);
        } else {
            Long roomId = user.getRoomId();
            //断线重连
            if (roomId != 0) {
                logger.info("该用户在房间"+roomId+"中！");
                Room room = roomDao.getRoomById(roomId);
                if (room != null && room.getState() != 2) {
                    logger.info("房间"+roomId+"还在使用中");
                    data = new JSONObject();
                    data.put("id", user.getId());
                    data.put("roomId", roomId);
                } else {
                    user.setRoomId(0);
                }
            }
        }
        user.setName(name);
        user.setFace(face);
        user.setGender("男".equals(gender)?0:1);
        user.setLoginIp(session.getIp());
        user.setLoginDate(new Date());
        user.setLoginTimes(user.getLoginTimes() + 1);
        user.setToken(rongYunservice.getToken(user));
        userDao.updateUser(user);

        // 判断是否在其他地方登录过
//        boolean onlinePlayer = SessionManager.isOnlinePlayer(user.getId());
//        if (onlinePlayer) {
//            Session oldSession = SessionManager.removeSession(user.getId());
//            oldSession.removeAttachment();
//            // 踢下线
//            oldSession.close();
//            logger.info(user.getId() + "已登录，被强迫下线");
//        }
//
//        // 加入在线玩家会话
//        if (SessionManager.putSession(user.getId(), session)) {
//            session.setAttachment(user);
//        } else {
//            throw new ErrorCodeException(ResultCode.LOGIN_FAIL);
//        }
        UserResponse res = new UserResponse(user);
        //是否抽奖
        boolean[] isJoin=new boolean[2];
        Activity activity1=activityDao.getActivityByUserIdAndType(user.getId(),1);
        if (activity1!=null)
            isJoin[0]=true;
        Activity activity2=activityDao.getActivityByUserIdAndType(user.getId(),2);
        if (activity2!=null)
            isJoin[1]=true;
        res.setIsJoin(isJoin);
        //获取房卡
        Permission permission = permissionDao.getPermissionByUserId(user.getId());
        res.setRoomCard(permission.getPermissionNum());
        //发送断线重连
        if (data != null) {
            logger.info("发起断线重连");
            MQProductor mQProductor = MQProductor.build();
            mQProductor.send(Request.valueOf(ModuleId.CARD, CardCmd.RECONNECTED, data), JmsTypeEnum.REQUEST);
        }
        return res;
    }

    @Override
    public List<Map<String,String>> config() throws Exception {
        List<Map<String,String>> config = Lists.newArrayList();
        List<Config> configs=configDao.getConfigByKey("playerNum%");
        for (Config c:configs){
            Map<String,String> map=Maps.newHashMap();
            map.put(c.getConfigKey().split("_")[0],c.getConfigKey().split("_")[1]);
            map.put("value",c.getConfigValue());
            config.add(map);
        }
        return config;
    }

    @Override
    public void leave(Long id) throws Exception {
        User user = userDao.getUserById(id);
        if (user != null && user.getRoomId() != 0) {
            RoomInf roomInf = RoomService.getRoom(user.getRoomId());
            if (roomInf == null)
                return;
            //设置用户掉线状态
            PlayerST player = roomInf.getPlayerById(id);
            player.setOnline(false);
            RoomService.setRoom(roomInf);

            Result<Long> res = new Result<Long>();
            res.setCmd(PlayerCmd.Leave);
            res.setModule(ModuleId.Login);
            res.setContent(id);
            List<Long> users = Lists.newArrayList();
            for (PlayerST playerST : roomInf.getPlayers()) {
                users.add(playerST.getPlayerId());
            }
            res.setUsers(users);
            ServerHandler.Radiate(res);
        }
    }

    public Map<String, String> buildCountMap(String name, String value) {
        Map<String, String> m = new HashMap<String, String>();
        m.put("name", name);
        m.put("value", value);
        return m;
    }

    public void link(Session session, String wid){
        User user = userDao.getUserByWId(wid);
        if (user == null) {
            user = new User();
            user.setwId(wid);
            user.setType(0);//玩家
            user.setRegisterDate(new Date());
            user.setRegisterIp(session.getIp());
            userDao.createUser(user);
            Permission p = new Permission();
            p.setPermissionNum(1000);//初始送10张房卡
            p.setUserId(user.getId());
            permissionDao.createRoom(p);
        }
        boolean onlinePlayer = UserSessionService.isOnline(user.getId());
        if (onlinePlayer) {
            Session oldSession = UserSessionService.removeSession(user.getId());
            oldSession.removeAttachment();
            // 踢下线
            oldSession.close();
            logger.info(user.getId() + "已登录，被强迫下线");
        }

        // 加入在线玩家会话
        if (UserSessionService.putSession(user.getId(), session)) {
            session.setAttachment(user);
        } else {
            throw new ErrorCodeException(ResultCode.LOGIN_FAIL);
        }
    }

    @Override
    public int authen(int id, String realName, String idNum) {
        User user = userDao.getUserById(id);
        user.setIdNum(idNum);
        user.setRealName(realName);
        userDao.updateUser(user);
        return 1;
    }
}
