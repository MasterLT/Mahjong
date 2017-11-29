package com.wasu.game.enums;

/**
 * Created by Administrator on 2017/2/16.
 */
public enum TypePriorityOfOutFrist {


    CANHU(25, 2000),
    CANLOUHU(230, 2000),//是否漏胡

    CANTING(27, 1000),////提示是否听的时候  返回给玩家的牌的类型
    CANANGANG(23, 1000),//提示是否暗杠的时候  返回给玩家的牌的类型

    CANPENGTING(26, 900),//提示是否碰听的时候  返回给玩家的牌的类型
    CANCHITING(28, 900),
    CANMINGGANG2(29, 800),

    TING(61, 1000),
    ERROR(0, 0),//提示是否吃听的时候  返回给玩家的牌的类型
    CANPENGPAI(21, 800),//提示是否碰的时候  返回给玩家的牌的类型
    CANCHIPAI(22, 100),//提示是否吃的时候  返回给玩家的牌的类型
    CANMINGGANG(24, 800);//提示是否明杠的时候  返回给玩家的牌的类型


    private int type;
    private int priority;

    TypePriorityOfOutFrist(int type, int priority) {
        this.type = type;
        this.priority = priority;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public static TypePriorityOfOutFrist getTypePriotityByType(int type) {
        for (TypePriorityOfOutFrist typePriority : TypePriorityOfOutFrist.values()) {
            if (typePriority.getType() == type) {
                return typePriority;
            }
        }
        return ERROR;
    }
}
