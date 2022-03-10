import Util.ValidationClass;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import entity.Error;
import entity.LiftRide;
import entity.Resort;
import entity.Season;
import entity.Skier;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@WebServlet(name = "SkierServlet", value = "/SkierServlet")
public class SkierServlet extends HttpServlet {

    ValidationClass validationClass = new ValidationClass();

    private void isUrlValidForSkier(HttpServletResponse response, String[] urlPath) throws IOException, Error {
        if (urlPath.length > 8) {
            validationClass.throwResponse(400, "Invalid URL parameter!");
        }
        validationClass.validateParameter(urlPath[1], Integer.MAX_VALUE);
        validationClass.validateUrl(urlPath[2], "seasons");
        validationClass.validateParameter(urlPath[3], Integer.MAX_VALUE);
        validationClass.validateUrl(urlPath[4], "days");
        validationClass.validateParameter(urlPath[5], 366);
        validationClass.validateUrl(urlPath[6], "skiers");
        validationClass.validateParameter(urlPath[7], Integer.MAX_VALUE);
    }

    private void isUrlValidForResort(HttpServletResponse response, String[] urlPath) throws IOException, Error {
        validationClass.validateParameter(urlPath[1], Integer.MAX_VALUE);
        validationClass.validateUrl(urlPath[2], "vertical");
    }

    private void validateSeasonValues(Season season) throws Error, IOException {
        if (season.getTime() <= 0 || season.getTime() >= Integer.MAX_VALUE) {
            validationClass.throwResponse(HttpServletResponse.SC_BAD_REQUEST, "Invalid value of time!");
        }
        if (season.getLiftID() <= 0 || season.getLiftID() >= Integer.MAX_VALUE) {
            validationClass.throwResponse(HttpServletResponse.SC_BAD_REQUEST, "Invalid value of liftID!");
        }
        if (season.getWaitTime() <= 0 || season.getWaitTime() >= Integer.MAX_VALUE) {
            validationClass.throwResponse(HttpServletResponse.SC_BAD_REQUEST, "Invalid value of waitTime!");
        }
    }

    private void sendMessageToConsumer(String skierId, Season season, ConnectionFactory factory) throws Error, IOException {

        try (Connection connection = factory.newConnection()) {
            Channel channel = connection.createChannel();
            channel.queueDeclare("Lift Ride", false, false, false, null);
            String message = String.format("%s/%d/%d/%d", skierId, season.getLiftID(), season.getTime(), season.getWaitTime());
            channel.basicPublish("", "Lift Ride", null, message.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            validationClass.throwResponse(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (TimeoutException e) {
            validationClass.throwResponse(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private void sendLiftRideToConsumer(LiftRide liftRide,
                                        ConnectionFactory factory) throws Error, IOException {

//        factory.setPort(5672);
//        factory.setUsername("admin");
//        factory.setPassword("admin");
        try (Connection connection = factory.newConnection()) {
            Channel channel = connection.createChannel();
            channel.queueDeclare("lab7", false, false, false, null);
            String message = String.format("%d/%d/%d/%d/%d/%d", liftRide.getSkierId(), liftRide.getResortId(),
                    liftRide.getSeasonId(), liftRide.getDayId(),liftRide.getTime(), liftRide.getLiftId());
            System.out.println(message);
            channel.basicPublish("", "lab7", null, message.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            validationClass.throwResponse(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (TimeoutException e) {
            validationClass.throwResponse(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/plain");
        String urlPath = req.getPathInfo();

        // check we have a URL!
        validationClass.validateUrlPath(res, urlPath);
        try {
            if (urlPath.contains("vertical")) {
                isUrlValidForResort(res, urlPath.split("/"));

                List<Resort> resorts = new ArrayList<>();
                resorts.add(new Resort("resort-1", 10));
                resorts.add(new Resort("resort-2", 20));
                Skier skier = new Skier(resorts);

                PrintWriter out = res.getWriter();
                res.setStatus(HttpServletResponse.SC_OK);
                res.setContentType("application/json");
                res.setCharacterEncoding("UTF-8");
                out.print(new Gson().toJson(skier));
                out.flush();
            } else {
                isUrlValidForSkier(res, urlPath.split("/"));
                PrintWriter out = res.getWriter();
                res.setStatus(HttpServletResponse.SC_OK);
                res.setContentType("application/json");
                res.setCharacterEncoding("UTF-8");
                out.print(new Gson().toJson(9));
                out.flush();
            }
        } catch (Error e) {
            PrintWriter out = res.getWriter();
            res.setStatus(e.getErrorCode());
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            out.print(new Gson().toJson(e));
            out.flush();
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/plain");
        String urlPath = req.getPathInfo();

        // check we have a URL!
        validationClass.validateUrlPath(res, urlPath);

        System.out.println(urlPath);
        StringBuilder sb = new StringBuilder();
        String request;
        while ((request = req.getReader().readLine()) != null) {
            sb.append(request);
        }

        try {
            String[] urlPathArray = urlPath.split("/");
            isUrlValidForSkier(res, urlPathArray);
//            String skierId = urlPathArray[urlPathArray.length - 1];

            LiftRide liftRide = new Gson().fromJson(sb.toString(), LiftRide.class);
//            Season season = new Gson().fromJson(sb.toString(), Season.class);
            System.out.println(sb.toString());
            System.out.println(liftRide.getLiftId());
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("54.225.153.29");
            factory.setUsername("test");
            factory.setPassword("test");
            factory.setPort(5672);

//            validateSeasonValues(season);
//            sendMessageToConsumer(skierId, season, factory);
            sendLiftRideToConsumer(liftRide, factory);

            res.setStatus(HttpServletResponse.SC_CREATED);
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            res.getOutputStream().print("Data has been updated!");
        } catch (Error e) {
            PrintWriter out = res.getWriter();
            res.setStatus(e.getErrorCode());
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            out.print(new Gson().toJson(e));
            out.flush();
        }
    }
}