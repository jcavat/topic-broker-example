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
 
            //Setup a message producer to respond to messages from clients, we will get the destination
            //to send to from the JMSReplyTo header field from a Message
            //this.replyProducer = this.session.createProducer(null);
            //this.replyProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
 
            //Set up a consumer to consume messages off of the admin queue
            //MessageConsumer consumer = this.session.createConsumer(adminQueue);

            //consumer.setMessageListener(this);

        } catch (Exception e) {
            //Handle the exception appropriately
        }
 
        //Delegating the handling of messages to another class, instantiate it before setting up JMS so it
        //is ready to handle messages
        //this.setupMessageQueueConsumer();
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
 
            //Send the response to the Destination specified by the JMSReplyTo field of the received message,
            //this is presumably a temporary queue created by the client
            //this.replyProducer.send(message.getJMSReplyTo(), response);
        } catch (JMSException e) {
            //Handle the exception appropriately
        }
    }
 
    public static void main(String[] args) {
        new Server();
    }
}