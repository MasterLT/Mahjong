package com.wasu.game.enums;

public enum CardType {
    SHOUPAI("手牌", 0),
    ROLL("掷骰子", 1),

    PENGPAI("碰牌", 11),
    CHIPAI("吃牌", 12),
    ANGANG("暗杠", 13),
    MINGGANG("明杠", 14),
    YICHUPAI("已出", 15),//�Ѿ������
    UNUSEPAI("剩牌", 16),//���������

    OUT("出牌", 31),
    IN("抓牌", 32),

    BEPENG("被碰的牌", 51),
    BEMINGGANG("被杠的牌", 52),
    BECHI("被吃的牌", 53),
    BEHU("被胡的牌", 54),
    BEMINGGANG2("抓牌时明杠", 55),

    BAO("宝牌", 100),
    GODWEALTH("财神", 101);

    private String name;
    private int type;


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

    private CardType(String name, int type) {
        this.name = name;
        this.type = type;
    }
}
