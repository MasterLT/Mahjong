package com.wasu.game.module.player.dao;

import com.wasu.game.domain.entity.Activity;
import com.wasu.game.domain.entity.Permission;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
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
public class ActivityDao {
	
	@Autowired
	private HibernateTemplate hibernateTemplate;

	public Activity getActivityById(long id){
		return hibernateTemplate.get(Activity.class, id);
	}

	public Activity getActivityByUserIdAndType(final long userId,final int type){

		return hibernateTemplate.executeWithNativeSession(new HibernateCallback<Activity>() {

			@Override
			public Activity doInHibernate(Session session)throws HibernateException, SQLException {

				SQLQuery query = session.createSQLQuery("SELECT * FROM activity where user_id =? and type=? and create_time>= CURDATE() and create_time <DATE_ADD(CURDATE(),INTERVAL 1 DAY)");
				query.setLong(0,userId);
				query.setInteger(1,type);
				query.addEntity(Activity.class);

//				Query query=session.createQuery("from Permission u where u.userId=?");
//				query.setLong(0, userId);
				List<Activity> list = query.list();
				if(list==null || list.isEmpty()){
					return null;
				}
				return list.get(0);
			}
		});
	}
	
	public Activity createActivity(Activity activity){
		long id = (Long) hibernateTemplate.save(activity);
		activity.setId(id);
		return activity;
	}
	
	public void updateActivity(Activity activity){
		hibernateTemplate.update(activity);
	}
}
