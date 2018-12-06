package ch.hepia;

import java.util.ArrayList;
import java.util.Optional;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Client {
    private static int ackMode;
    private static String clientQueueName;
    private static String messageBrokerUrl;

    private boolean transacted = false;

    private Optional<Session> maybeSession;
    private Optional<MessageProducer> maybeProducer;

    static {
        messageBrokerUrl = "tcp://localhost:61616";
        clientQueueName = "client.messages";
        ackMode = Session.AUTO_ACKNOWLEDGE;
    }

    public Client() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(messageBrokerUrl);
        try {
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(transacted, ackMode);
            maybeSession = Optional.of(session);

            Destination mainTopic = session.createTopic(clientQueueName);

            //Setup a message producer to send message to the queue the server is consuming from
            MessageProducer producer = session.createProducer(mainTopic);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            maybeProducer = Optional.of(producer);

            MessageConsumer responseConsumer = session.createConsumer(mainTopic);

            responseConsumer.setMessageListener(new MessageListener() {
                public void onMessage(Message message) {
                    try {
                        if (message instanceof TextMessage) {
                            TextMessage textMessage = (TextMessage) message;
                            String messageText = textMessage.getText();
                            System.out.println("Received string: " + messageText);
                        }
                        if (message instanceof ObjectMessage) {
                            ObjectMessage objMessage = (ObjectMessage) message;
                            Object obj = objMessage.getObject();
                            System.out.println("Received obj: " + obj);
                        }
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });

            TextMessage txtMessage = session.createTextMessage("Test");
            producer.send(txtMessage);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void send(String msg) {
        maybeProducer.ifPresent(p -> maybeSession.ifPresent(s -> {
            try {
                TextMessage txt = s.createTextMessage(msg);
                p.send(txt);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }));
    }

    public void send(House house) {
        maybeProducer.ifPresent(p -> maybeSession.ifPresent(s -> {
            try {
                ObjectMessage obj = s.createObjectMessage(house);
                p.send(obj);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }));
    }

    public static void main(String[] args) {
        Client c = new Client();
        c.send("COUCOU");

        ArrayList<String> names = new ArrayList<>();
        names.add("Bob");
        names.add("Fred");
        names.add("Max");
        House myHouse = new House(names);

        c.send(myHouse);
    }
}
