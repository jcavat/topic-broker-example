package ch.hepia;
import java.util.*;
import java.util.function.Consumer;
import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class TopicProducer extends Communicator {
    private Optional<MessageProducer> producer = Optional.empty();

    public void _init(Destination destination) {
        this.producer = session.map(s -> {
            try {
                MessageProducer p = s.createProducer(destination);
                p.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
                return p;
            } catch (JMSException e) {
                e.printStackTrace();
                return null;
            }
        }).filter(p -> p != null);
    }

    public void send(String text) {
        producer.ifPresent(p -> session.ifPresent(s -> {
            try {
                TextMessage txt = s.createTextMessage(text);
                p.send(txt);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }));
    }
}