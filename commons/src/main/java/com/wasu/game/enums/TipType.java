package com.wasu.game.enums;

/**
 * Created by Administrator on 2016/12/28.
 */
public enum TipType {

    CANPENGPAI("是否碰牌", 21),//提示是否碰的时候  返回给玩家的牌的类型
    CANCHIPAI("是否吃牌", 22),//提示是否吃的时候  返回给玩家的牌的类型
    CANANGANG("是否暗杠", 23),//提示是否暗杠的时候  返回给玩家的牌的类型
    CANMINGGANG("是否明杠", 24),//提示是否明杠的时候  返回给玩家的牌的类型
    CANHU("是否胡牌", 25),
    CANPENGTING("是否碰听", 26),//提示是否碰听的时候  返回给玩家的牌的类型
    CANTING("是否听", 27),////提示是否听的时候  返回给玩家的牌的类型
    CANCHITING("是否吃听", 28),//提示是否吃听的时候  返回给玩家的牌的类型
    CANMINGGANG2("抓牌时明杠", 29),//在抓牌时，提示明杠
    CANLOUHU("是否漏胡", 230),//是否漏胡
    OUTOFTING("要出的牌", 31),//吃听 、碰听、听后需要出的牌的类型
    TING("听", 61),;

    private String name;
    private int type;

    TipType(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
