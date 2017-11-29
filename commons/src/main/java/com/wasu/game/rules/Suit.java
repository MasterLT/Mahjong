package com.wasu.game.rules;

import com.wasu.game.domain.PlayerST;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/1/5.
 */
public class Suit extends BaseRule{
    private List<Integer> cards=null;
    public Suit() {  //是否是清一色
    }
    public Suit(List<Integer> cards) {
        this.cards = cards;
    }
    @Override
     public  boolean handle(PlayerST playerST,Integer cardNum) {
        if (isFlush(playerST)){
            System.out.println("是清一色");
           return false;
        }else {
            if (null!=nextRule){
                return  nextRule.handle(playerST,cardNum);
            }
            return true;
        }
    }
    private boolean isFlush(PlayerST playerST) {
        List<List<Integer>> useList=playerST.returnUseCards();
        int[] suitNo=new int[3];
        int no=0;
        calculateSuit(suitNo,useList);
        for (int i=0;i<suitNo.length;i++){
            if (suitNo[i]>0){
                no++;
            }
        }
        if (no>1){
            return false;
        }
        return true;
    }

    //计算是否是清一色
    private void calculateSuit(int[] suitNo, List<List<Integer>> useList) {
        for (int j=0;j<useList.size();j++){
            List<Integer> pai=useList.get(j);
            if (pai!=null&&pai.size()>0){
                for (int i=0;i<pai.size();i++){
                    Integer count=pai.get(i);
                    if (count<10&&count>0){
                        suitNo[0]++;
                    }else if (count>9&&count<19){
                        suitNo[1]++;
                    }else if (count>18&&count<28){
                        suitNo[2]++;
                    }
                }
            }
            if (!CollectionUtils.isEmpty(this.cards)){
                for (int i=0;i<cards.size();i++){
                    int count=cards.get(i);
                    if (count<10&&count>0){
                        suitNo[0]++;
                    }else if (count>9&&count<19){
                        suitNo[1]++;
                    }else if (count>18&&count<28){
                        suitNo[2]++;
                    }
                }
            }
        }
    }
}
