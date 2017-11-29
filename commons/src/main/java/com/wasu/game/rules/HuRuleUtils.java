package com.wasu.game.rules;

import com.wasu.game.domain.PlayerST;
import com.wasu.game.domain.RoomInf;
import com.wasu.game.enums.heb.HuType;
import com.wasu.game.enums.heb.RuleType;
import com.wasu.game.utilities.RoomRuleUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/1/17.
 */
public class HuRuleUtils {

    public static int ziMoType(RoomInf roomInf, List<Integer> rules, Long playerId, int cardNum) {
        int huType = -1;
        PlayerST playerST = roomInf.getPlayerByPosition(roomInf.getPositionById(playerId));
        if (playerST.getHuInfo().isJia()) {//是否是夹胡
            if (playerST.getHuInfo().getHuCards().contains(cardNum))
                huType = HuType.ZIMOJIA.getType();
            if (RoomRuleUtils.moBao(cardNum, playerId, roomInf))
                huType = HuType.MOBAOJIA.getType();
            if (rules != null && rules.contains(RuleType.FENG.getType()) && RoomRuleUtils.feng(cardNum, playerId, roomInf))
                huType = HuType.GUADAFENGJIA.getType();
            if (rules != null && rules.contains(RuleType.FEI.getType()) && RoomRuleUtils.hongzhong(cardNum, playerId, roomInf))
                huType = HuType.HONGZHONJIA.getType();
            if (RoomRuleUtils.baoZhongBao(cardNum, playerId, roomInf))
                huType = HuType.BAOZHONGBAO.getType();
        } else {
            if (playerST.getHuInfo().getHuCards().contains(cardNum))
                huType = HuType.ZIMO.getType();
            if (RoomRuleUtils.moBao(cardNum, playerId, roomInf))
                huType = HuType.MOBAO.getType();
            if (rules != null && rules.contains(RuleType.FENG.getType()) && RoomRuleUtils.feng(cardNum, playerId, roomInf))
                huType = HuType.GUADAFENG.getType();
            if (rules != null && rules.contains(RuleType.FEI.getType()) && RoomRuleUtils.hongzhong(cardNum, playerId, roomInf))
                huType = HuType.HONGZHON.getType();
        }
        return huType;
    }
}
