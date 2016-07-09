package com.intergrammar.parlator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.Map;

public class ErrorMessageServlet extends HttpServlet {

    private final SessionHandler sessionHandler;

    public ErrorMessageServlet() {
        ApplicationContext context = ApplicationContext.getInstance();
        sessionHandler = context.sessionHandler();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.io.IOException {
        PrintWriter pw = new PrintWriter(response.getOutputStream());
        String queryString = request.getQueryString();
        HttpSession session = request.getSession();
        Map<String,String> errorMessageMap = sessionHandler.getErrorMessageMap(session);
        pw.write(errorMessageMap.get(queryString));
        pw.flush();
        pw.close();
    }
}
