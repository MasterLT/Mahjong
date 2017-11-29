package com.wasu.game.quarz.job;

import com.wasu.game.quarz.QuarzManager;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class PengJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		try {
			
			JobDataMap jobData=context.getJobDetail().getJobDataMap();
			int position= jobData.getInt("jobName");
			long roomId= jobData.getLong("groupName");
			System.out.println("============position:"+position+"  roomId:"+roomId+"============");
			//删除当前job
			QuarzManager.deleteJob(roomId,PengJob.class);

			//没人碰,判断吃
//			Request request=Request.valueOf(ModuleId.PLAYER, CardCmd.CANEAT, JSONObject.parseObject("{'roomId':"+roomId+"}"));
//    		Invoker invoker = InvokerHoler.getInvoker(ModuleId.PLAYER, CardCmd.CANEAT);
//    		if(invoker != null){
//    			invoker.invoke(request);
//    		}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
