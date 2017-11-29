package com.wasu.game.quarz.job;

import com.wasu.game.module.CardCmd;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.wasu.game.module.ModuleId;
import com.wasu.game.module.PlayerCmd;
import com.wasu.game.quarz.QuarzManager;
import com.wasu.game.scanner.Invoker;
import com.wasu.game.scanner.InvokerHoler;

public class PlayJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		try {
			
			JobDataMap jobData=context.getJobDetail().getJobDataMap();
			int position= jobData.getInt("jobName");
			long roomId= jobData.getLong("groupName");
			System.out.println("============position:"+position+"  roomId:"+roomId+"============");
			//删除当前job
			QuarzManager.deleteJob(roomId,PlayJob.class);
			
			//强制出牌
    		Invoker invoker = InvokerHoler.getInvoker(ModuleId.PLAYER, CardCmd.AUTOCHUPAI);
    		if(invoker != null){
    			invoker.invoke(roomId,position);
    		}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
