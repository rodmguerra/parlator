package com.intergrammar.parlator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interlinguatts.Voice;
import com.interlinguatts.VoiceProvider;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class VoicesServlet extends HttpServlet {

    private VoiceProvider voiceProvider;
    {
        try {
            voiceProvider = (VoiceProvider) Class.forName("com.interlinguatts.ivona.IvonaConnector").newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");
        String recomendedString = request.getParameter("recommended");
        boolean recommended = recomendedString == null? true : Boolean.valueOf(recomendedString);
        List<Voice> voices = recommended? voiceProvider.getGoodVoicesForInterlingua() : voiceProvider.getVoices();
        PrintWriter writer = new PrintWriter(response.getOutputStream());
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(voices);
        System.out.println(objectMapper.writeValueAsString(voices));
        writer.print(objectMapper.writeValueAsString(voices));
        writer.flush();
    }
}
