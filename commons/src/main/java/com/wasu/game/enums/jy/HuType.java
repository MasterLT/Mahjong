package com.wasu.game.enums.jy;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2017/4/12.
 */
public enum HuType {

    DIANPAO("平胡点炮", 1, 1, 1),
    ZIMO("平胡自摸", 2, 2, 2),
    TIANHU("天胡", 3, 4, 4),
    DIHU("地胡", 4, 4, 4),
    QIDUI("七对子", 5, 2, 4),
    QINGYISE("清一色", 6, 4, 8),
    ZIQING("字清", 7, 40, 40),
    DAPENGHU("大碰胡", 8, 2, 4),
    CAISHENHUI("财神会", 9, 4, 4),
    SHISANYAO("十三幺", 10, 2, 4),
    DUDIAO("独吊", 11, 2, 4),
    GANGKAI("杠开", 12, 4, 4),
    QIANGGANGHU("抢杠胡", 13, 4, 4),
    QINGYISE1("清一色不翻", 14, 2, 4),
    GANGKAI1("杠开不翻", 15, 2, 4);

    private String name;
    private int type;
    private int paoScore;
    private int moScore;

    HuType(String name, int type, int paoScore, int moScore) {
        this.name = name;
        this.type = type;
        this.paoScore = paoScore;
        this.moScore = moScore;
    }

    public static int getScoreByType(int type, boolean ziMo) {
        int score = 0;
        for (HuType h : HuType.values()) {
            if (h.getType() != type)
                continue;
            if (ziMo)
                score = h.getMoScore();
            else
                score = h.getPaoScore();
            System.out.println(h.getName()+":积分" + score+"++");
        }
        return score;
    }

    public static int getScoreByType(List<Integer> types, boolean ziMo) {
        types = Lists.newArrayList(types);
        int total;
        if (types.size() == 1)
            total = getScoreByType(types.get(0), ziMo);
        else {
            types.remove((Integer) HuType.ZIMO.getType());
            types.remove((Integer) HuType.DIANPAO.getType());
            List<Integer> scores = Lists.newArrayList();
            for (Integer type : types) {
                if (type == null)
                    continue;
                scores.add(getScoreByType(type, ziMo));
            }
            Collections.sort(scores);
            int max = scores.get(scores.size() - 1);
            total = max * (int) Math.pow(2, scores.size() - 1);
        }

        return total;
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

    public int getPaoScore() {
        return paoScore;
    }

    public void setPaoScore(int paoScore) {
        this.paoScore = paoScore;
    }

    public int getMoScore() {
        return moScore;
    }

    public void setMoScore(int moScore) {
        this.moScore = moScore;
    }

    public static void main(String[] args) {
        List<Integer> huType = Lists.newArrayList();
        huType.add(HuType.TIANHU.getType());
        huType.add(HuType.CAISHENHUI.getType());
        huType.add(HuType.QINGYISE.getType());

        huType.add(HuType.DIANPAO.getType());
        System.out.println(getScoreByType(huType, true));
    }
}
