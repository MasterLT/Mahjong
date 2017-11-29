package com.wasu.game.domain.response;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/1/5.
 */
public class ConfigResponse implements Serializable {
    private static final long serialVersionUID = 111;

    private List<Map<String, String>> optionalRule;

    private List<Map<String, String>> optionalCount;

    public List<Map<String, String>> getOptionalRule() {
        return optionalRule;
    }

    public void setOptionalRule(List<Map<String, String>> optionalRule) {
        this.optionalRule = optionalRule;
    }

    public List<Map<String, String>> getOptionalCount() {
        return optionalCount;
    }

    public void setOptionalCount(List<Map<String, String>> optionalCount) {
        this.optionalCount = optionalCount;
    }
}
