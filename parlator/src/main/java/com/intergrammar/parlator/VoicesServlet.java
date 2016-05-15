package com.intergrammar.parlator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interlinguatts.IvonaFacade;
import com.ivona.services.tts.model.Voice;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VoicesServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        IvonaFacade ivonaFacade = new IvonaFacade();

        response.setContentType("application/json");
        String recomendedString = request.getParameter("recommended");
        boolean recommended = recomendedString == null? true : Boolean.valueOf(recomendedString);
        List<String> recommendedVoiceNames = Arrays.asList("Carla", "Giorgio");

        List<Voice> voices;
        List<Voice> allVoices = ivonaFacade.getVoices();
        if(recommended) {
            voices = new ArrayList<Voice>();
            for(Voice voice : allVoices) {
                if (recommendedVoiceNames.contains(voice.getName())) {
                    voices.add(voice);
                }
            }
        } else voices = allVoices;

        PrintWriter writer = new PrintWriter(response.getOutputStream());
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(voices);
        System.out.println(objectMapper.writeValueAsString(voices));
        writer.print(objectMapper.writeValueAsString(voices));
        writer.flush();
    }
}
