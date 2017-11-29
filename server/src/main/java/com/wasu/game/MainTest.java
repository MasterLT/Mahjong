package com.wasu.game;


import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wasu.game.domain.PlayerST;
import com.wasu.game.domain.RoomInf;
import com.wasu.game.enums.CardType;
import com.wasu.game.enums.heb.HuType;
import com.wasu.game.module.player.core.PlayCardUtils;
import com.wasu.game.service.RoomService;
import com.wasu.game.service.UserService;
import com.wasu.game.tools.AppBeanContext;
import com.wasu.game.tools.caches.RedisCache;

import java.util.List;

public class MainTest {

    public static void main(String[] args) throws Exception {
        Long roomId=2879L;
        Long playerId=2931L;
        List<Integer> list= Lists.newArrayList(1,6,6,8,8,12,12,15,15,16,16,17,17);
        RoomInf roomInf=RoomService.getRoom(roomId);
        PlayerST playerST=null;
        for(PlayerST p:roomInf.getPlayers()){
            if(p.getPlayerId()==playerId){
                playerST=p;
                break;
            }
        }
        List<Integer> cardList =playerST.getCardsByType(CardType.SHOUPAI.getType());
        cardList.clear();
        cardList.addAll(list);
        RoomService.save(roomInf);
    }

    //积分测试
    static void point() {//4083,4084,4085,4086
        PlayCardUtils playCardUtils = AppBeanContext.getBean(PlayCardUtils.class);
        RoomInf roomInf = JSONObject.parseObject("{\"adminId\":4083,\"applyDissolution\":0,\"banker\":2,\"baoCard\":27,\"current\":0,\"currentCount\":1,\"currentRount\":1,\"firstBanker\":2,\"huCard\":7,\"leftDeskCardNum\":51,\"leftDeskCards\":[16,9,1,7,24,26,7,14,25,33,7,12,18,19,13,22,2,3,5,23,17,3,20,20,10,11,21,21,26,13,26,25,23,24,8,4,6,20,23,15,15,5,12,21,5,6,1,20,33,14,24],\"option\":11,\"pao\":4084,\"playerNum\":4,\"players\":[{\"cards\":[{\"cardNum\":10,\"cards\":[4,5,10,8,11,9,24,9,25,4],\"type\":0},{\"cardNum\":1,\"cards\":[4],\"type\":32},{\"cardNum\":1,\"cards\":[33],\"type\":15},{\"cardNum\":2,\"cards\":[33,14],\"type\":31},{\"cardNum\":3,\"cards\":[1,3,2],\"type\":12}],\"playerId\":4083,\"position\":0,\"stand\":false,\"state\":true,\"status\":0},{\"cards\":[{\"cardNum\":13,\"cards\":[21,12,10,9,8,22,16,11,14,16,22,11,17],\"type\":0},{\"cardNum\":3,\"cards\":[2,11,17],\"type\":32},{\"cardNum\":2,\"cards\":[27,7],\"type\":15},{\"cardNum\":3,\"cards\":[27,2,7],\"type\":31}],\"playerId\":4084,\"position\":1,\"stand\":true,\"state\":true,\"status\":0},{\"cards\":[{\"cardNum\":10,\"cards\":[1,13,18,10,22,15,17,15,16,6],\"type\":0},{\"cardNum\":3,\"cards\":[2,16,6],\"type\":32},{\"cardNum\":3,\"cards\":[33,27,2],\"type\":15},{\"cardNum\":4,\"cards\":[33,27,26,2],\"type\":31},{\"cardNum\":3,\"cards\":[3,4,2],\"type\":12}],\"playerId\":4085,\"position\":2,\"stand\":false,\"state\":true,\"status\":0},{\"cards\":[{\"cardNum\":7,\"cards\":[19,8,19,18,18,6,19],\"type\":0},{\"cardNum\":1,\"cards\":[25],\"type\":32},{\"cardNum\":2,\"cards\":[23,17],\"type\":15},{\"cardNum\":3,\"cards\":[23,17,2],\"type\":31},{\"cardNum\":6,\"cards\":[13,12,14,25,27,26],\"type\":12},{\"cardNum\":1,\"cards\":[7],\"type\":54}],\"huInfo\":{\"huCards\":[7],\"huType\":2,\"jia\":true},\"playerId\":4086,\"position\":3,\"stand\":false,\"state\":true,\"status\":2,\"tempHuInfo\":{2:{\"huCards\":[7],\"huType\":0,\"jia\":true}}}],\"posistions\":[],\"roomId\":1838,\"round\":1,\"rule\":[\"2\",\"3\",\"4\",\"5\",\"6\"],\"status\":1,\"tempTips\":[{\"position\":3,\"priority\":1002,\"tipInfos\":[{\"cards\":[{\"cardNum\":1,\"cards\":[7],\"type\":54}],\"position\":1,\"userid\":0}],\"type\":25,\"userid\":4086}],\"totalCount\":0,\"win\":4086}",
                RoomInf.class);
        RoomService.setRoom(roomInf);
        //设置点炮
        roomInf.setPao(null);
        //胡
        hu(roomInf, HuType.LOUHU.getType());
        //听
        ting(roomInf, 4083L, 0);
        //站
        zhan(roomInf, 4083L, false);
        zhan(roomInf, 4084L, false);
        zhan(roomInf, 4085L, true);
        playCardUtils.end(roomInf.getRoomId());
    }

    static void hu(RoomInf roomInf, int huType) {
        PlayerST playerST = roomInf.getPlayerByPosition(RoomService.getRoomUser(roomInf.getRoomId(), roomInf.getWin()).getPosition());
        playerST.getHuInfo().setHuType(huType);
        RoomService.setRoom(roomInf);
    }

    static void ting(RoomInf roomInf, Long id, int is) {
        PlayerST playerST = roomInf.getPlayerByPosition(RoomService.getRoomUser(roomInf.getRoomId(), id).getPosition());
        playerST.setStatus(is);
        RoomService.setRoom(roomInf);
    }

    static void zhan(RoomInf roomInf, Long id, boolean is) {
        PlayerST playerST = roomInf.getPlayerByPosition(RoomService.getRoomUser(roomInf.getRoomId(), id).getPosition());
        playerST.setStand(is);
        RoomService.setRoom(roomInf);
    }
}
