package com.wasu.game.login.dao;

import com.wasu.game.domain.entity.User;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

/**
 * 玩家dao
 * @author -琴兽-
 *
 */
@Component
public class UserDao {
	
	@Autowired
	private HibernateTemplate hibernateTemplate;
	
	/**
	 * 获取玩家通过id
	 * @param playerId
	 * @return
	 */
	public User getUserById(long id){
		return hibernateTemplate.get(User.class, id);
	}
	
	
	/**
	 * 获取玩家通过玩家名
	 * @param playerName
	 * @return
	 */
	public User getUserByWId(final String wId){
		
		return hibernateTemplate.execute(new HibernateCallback<User>() {

			@Override
			public User doInHibernate(Session session)throws HibernateException, SQLException {
//				SQLQuery query = session.createSQLQuery("SELECT * FROM user where w_id = ?");
//				query.setString(0, wId);
//				query.addEntity(User.class);
				Query query=session.createQuery("from User u where u.wId=?");
				query.setString(0, wId);

				@SuppressWarnings("unchecked")
				List<User> list = query.list();
				if(list==null || list.isEmpty()){
					return null;
				}
				return list.get(0);
			}
		});
	}
	
	
	/**
	 * 创建玩家
	 * @param player
	 * @return
	 */
	public User createUser(User user){
		long id = (Long) hibernateTemplate.save(user);
		user.setId(id);
		return user;
	}
	
	/**
	 * 更新玩家
	 * @param player
	 * @return
	 */
	public void updateUser(User user){
		hibernateTemplate.update(user);
	}
}
