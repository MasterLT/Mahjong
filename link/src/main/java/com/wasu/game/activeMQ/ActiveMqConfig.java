package com.wasu.game.activeMQ;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ActiveMqConfig {

	@Value("${mq.queue}")
	private String queue;

	public String getQueue() {
		return queue;
	}

	public void setQueue(String queue) {
		this.queue = queue;
	}
}
