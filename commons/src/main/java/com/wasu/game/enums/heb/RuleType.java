package com.wasu.game.enums.heb;

public enum RuleType {
    JIA("不夹不胡", 1),
    JIA2("三七夹", 2),
    DA("先打后抓", 3),
    LOU("漏胡", 4),
    FENG("刮大风", 5),
    FEI("红中满天飞", 6);

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
