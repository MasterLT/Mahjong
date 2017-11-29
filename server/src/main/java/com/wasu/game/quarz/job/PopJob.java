package com.wasu.game.quarz.job;

import com.wasu.game.domain.PlayerST;
import com.wasu.game.enums.CardType;
import com.wasu.game.module.player.core.LinkedHandler;
import com.wasu.game.module.player.core.LinkedHandlerImpl;
import com.wasu.game.quarz.QuarzManager;
import com.wasu.game.service.RoomService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

public class PopJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		try {
			
			JobDataMap jobData=context.getJobDetail().getJobDataMap();
			int position= jobData.getInt("jobName");
			long roomId= jobData.getLong("groupName");
			System.out.println("============position:"+position+"  roomId:"+roomId+"============");
			//删除当前job
			QuarzManager.deleteJob(roomId,PopJob.class);
			//设置出牌
            PlayerST playerST= RoomService.getRoom(roomId).getPlayerByPosition(RoomService.getRoom(roomId).getCurrent());
            List<Integer> cardList=playerST.getCardsByType(CardType.SHOUPAI.getType());
            int outCard=cardList.get(cardList.size()-1);
			//出牌
			LinkedHandler linkedHandler= LinkedHandlerImpl.build();
			linkedHandler.pop(roomId,outCard);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
