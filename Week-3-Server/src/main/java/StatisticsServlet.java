import com.google.gson.Gson;
import entity.*;
import entity.Error;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "StatisticsServlet", value = "/StatisticsServlet")
public class StatisticsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        List<Endpoint> endpointStats = new ArrayList<>();
        endpointStats.add(new Endpoint("/resorts", "GET", 11, 198));
        endpointStats.add(new Endpoint("/resorts/add", "POST", 21, 98));

        PrintWriter out = res.getWriter();
        res.setStatus(HttpServletResponse.SC_OK);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        out.print(new Gson().toJson(new Statistic(endpointStats)));
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
