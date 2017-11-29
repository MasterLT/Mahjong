package com.wasu.game.login.dao;

import com.wasu.game.domain.entity.Room;
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
public class RoomDao {
	
	@Autowired
	private HibernateTemplate hibernateTemplate;
	
	public Room getRoomById(long id){
		return hibernateTemplate.get(Room.class, id);
	}
	
	public Room getRoomByRoomId(final long roomId){
		
		return hibernateTemplate.execute(new HibernateCallback<Room>() {

			@Override
			public Room doInHibernate(Session session)throws HibernateException, SQLException {
				Query query=session.createQuery("from Room u where u.roomId=?");
				query.setLong(0, roomId);
				List<Room> list = query.list();
				if(list==null || list.isEmpty()){
					return null;
				}
				return list.get(0);
			}
		});
	}
	
	public Room createRoom(Room room){
		long id = (Long) hibernateTemplate.save(room);
		room.setId(id);
		return room;
	}
	
	public void updateRoom(Room room){
		hibernateTemplate.update(room);
	}
}
