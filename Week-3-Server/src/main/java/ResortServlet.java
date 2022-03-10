import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Util.ValidationClass;
import com.google.gson.Gson;
import entity.*;
import entity.Error;

@WebServlet(name = "ResortServlet", value = "/ResortServlet")
public class ResortServlet extends HttpServlet {

    ValidationClass validationClass = new ValidationClass();

    private void isValidUrlPathForSeasons(HttpServletResponse response, String[] urlPath) throws IOException, Error {
        validationClass.validateParameter(urlPath[1], Integer.MAX_VALUE);
        validationClass.validateUrl(urlPath[2], "seasons");
    }

    private void isValidUrlPathForSkiers(HttpServletResponse response, String[] urlPath) throws IOException, Error {
        validationClass.validateParameter(urlPath[1], Integer.MAX_VALUE);
        validationClass.validateUrl(urlPath[2], "seasons");
        validationClass.validateParameter(urlPath[3], Integer.MAX_VALUE);
        validationClass.validateUrl(urlPath[4], "day");
        validationClass.validateParameter(urlPath[5], Integer.MAX_VALUE);
        validationClass.validateUrl(urlPath[6], "skiers");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/plain");
        String url = req.getPathInfo();

        if (url == null) {
            List<Resort> resorts = new ArrayList<>();
            resorts.add(new Resort("resort-1", 10));
            resorts.add(new Resort("resort-2", 20));

            PrintWriter out = res.getWriter();
            res.setStatus(HttpServletResponse.SC_OK);
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            out.print(new Gson().toJson(new Skier(resorts)));
            out.flush();
        } else {
            try {
                validationClass.validateUrlPath(res, url);
                String[] urlPath = url.split("/");

                System.out.println(urlPath.length);
                if (urlPath.length == 3) {
                    isValidUrlPathForSeasons(res, urlPath);

                    PrintWriter out = res.getWriter();
                    res.setStatus(HttpServletResponse.SC_OK);
                    res.setContentType("application/json");
                    res.setCharacterEncoding("UTF-8");
                    out.print(new Gson().toJson(new SeasonsByResort(Arrays.asList("season-1", "season-2"))));
                    out.flush();
                } else if (urlPath.length == 7) {
                    isValidUrlPathForSkiers(res, urlPath);

                    PrintWriter out = res.getWriter();
                    res.setStatus(HttpServletResponse.SC_OK);
                    res.setContentType("application/json");
                    res.setCharacterEncoding("UTF-8");
                    out.print(new Gson().toJson(new SkierDetails("Mission Ridge", 78999)));
                    out.flush();
                } else {
                    validationClass.throwResponse(HttpServletResponse.SC_NOT_FOUND, "Invalid URL pattern!");
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

    }

    private void validateYearValue(Year year) throws Error, IOException {
        if (year.getYear() <= 0 || year.getYear() >= Integer.MAX_VALUE) {
            validationClass.throwResponse(HttpServletResponse.SC_BAD_REQUEST, "Invalid value of year!");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/plain");
        String urlPath = req.getPathInfo();

        // check we have a URL!
        validationClass.validateUrlPath(res, urlPath);

        StringBuilder sb = new StringBuilder();
        String request;
        while ((request = req.getReader().readLine()) != null) {
            sb.append(request);
        }

        try {
            isValidUrlPathForSeasons(res, urlPath.split("/"));

            Year year = (Year) new Gson().fromJson(sb.toString(), Year.class);
            validateYearValue(year);

            res.setStatus(HttpServletResponse.SC_CREATED);
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
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
