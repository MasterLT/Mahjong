package com.wasu.game.quarz.job;

import com.wasu.game.module.ModuleId;
import com.wasu.game.module.PlayerCmd;
import com.wasu.game.quarz.QuarzManager;
import com.wasu.game.scanner.Invoker;
import com.wasu.game.scanner.InvokerHoler;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class EatJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		try {
			
			JobDataMap jobData=context.getJobDetail().getJobDataMap();
			int position= jobData.getInt("jobName");
			long roomId= jobData.getLong("groupName");
			System.out.println("============position:"+position+"  roomId:"+roomId+"============");
			//删除当前job
			QuarzManager.deleteJob(roomId,EatJob.class);
			
			//强制抓
    		Invoker invoker = InvokerHoler.getInvoker(ModuleId.PLAYER, PlayerCmd.ZHUAPAI);
    		if(invoker != null){
    			invoker.invoke(roomId);
    		}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
