import com.rabbitmq.client.*;
import entity.Season;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

public class Consumer {

    private static Map<String, Season> map = new ConcurrentHashMap<>();
    private static int parseInt(String s) {
        return Integer.parseInt(s);
    }
    private static void retrieveMessageFromServer() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare("Lift Ride", false, false, false, null);
            channel.basicConsume("Lift Ride", true, (consumerTag, m) -> {
                final String[] message = new String(m.getBody(), "UTF-8").split("/");
                Season season = new Season(parseInt(message[1]), parseInt(message[2]), parseInt(message[3]));
                map.put(message[0], season);
                System.out.println("receive messages");
            }, consumerTag -> {});
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    public static void main (String[] args) {
        int numThreads = 10;
        if (args.length != 0) {
            numThreads = Integer.parseInt(args[0]);
        }
        for (int i = 0; i < numThreads; i++) {
            Runnable thread = () -> {
                retrieveMessageFromServer();
            };
            new Thread(thread).start();
        }
    }
}
