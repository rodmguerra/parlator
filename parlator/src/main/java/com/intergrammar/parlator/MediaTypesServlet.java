package com.intergrammar.parlator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interlinguatts.MediaType;
import com.interlinguatts.Voice;
import com.interlinguatts.VoiceGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class MediaTypesServlet extends HttpServlet {

    private VoiceGenerator voiceGenerator;
    {
        voiceGenerator = ApplicationContext.getInstance().voiceGenerator();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");
        List<MediaType> mediaTypes = voiceGenerator.getAvailableMediaTypes();
        PrintWriter writer = new PrintWriter(response.getOutputStream());
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(mediaTypes);
        System.out.println(objectMapper.writeValueAsString(mediaTypes));
        writer.print(objectMapper.writeValueAsString(mediaTypes));
        writer.flush();
    }
}
