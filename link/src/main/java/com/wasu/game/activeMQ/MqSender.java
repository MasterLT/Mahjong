package com.wasu.game.activeMQ;

import com.wasu.game.domain.Result;
import com.wasu.game.proto.PlayerModule;
import com.wasu.game.proto.PlayerModule.UserResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MqSender {
	
//	@Autowired
//	static MQProductor mQProductor;
	
	public static void main(String[] args) throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		 
//		PooledConnectionFactory pool = applicationContext.getBean(PooledConnectionFactory.class);
//		Connection con = pool.createConnection();
//		con.start();
//		Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
//		Topic topic = session.createTopic("mahjong");
//		MessageConsumer consumer = session.createConsumer(topic,"JMSType = 'room2'");
//		consumer.setMessageListener(new MessageListener() {
//			public void onMessage(Message message) {
//				TextMessage tm = (TextMessage) message;
//				try {
//					System.out.println("收到: " + tm.getText());
//				} catch (JMSException e) {
//					e.printStackTrace();
//				}
//			}
//		});
		
//		JmsTemplate jmsTemplate = applicationContext.getBean(JmsTemplate.class);
//		 Destination destination =(Destination)applicationContext.getBean("topicDestination");
//		 Destination destination =new ActiveMQTopic("litao");
//		 jmsTemplate.send(destination, new MessageCreator() {
//		 public Message createMessage(Session session) throws JMSException {
//			 TextMessage tx=session.createTextMessage("room2发送消息：Hello ActiveMQ！");
//			 tx.setJMSType("room2");
//			 return tx;
//		 	}
//		 });
//		MessageProducer producer=session.createProducer(topic);
//		TextMessage tx=session.createTextMessage("room2发送消息：Hello ActiveMQ！");
//		tx.setJMSType("room2");
//		producer.send(topic, tx);
		UserResponse userResponse = PlayerModule.UserResponse.newBuilder()
				.setId(1)
				.setWId("hahah")
				.setDrawCount(1)
				.setFleeCount(1)
				.setLostCount(1)
				.setWinCount(1)
				.setLevel(1)
				.setState(0)
				.setExp(1).build();
		MQProductor mQProductor=applicationContext.getBean(MQProductor.class);
		mQProductor.send(Result.SUCCESS(userResponse), JmsTypeEnum.REQUEST);
	}
}
