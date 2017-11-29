package com.wasu.game.enums;

/**
 * Created by Administrator on 2016/12/29.
 */
public enum PlayerType {
    BASE("正常", 0),
    TING("听", 1),
    HU("胡", 2),
    LOST("输", 3);

    private String name;
    private int type;

    PlayerType(String name, int type) {
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
