package ch.hepia;
import java.util.*;
import java.util.function.Consumer;
import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class TopicConsumer extends Communicator {

    private Consumer<Message> consumer;

    public TopicConsumer(Consumer<Message> consumer) {
        this.consumer = consumer;
    }

    public void _init(Destination destination) {
        session.map(s -> {
            try {
                MessageConsumer mc = s.createConsumer(destination);
                mc.setMessageListener(new MessageListener() {
                    public void onMessage(Message message) {
                        consumer.accept(message);
                    }
                });
                return mc;
            } catch (JMSException e) {
                e.printStackTrace();
                return null;
            }
        });
    }
}