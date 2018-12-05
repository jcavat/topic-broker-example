package ch.hepia;

import java.util.*;
import java.util.function.Consumer;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class AppConsumer {
    public static void main(String... args) {
        TopicConsumer cs = new TopicConsumer((Message message) -> {
            try {
                if (message instanceof TextMessage) {
                    System.out.println("-- consume: " + ((TextMessage) message).getText());
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
        cs.init();
        TopicConsumer cs2 = new TopicConsumer((Message message) -> {
            try {
                if (message instanceof TextMessage) {
                    System.out.println("-- eat: " + ((TextMessage) message).getText());
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
        cs2.init();

    }
}



