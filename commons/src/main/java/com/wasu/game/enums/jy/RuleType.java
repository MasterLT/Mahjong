package com.wasu.game.enums.jy;

public enum RuleType {
    ZHUANG("五十根下庄家", 1),
    QING("清一色翻倍", 2),
    GANG("上开花", 3);

    private String name;
    private int type;

    RuleType(String name, int type) {
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
