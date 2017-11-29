package com.wasu.game.module.player.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

import com.wasu.game.domain.entity.Room;

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
		return hibernateTemplate.executeWithNativeSession(new HibernateCallback<Room>() {

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

	public List<Room> getRoomByAdminId(final long roomId, final int state){
		return hibernateTemplate.executeWithNativeSession(new HibernateCallback<List<Room>>() {

			@Override
			public List<Room> doInHibernate(Session session)throws HibernateException, SQLException {
				Query query=session.createQuery("from Room u where u.adminId=? and u.state=?");
				query.setLong(0, roomId);
				query.setInteger(1, state);
				List<Room> list = query.list();
				return list;
			}
		});
	}

	public Room updateRoomByRoomId(final long roomId,final Integer state){
		return hibernateTemplate.executeWithNativeSession(new HibernateCallback<Room>() {
			@Override
			public Room doInHibernate(Session session)throws HibernateException, SQLException {
				Query query=session.createQuery("update Room u set u.state=? where u.id=?");
				query.setInteger(0, state);
				query.setLong(1, roomId);
				query.executeUpdate();
				return null;
			}
		});
	}

	public Room getRoomByRoomNum(final int roomNum){
		return hibernateTemplate.executeWithNativeSession(new HibernateCallback<Room>() {
			@Override
			public Room doInHibernate(Session session)throws HibernateException, SQLException {
				Query query=session.createQuery("from Room u where u.roomNum=? and u.state!=2 and u.state!=4");
				query.setLong(0, roomNum);
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

	public void delete(Room room){
		hibernateTemplate.delete(room);
	}
}
