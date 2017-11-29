package com.wasu.game.enums;

public enum MahjongType {
    BASE(0, 27),
    ALL(1, 34);

    private int type;
    private int cardNum;

    MahjongType(int type, int cardNum) {
        this.type = type;
        this.cardNum = cardNum;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCardNum() {
        return cardNum;
    }

    public void setCardNum(int cardNum) {
        this.cardNum = cardNum;
    }

    public static MahjongType getMahjongTypeByType(int type) {
        MahjongType mtype = null;
        for (MahjongType m : MahjongType.values()) {
            if (m.getType() == type) {
                mtype = m;
                break;
            }
        }
        return mtype;
    }

}
