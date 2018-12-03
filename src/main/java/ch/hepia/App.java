package ch.hepia;

import java.util.*;
import java.util.function.Consumer;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class App {
    public static void main(String... args) {
        Producer c = new Producer();
        c.init();
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

        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
            c.send("Coucou " + i);
        }
        c.close();
        cs.close();
        cs2.close();
    }
}

class TopicConsumer extends Communicator {

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

class Producer extends Communicator {
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

abstract class Communicator {
    Optional<Session> session = Optional.empty();
    Optional<Connection> connection = Optional.empty();

    public Communicator() {
    }

    public void init() {
        try {
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");

            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            this.connection = Optional.of(connection);
            connection.start();

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            this.session = Optional.of(session);

            // Create the destination (Topic or Queue)
            Destination destination = session.createTopic("TOPIC");

            _init(destination);

        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

    public abstract void _init(Destination destination);

    public void close() {
        // Clean up
        session.ifPresent(s -> {
            try {
                s.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
        connection.ifPresent(c -> {
            try {
                c.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
    }
}