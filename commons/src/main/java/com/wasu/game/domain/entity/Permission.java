package com.wasu.game.domain.entity;

import javax.persistence.*;

/**
 * Created by kale on 2016/12/13.
 */
@Entity
@Table(name = "permission")
public class Permission {
    @Id
    @GeneratedValue
    private long id;

    @Column(name="user_id")
    private long userId;

    @Column(name="permission_id")
    private int permissionNum;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getPermissionNum() {
        return permissionNum;
    }

    public void setPermissionNum(int permissionNum) {
        this.permissionNum = permissionNum;
    }
}
