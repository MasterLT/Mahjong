package com.wasu.game.module.player.dao;

import com.wasu.game.domain.entity.Record;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * 玩家dao
 * @author -琴兽-
 *
 */
@Component
public class RecordDao {
	
	@Autowired
	private HibernateTemplate hibernateTemplate;
	
	public Record getRecordById(int id){
		return hibernateTemplate.get(Record.class, id);
	}

	public List<Record> getRoomRecordByUserId(final long userId, final long roomId, final Integer type){
		return hibernateTemplate.execute(new HibernateCallback<List<Record>>() {
			@Override
			public List<Record> doInHibernate(Session session)throws HibernateException, SQLException {
				Query query=session.createQuery("from Record r where r.userId=? and r.roomId=? and r.type=? order by r.startTime");
				query.setLong(0, userId);
				query.setLong(1, roomId);
				query.setInteger(2,type);
				List<Record> list = query.list();
				if(list==null || list.isEmpty()){
					return null;
				}
				return list;
			}
		});
	}

	public List<Record> getLimitRoomRecordByUserId(final long userId, final Integer type, final Integer num){
		return hibernateTemplate.execute(new HibernateCallback<List<Record>>() {
			@Override
			public List<Record> doInHibernate(Session session)throws HibernateException, SQLException {
				Query query=session.createQuery("from Record r where r.userId=? and r.type=? order by r.startTime desc");
				query.setLong(0, userId);
				query.setLong(1, type);
				if (num!=null){
					query.setFirstResult(0);
					query.setMaxResults(num);
				}
				List<Record> list = query.list();
				if(list==null || list.isEmpty()){
					return null;
				}
				return list;
			}
		});
	}

	public List<Record> getRoomRecordByUserIdAndTime(final long userId, final Integer type, final Date time){
		return hibernateTemplate.execute(new HibernateCallback<List<Record>>() {
			@Override
			public List<Record> doInHibernate(Session session)throws HibernateException, SQLException {
				Query query=session.createQuery("from Record r where r.userId=? and r.type=? and r.startTime>? order by r.startTime desc");
				query.setLong(0, userId);
				query.setLong(1, type);
				query.setDate(2,time);
				List<Record> list = query.list();
				if(list==null || list.isEmpty()){
					return null;
				}
				return list;
			}
		});
	}

	public List<Record> getRoomRecordByUserId(final long userId, final long roomId, final Integer count, final Integer type){
		return hibernateTemplate.execute(new HibernateCallback<List<Record>>() {
			@Override
			public List<Record> doInHibernate(Session session)throws HibernateException, SQLException {
				Query query=session.createQuery("from Record r where r.userId=? and r.roomId=? and r.type=? and r.innings=?");
				query.setLong(0, userId);
				query.setLong(1, roomId);
				query.setInteger(2,type);
				query.setInteger(3,count);
				List<Record> list = query.list();
				if(list==null || list.isEmpty()){
					return null;
				}
				return list;
			}
		});
	}
	
	public List<Record> getRecordByRoomId(final long roomId, final Integer type){
		return hibernateTemplate.execute(new HibernateCallback<List<Record>>() {
			public List<Record> doInHibernate(Session session)throws HibernateException, SQLException {
				Query query=session.createQuery("from Record r where r.roomId=? and r.type=? order by r.startTime desc");
				query.setLong(0, roomId);
				query.setInteger(1,type);
				List<Record> list = query.list();
				if(list==null || list.isEmpty()){
					return null;
				}
				return list;
			}
		});
	}
	
	public Record createRecord(Record record){
		int id = (Integer) hibernateTemplate.save(record);
		record.setId(id);
		return record;
	}
	
	public void updateRecord(Record user){
		hibernateTemplate.update(user);
	}
}
