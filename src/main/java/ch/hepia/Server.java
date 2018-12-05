package ch.hepia;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.ActiveMQConnectionFactory;
 
import javax.jms.*;
 
public class Server implements MessageListener {
    private static int ackMode;
    private static String mainTopic;
    private static String messageBrokerUrl;
 
    private Session session;
    private boolean transacted = false;
    //private MessageProducer replyProducer;
 
    static {
        messageBrokerUrl = "tcp://localhost:61616";
        mainTopic = "client.messages";
        ackMode = Session.AUTO_ACKNOWLEDGE;
    }
 
    public Server() {
        try {
            //This message broker is embedded
            BrokerService broker = new BrokerService();
            broker.setPersistent(false);
            broker.setUseJmx(false);
            broker.addConnector(messageBrokerUrl);
            broker.start();

            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(messageBrokerUrl);
            Connection connection = connectionFactory.createConnection();
            connection.start();
            this.session = connection.createSession(this.transacted, ackMode);
            Destination adminQueue = this.session.createTopic(mainTopic);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    /*
    private void setupMessageQueueConsumer() {
        try {
        } catch (JMSException e) {
            //Handle the exception appropriately
        }
    }
    */
 
    public void onMessage(Message message) {
        try {
            TextMessage response = this.session.createTextMessage();
            if (message instanceof TextMessage) {
                TextMessage txtMsg = (TextMessage) message;
                String messageText = txtMsg.getText();
                response.setText(messageText + " COUCOU");
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
 
    public static void main(String[] args) {
        new Server();
    }
}