package com.wasu.game.enums.heb;

/**
 * Created by Administrator on 2017/1/16.
 */
public enum HuType {
    DIANPAO("点炮", 1, 1),
    DIANPAOJIA("夹胡点炮", 2, 2),
    DIANPAOZHAN("站立点炮", 3, 3),
    DIANPAOJIAZHAN("站立夹胡点炮", 4, 6),
    ZIMO("自摸", 5, 2),
    ZIMOJIA("夹胡自摸", 6, 4),
    ZIMOZHAN("站立自摸", 7, 3),
    ZIMOJIAZHAN("站立夹胡自摸", 8, 6),
    MOBAO("摸宝", 9, 3),
    MOBAOJIA("夹胡摸宝", 10, 6),
    BAOZHONGBAO("宝中宝", 11, 12),
    GUADAFENG("刮大风", 12, 3),
    GUADAFENGJIA("夹胡刮大风", 13, 6),
    HONGZHON("红中满天飞", 14, 3),
    HONGZHONJIA("夹胡红中满天飞", 15, 6),
    LOUHU("漏胡", 16, 12),
    LOUHU1("漏胡夹胡", 17, 12),
    LOUHU2("漏胡刮大风", 18, 12),
    LOUHU3("漏胡红中满天飞", 19, 12);

    private String name;
    private int type;
    private int score;

    HuType(String name, int type, int score) {
        this.name = name;
        this.type = type;
        this.score = score;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
