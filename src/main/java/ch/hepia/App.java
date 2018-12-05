package ch.hepia;

import java.util.*;
import java.util.function.Consumer;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class App {
    public static void main(String... args) {
        TopicProducer c = new TopicProducer();
        c.init();
        for (int i = 0; i < 10; i++) {
            System.out.println("Test");
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
            c.send("Coucou " + i);
        }
        c.close();
    }
}
