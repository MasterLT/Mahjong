package com.wasu.game.enums;

public enum OperationType {
    FAPAI("发牌", 0),
    IN("抓牌", 1),
    OUTHAND("出牌", 3),
    CHI("吃", 4),
    PENG("碰", 5),
    CHITING("吃听", 6),
    PENGTING("碰听", 7),
    MINGGANG1("明杠", 8),
    MINGGANG2("明杠", 9),
    ANGANG("暗杠", 10);

    private String name;
    private int type;

    OperationType(String name, int type) {
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
