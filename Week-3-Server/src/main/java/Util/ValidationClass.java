package Util;

import com.google.gson.Gson;
import entity.Error;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ValidationClass {

    public void throwResponse(int status, String message) throws IOException, Error {
        throw new Error(status, message);
    }

    public void validateUrlPath(HttpServletResponse response, String urlPath) throws IOException {
        // check we have a URL!
        if (urlPath == null || urlPath.isEmpty()) {
            try {
                throwResponse(HttpServletResponse.SC_BAD_REQUEST, "Missing Parameters in the URL");
            } catch (Error e) {
                PrintWriter out = response.getWriter();
                response.setStatus(e.getErrorCode());
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                out.print(new Gson().toJson(e));
                out.flush();
            }
            return;
        }
    }

    public void validateParameter(String string, Integer maxValue) throws IOException, Error {
        if (string == null || string.isEmpty()) {
            throwResponse(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL parameter!");
        }
        try {
            if (Integer.parseInt(string) <= 0 || Integer.parseInt(string) >= maxValue) {
                throwResponse(HttpServletResponse.SC_NOT_FOUND, "Data not found!");
            }
        } catch (NumberFormatException e) {
            throwResponse(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL parameter!");
        }
    }

    public void validateUrl(String string, String requiredString) throws IOException, Error {
        if (!requiredString.equals(string)) {
            throwResponse(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL pattern!");
        }
    }
}
