package com.wasu.game.quarz.job;

import com.wasu.game.module.player.core.LinkedHandler;
import com.wasu.game.module.player.core.LinkedHandlerImpl;
import com.wasu.game.quarz.QuarzManager;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class PushJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		try {
			
			JobDataMap jobData=context.getJobDetail().getJobDataMap();
			int position= jobData.getInt("jobName");
			long roomId= jobData.getLong("groupName");
			System.out.println("============position:"+position+"  roomId:"+roomId+"============");
			//删除当前job
			QuarzManager.deleteJob(roomId,PushJob.class);
			//抓牌
			LinkedHandler linkedHandler= LinkedHandlerImpl.build();
			linkedHandler.push(roomId);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
