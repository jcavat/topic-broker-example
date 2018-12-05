package ch.hepia;

import org.apache.activemq.ActiveMQConnectionFactory;
 
import javax.jms.*;
import java.util.Random;
 
public class Client {
    private static int ackMode;
    private static String clientQueueName;
 
    private boolean transacted = false;
    private MessageProducer producer;
 
    static {
        clientQueueName = "client.messages";
        ackMode = Session.AUTO_ACKNOWLEDGE;
    }
 
    public Client() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://129.194.184.101:61616");
        try {
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(transacted, ackMode);
            Destination mainTopic = session.createTopic(clientQueueName);
 
            //Setup a message producer to send message to the queue the server is consuming from
            this.producer = session.createProducer(mainTopic);
            this.producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
 
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
            this.producer.send(txtMessage);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
 
 
    public static void main(String[] args) {
        new Client();
    }
}