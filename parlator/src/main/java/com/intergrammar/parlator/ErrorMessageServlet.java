package com.intergrammar.parlator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class ErrorMessageServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.io.IOException {
        PrintWriter pw = new PrintWriter(response.getOutputStream());

        String queryString = request.getQueryString();
        pw.write(request.getSession().getAttribute(queryString).toString());
        pw.flush();
        pw.close();
    }
}
