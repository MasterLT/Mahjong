package com.wasu.game.module.player.dao;

import com.wasu.game.domain.entity.RoomUser;
import com.wasu.game.domain.entity.User;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * 玩家dao
 * @author -琴兽-
 *
 */
@Component
public class RoomUserDao {
	
	@Autowired
	private HibernateTemplate hibernateTemplate;
	
	/**
	 * 创建玩家
	 * @return
	 */
	public RoomUser createUser(RoomUser user){
		long id = (Long) hibernateTemplate.save(user);
		user.setId(id);
		return user;
	}
	
	/**
	 * 更新玩家
	 * @return
	 */
	public void updateUser(RoomUser user){
		hibernateTemplate.update(user);
	}

	/**
	 * 更新玩家
	 * @return
	 */
	public void delete(RoomUser user){
		hibernateTemplate.delete(user);
	}
}
