package ch.hepia;

import org.apache.activemq.ActiveMQConnectionFactory;
import java.util.Optional; 
import javax.jms.*;
import java.util.Random;
 
public class Client {
    private static int ackMode;
    private static String clientQueueName;
 
    private boolean transacted = false;
    private MessageProducer producer;

    private Optional<Session> maybeSession = Optional.empty();
    private Optional<MessageProducer> maybeProducer = Optional.empty();

 
    static {
        clientQueueName = "client.messages";
        ackMode = Session.AUTO_ACKNOWLEDGE;
    }
 
    public Client() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        try {
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(transacted, ackMode);
            maybeSession = maybeSession.of(session);

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
                            System.out.println("Received: " + messageText);
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
 
 
    public static void main(String[] args) {
        Client c = new Client();
        c.send("COUCOU");
    }
}