package com.wasu.game.login.dao;

import com.wasu.game.domain.entity.Activity;
import com.wasu.game.domain.entity.Config;
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
public class ConfigDao {
	
	@Autowired
	private HibernateTemplate hibernateTemplate;

	public Config getConfigById(long id){
		return hibernateTemplate.get(Config.class, id);
	}

	public List<Config> getConfigByKey(final String key){

		return hibernateTemplate.executeWithNativeSession(new HibernateCallback<List<Config>>() {

			@Override
			public List<Config> doInHibernate(Session session)throws HibernateException, SQLException {

				SQLQuery query = session.createSQLQuery("SELECT * FROM config where config_key like ?");
				query.setString(0,key);
				query.addEntity(Config.class);

//				Query query=session.createQuery("from Permission u where u.userId=?");
//				query.setLong(0, userId);
				List<Config> list = query.list();
				return list;
			}
		});
	}
	
	public Config createConfig(Config activity){
		long id = (Long) hibernateTemplate.save(activity);
		activity.setId(id);
		return activity;
	}
	
	public void updateConfig(Config activity){
		hibernateTemplate.update(activity);
	}
}
