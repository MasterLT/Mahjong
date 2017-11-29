package com.wasu.game.domain.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by 琴兽 on 2016/12/13.
 */
@Entity
@Table(name = "config")
public class Config {
    @Id
    @GeneratedValue
    private long id;

    @Column(name="config_key")
    private String configKey;

    @Column(name="config_value")
    private String configValue;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }
}
