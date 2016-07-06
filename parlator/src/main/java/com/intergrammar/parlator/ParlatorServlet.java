package com.intergrammar.parlator;

import com.interlinguatts.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.Normalizer;

public class ParlatorServlet extends HttpServlet {

    private TextToSpeech tts;
    private VoiceGenerator voiceGenerator;

    public ParlatorServlet() {
        System.out.println("Starting parlator servlet!");
        ApplicationContext context = ApplicationContext.getInstance();
        tts = context.tts();
        voiceGenerator =  context.voiceGenerator();
        System.out.println("Parlator servlet loaded!");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.io.IOException {
        response.setContentType("audio/mpeg");
        Voice defaultVoice = voiceGenerator.getDefaultVoice();
        String voiceName = request.getParameter("voiceName");
        if (voiceName == null || voiceName.isEmpty()) {
            voiceName = defaultVoice.getName();
        }

        String voiceLanguage = request.getParameter("voiceLanguage");
        if (voiceLanguage == null || voiceLanguage.isEmpty()) {
            voiceLanguage = defaultVoice.getLanguage();
        }

        Voice voice = new Voice();
        voice.setName(voiceName);
        voice.setLanguage(voiceLanguage);

        String text = request.getParameter("text");
        String filename = text;
        filename = Normalizer.normalize(filename, Normalizer.Form.NFD);
        filename = filename.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        filename = filename.replaceAll("[^a-zA-Z0-9.-]", "_");
        int maxFilenameSize = 50;
        if(filename.length() > maxFilenameSize) {
            filename = filename.substring(0,maxFilenameSize);
        }
        response.addHeader("Content-Disposition", "attachment; filename=" + filename + ".mp3");
        try {
            tts.textToSpeech(response.getOutputStream(), voice, text);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            System.out.println(sw);
            request.getSession().setAttribute(request.getQueryString(), sw.toString());
        }
    }
}
