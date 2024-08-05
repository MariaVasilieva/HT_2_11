package org.example.servlet;

import jakarta.servlet.http.Cookie;
import org.thymeleaf.context.Context;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


@WebServlet (value = "/time")
public class TimeServlet extends HttpServlet {
    private ThymeleafConfig thymeleafConfig;
    public void init() throws ServletException {
        thymeleafConfig = new ThymeleafConfig();
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String timeZone = req.getParameter("timezone");
        if (timeZone == null || timeZone.isEmpty()) {
            timeZone = getTimezoneFromCookie(req);
        } else {
            timeZone = timeZone.replace(" ", "+");
            saveTimezoneToCookie(resp, timeZone);
        }
        if (timeZone == null || timeZone.isEmpty()) {
            timeZone = "UTC";
        }
        ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.of(timeZone));
        String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Context context = new Context();
        context.setVariable("timeZone", timeZone);
        context.setVariable("currentTime", formattedTime);

        thymeleafConfig.process("index", context, resp);
    }
    private void saveTimezoneToCookie(HttpServletResponse response, String timezone) {
        Cookie cookie = new Cookie("lastTimezone", timezone);
        cookie.setMaxAge(60 * 60 * 24);
        response.addCookie(cookie);
    }

    private String getTimezoneFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("lastTimezone".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
