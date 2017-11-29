package com.wasu.game.module.player.dao;

import com.wasu.game.domain.entity.OptionRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

/**
 * 玩家dao
 * @author -琴兽-
 *
 */
@Component
public class OptionRecordDao {
	
	@Autowired
	private HibernateTemplate hibernateTemplate;
	
	public OptionRecord getOptionRecordById(long id){
		return hibernateTemplate.get(OptionRecord.class, id);
	}

	public OptionRecord createOptionRecord(OptionRecord record){
		long id = (Long) hibernateTemplate.save(record);
		record.setId(id);
		return record;
	}
	
	public void updateOptionRecord(OptionRecord record){
		hibernateTemplate.update(record);
	}
}
