package com.wasu.game.enums;

public enum CardChangeType {
    GOU("购买", 0),
    ZENG("赠送", 1),
    BU("补偿", 2),
    KAI("开房", 3),
    ZHUAN("转卖", 4),
    HUO("活动", 5),
    QI("其它", 6);


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

    private CardChangeType(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public static String getTypeName(int type){
        for (CardChangeType c:CardChangeType.values()){
            if (c.getType()==type)
                return c.getName();
        }
        return "";
    }
}
