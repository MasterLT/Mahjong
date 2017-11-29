package com.wasu.game.module.player.dao;

import com.wasu.game.domain.entity.CardRecord;
import com.wasu.game.domain.entity.RoomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

/**
 * 玩家dao
 * @author -琴兽-
 *
 */
@Component
public class CardRecordDao {
	
	@Autowired
	private HibernateTemplate hibernateTemplate;
	
	/**
	 * 创建玩家
	 * @return
	 */
	public CardRecord createUser(CardRecord user){
		long id = (Long) hibernateTemplate.save(user);
		user.setId(id);
		return user;
	}
	
	/**
	 * 更新玩家
	 * @return
	 */
	public void updateUser(CardRecord user){
		hibernateTemplate.update(user);
	}

	/**
	 * 更新玩家
	 * @return
	 */
	public void delete(CardRecord user){
		hibernateTemplate.delete(user);
	}
}
