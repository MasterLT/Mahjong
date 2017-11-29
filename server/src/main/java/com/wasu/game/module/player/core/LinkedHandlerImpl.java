package com.wasu.game.module.player.core;

import com.wasu.game.domain.RoomInf;
import com.wasu.game.domain.Tip;
import com.wasu.game.quarz.QuarzManager;
import com.wasu.game.quarz.job.*;
import com.wasu.game.service.RoomService;
import com.wasu.game.tools.AppBeanContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by lit on 2016/12/26.
 */
@Component("linkedHandler")
public class LinkedHandlerImpl implements LinkedHandler {

    private static LinkedHandler linkedHandler = null;
    @Autowired
    PlayCardUtils playCardUtils;

    public static LinkedHandler build() {
        if (linkedHandler == null) {
            linkedHandler= AppBeanContext.getBean(LinkedHandlerImpl.class);
        }
        return linkedHandler;
    }

    public void pop(long roomId, Integer outCard) {
//        QuarzManager.deleteJob(roomId, PopJob.class);
        //发送抓牌提示(听牌只提示胡)
        RoomInf room = RoomService.getRoom(roomId);
        List<Tip> tips = room.getTips();
        if (tips != null && tips.size() > 0) {
            playCardUtils.getCardTips(roomId, tips);
            //定时
//            QuarzManager.addJob(roomId, QuarzManager.tipsWaitTime, PopJob.class);
            return;
        }
        //听牌就立即出牌
//        if(room.getPlayerByPosition(room.getCurrent()).getStatus()== PlayerType.TING.getType()&&outCard==null){
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            //设置出牌
//            PlayerST playerST= SessionManager.getRoom(roomId).getPlayerByPosition(SessionManager.getRoom(roomId).getCurrent());
//            List<Integer> cardList=playerST.getCardsByType(CardType.SHOUPAI.getType());
//            outCard=cardList.get(cardList.size()-1);
//        }
        //未传outCard只发提示
        if (outCard == null) {
            QuarzManager.addJob(roomId, QuarzManager.outCardWaitTime, PopJob.class);
            return;
        }
        //出牌
        outCard = playCardUtils.playCard(roomId, outCard);
        sleep(500);
        //封装提示
        playCardUtils.setPlayCardTips(roomId, outCard);
        //抓牌
        push(roomId);
    }

    public void push(long roomId) {
        //发送提示
        List<Tip> tips = RoomService.getRoom(roomId).getTips();
        if (tips != null && tips.size() > 0) {
            playCardUtils.getCardTips(roomId, tips);
            //定时
//            QuarzManager.addJob(roomId, QuarzManager.tipsWaitTime, PushJob.class);
            return;
        }
        //流局：出牌结束流局，不发送提示和抓牌
        if (RoomService.getRoom(roomId).getLeftDeskCardNum() <= RoomService.getRoom(roomId).getDiscard()) {//剩余8张流局
            //结束
            playCardUtils.end(roomId);
            return;
        }
        int inCard = playCardUtils.getCard(roomId);
        //判断
        playCardUtils.setCardTips(roomId, inCard);
        sleep(500);
        //出牌
        pop(roomId, null);
    }

    /**
     * 發牌財神會直接胡牌
     * @param roomId
     */
    public void push1(long roomId) {
        int inCard = playCardUtils.getCard(roomId);
        //判断
        playCardUtils.setCardTips(roomId, inCard);
        //判斷財神會
        playCardUtils.setCaiShenHuiTips(roomId);
        //出牌
        pop(roomId, null);
    }

    public void sleep(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
