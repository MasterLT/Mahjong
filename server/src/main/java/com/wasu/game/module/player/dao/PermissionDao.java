package com.wasu.game.module.player.dao;

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
public class PermissionDao {
	
	@Autowired
	private HibernateTemplate hibernateTemplate;

	public Permission getPermissionById(long id){
		return hibernateTemplate.get(Permission.class, id);
	}

	public Permission getPermissionByUserId(final long userId){

		return hibernateTemplate.executeWithNativeSession(new HibernateCallback<Permission>() {

			@Override
			public Permission doInHibernate(Session session)throws HibernateException, SQLException {

				SQLQuery query = session.createSQLQuery("SELECT * FROM permission where user_id =? ");
				query.setLong(0,userId);
				query.addEntity(Permission.class);

//				Query query=session.createQuery("from Permission u where u.userId=?");
//				query.setLong(0, userId);
				List<Permission> list = query.list();
				if(list==null || list.isEmpty()){
					return null;
				}
				return list.get(0);
			}
		});
	}
	
	public Permission createRoom(Permission permission){
		long id = (Long) hibernateTemplate.save(permission);
		permission.setId(id);
		return permission;
	}
	
	public void updateRoom(Permission permission){
		hibernateTemplate.update(permission);
	}
}
