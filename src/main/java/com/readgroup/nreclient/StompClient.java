package com.readgroup.nreclient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import javax.jms.*;

public class StompClient  implements Runnable {

    private static final Logger logger = LogManager.getLogger();


    public static void main(String[] args) throws Exception {

        new StompClient().run();
    }

    @Override
    public void run() {
        String brokerUri = PropertiesCache.getInstance().getProperty("com.readgroup.nreclient.nredata.brokerUri");
        String queueName = PropertiesCache.getInstance().getProperty("com.readgroup.nreclient.nredata.queueName");
        String awsQueueName = PropertiesCache.getInstance().getProperty("com.readgroup.aws.queueName");

        String region = PropertiesCache.getInstance().getProperty("com.readgroup.aws.region");
        String vpcEndpoint = PropertiesCache.getInstance().getProperty("com.readgroup.aws.sqs.endpoint");

        QueueUtils queueUtils = new QueueUtils(awsQueueName, vpcEndpoint, region);

        StompJmsConnectionFactory connectionFactory = new StompJmsConnectionFactory();
        connectionFactory.setBrokerURI(brokerUri);

        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;

        try {
            connection = connectionFactory.createConnection("KBc36469e5-929c-4a78-8362-182c00af27dd", "3d2149b8-ba91-48fc-bec9-55343a439969");
            connection.start();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(queueName);
            consumer = session.createConsumer(topic);

            logger.info("Connected to STOMP " + brokerUri);

            consumer.setMessageListener(new MessageHandler(queueUtils));

            while (!Thread.interrupted()) {}

            try {
                if (consumer != null) {
                    consumer.close();
                }

                if (session != null) {
                    session.close();
                }

                if (connection != null) {
                    connection.close();
                    connection = null;
                }
            } catch (JMSException ex) {
                logger.error(ex.getMessage());
                ex.printStackTrace();
            }

            logger.warn("Thread was interrupted!");


        } catch (JMSException e) {
            logger.error(e.getMessage());
        }
    }
}
