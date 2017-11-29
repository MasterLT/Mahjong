package com.wasu.game.activeMQ;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ActiveMqConfig {

	@Value("${mq.topick}")
	private String topick;

	public String getTopick() {
		return topick;
	}

	public void setTopick(String topick) {
		this.topick = topick;
	}
}
