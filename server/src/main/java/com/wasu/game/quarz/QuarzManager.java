package com.wasu.game.quarz;

import java.util.Date;

import com.wasu.game.service.RoomService;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class QuarzManager {


	private static Logger logger = Logger.getLogger(QuarzManager.class);
	private static SchedulerFactory sf;
	private static Scheduler sched;
	public static final int outCardWaitTime=10;
	public static final int tipsWaitTime=4;
	public static final int tingTime=1;

	public static Scheduler buildScheduler(){
		if(sched==null){
			try {
				sf = new StdSchedulerFactory();
				sched = sf.getScheduler();
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}
		return sched;
	}

	public static void addJob(long roomId, int waitTime,Class clazz) {
		if(true)
			return;
		try {
			String jobName= RoomService.getRoom(roomId).getCurrent()+"";
			String groupName=roomId+"";
			// 通过过JobDetail封装HelloJob，同时指定Job在Scheduler中所属组及名称，这里，组名为group1，而名称为job1。
			JobDetail job = JobBuilder.newJob(clazz).withIdentity(jobName, groupName).build();

			job.getJobDataMap().put("jobName", jobName);
			job.getJobDataMap().put("groupName", roomId);

			// 从当前时间起15秒后
//			String time = new Date().getSeconds()==0?59+"":(new Date().getSeconds() - 1) + "/"+waitTime+" * * * * ?";
//			String time = (new Date().getSeconds()+waitTime)%60 + "/"+waitTime+" * * * * ?";
//			System.out.println("==================================="+time+"===================");
//
//			// 表达式调度构建器
//			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(time);

			// 创建一个SimpleTrigger实例，指定该Trigger在Scheduler中所属组及名称。
			// 接着设置调度的时间规则.当前时间运行
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity(groupName+jobName, groupName)
//					.withSchedule(scheduleBuilder).startAt(new Date())
					.withSchedule(SimpleScheduleBuilder.repeatSecondlyForTotalCount(1, waitTime)).startAt(new Date(new Date().getTime()+waitTime*1000)).build();

			// 注册并进行调度
			QuarzManager.buildScheduler().scheduleJob(job, trigger);

		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void deleteJob(long roomId,Class clazz) {
		if(true)
			return;
		try{
			JobKey jobKey = JobKey.jobKey(RoomService.getRoom(roomId).getCurrent()+"", roomId+"");
			logger.info("remove job "+clazz.getName()+" :"+QuarzManager.buildScheduler().deleteJob(jobKey));
		}catch(Exception e){
//			e.printStackTrace();
			logger.info("定时器为空");
		}
	}

}
