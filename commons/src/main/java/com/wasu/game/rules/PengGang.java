package com.wasu.game.rules;

import com.wasu.game.domain.PlayerST;
import com.wasu.game.enums.CardType;
import org.springframework.util.CollectionUtils;


import java.util.List;

/**
 * Created by Administrator on 2017/1/9.
 */
public class PengGang  extends BaseRule{
    private List<List<Integer>> hasTriplet=null;//刻牌

    public PengGang() {
    }

    public PengGang(List<List<Integer>> hasTriplet) {
        this.hasTriplet = hasTriplet;
    }

    @Override
   public boolean handle(PlayerST playerST, Integer cardNum) {
        List<Integer> pengPai=playerST.getCardsByType(CardType.PENGPAI.getType());
        List<Integer> mingGang=playerST.getCardsByType(CardType.MINGGANG.getType());
        List<Integer> anGang=playerST.getCardsByType(CardType.ANGANG.getType());
        if (!CollectionUtils.isEmpty(hasTriplet))
            return true;
        if (CollectionUtils.isEmpty(pengPai)&&CollectionUtils.isEmpty(mingGang)&&CollectionUtils.isEmpty(anGang)){
            System.out.print("没有杠、碰、刻");
            return false;
        }else {
            if (null!=nextRule){
                return  nextRule.handle(playerST,cardNum);
            }
            return true;
        }
    }
}
