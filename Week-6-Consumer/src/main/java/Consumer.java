import com.rabbitmq.client.*;

import entity.LiftRide;
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
        factory.setHost("54.225.153.29");
        factory.setUsername("test");
        factory.setPassword("test");
        factory.setPort(5672);
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare("lab7", false, false, false, null);
            channel.basicConsume("lab7", true, (consumerTag, m) -> {
                final String[] message = new String(m.getBody(), "UTF-8").split("/");
                Season season = new Season(parseInt(message[1]), parseInt(message[2]), parseInt(message[3]));
                map.put(message[0], season);
//                LiftRideDao liftRideDao = new LiftRideDao();
//                liftRideDao.createLiftRide(new LiftRide(10, 2, 3, 5, 500, 20));
                System.out.println("receive messages");
            }, consumerTag -> {});
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }


    private static void retrieveLiftRideFromServer() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("54.225.153.29");
        factory.setUsername("test");
        factory.setPassword("test");
        factory.setPort(5672);
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare("lab7", false, false, false, null);
            channel.basicConsume("lab7", true, (consumerTag, m) -> {
                final String[] message = new String(m.getBody(), "UTF-8").split("/");
                LiftRide liftRide = new LiftRide(parseInt(message[0]),
                        parseInt(message[1]),
                        parseInt(message[2]),
                        parseInt(message[3]),
                        parseInt(message[4]),
                        parseInt(message[5]));
                LiftRideDao liftRideDao = new LiftRideDao();
                liftRideDao.createLiftRide(liftRide);
                System.out.println("stored data to database!");
            }, consumerTag -> {
            });
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
                retrieveLiftRideFromServer();
//                retrieveMessageFromServer();
            };
            new Thread(thread).start();
        }
//        LiftRideDao liftRideDao = new LiftRideDao();
//        liftRideDao.createLiftRide(new LiftRide(10, 2, 3, 5, 500, 20));
    }
}
