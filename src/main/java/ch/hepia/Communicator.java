package ch.hepia;
import java.util.*;
import java.util.function.Consumer;
import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public abstract class Communicator {
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