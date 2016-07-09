package com.intergrammar.parlator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interlinguatts.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.text.Normalizer;
import java.util.Map;

public class ParlatorServlet extends HttpServlet {

    private TextToSpeech tts;
    private VoiceGenerator voiceGenerator;
    private SessionHandler sessionHandler;

    public ParlatorServlet() {
        System.out.println("Starting parlator servlet!");
        ApplicationContext context = ApplicationContext.getInstance();
        tts = context.tts();
        voiceGenerator =  context.voiceGenerator();
        sessionHandler = context.sessionHandler();
        System.out.println("Parlator servlet loaded!");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.io.IOException {
        InputStream inputStream = null;
        String queryString = null;
        String errorMessage = "";
        OutputStream responseOutputStream = null;
        Map<String,String> errorMessageMap = null;
        Map<String,byte[]> audioMap = null;
        try {
            HttpSession session = request.getSession();
            audioMap = sessionHandler.getAudioMap(session);
            errorMessageMap = sessionHandler.getErrorMessageMap(session);

            queryString = request.getQueryString();
            String text = request.getParameter("text");

            if(audioMap.containsKey(queryString)) {
                inputStream = new ByteArrayInputStream(audioMap.get(queryString));
            } else {
                String voiceJson = request.getParameter("voice");
                Voice voice = voiceJson == null? voiceGenerator.getDefaultVoice() : new ObjectMapper().readValue(voiceJson, Voice.class);
                inputStream = tts.textToSpeech(voice, text);
            }

            response.setContentType("audio/ogg; codecs=opus");
            response.addHeader("Content-Disposition", "attachment; filename=" + fileName(text) + ".ogg");

            byte[] buffer = new byte[2 * 1024];
            int readBytes;
            responseOutputStream = response.getOutputStream();
            ByteArrayOutputStream bufferOutputStream = new ByteArrayOutputStream();

            while ((readBytes = inputStream.read(buffer)) > 0) {
                responseOutputStream.write(buffer, 0, readBytes);
                bufferOutputStream.write(buffer,0, readBytes);
            }
            bufferOutputStream.flush();
            audioMap.put(queryString,bufferOutputStream.toByteArray());

            //System.out.println("\nFile saved: " + outputFileName);
        } catch (Exception e) {
            errorMessage += stackTrace(e);
        } finally {

            //close inputstream
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                errorMessage += stackTrace(e);
                storeAndLogErrorMessageIfAny(queryString, errorMessage, errorMessageMap);
            }

            //close outputsteam
            try {
                if (responseOutputStream != null) {
                    responseOutputStream.close();
                }
            } catch (Exception e) {
                errorMessage += stackTrace(e);
                storeAndLogErrorMessageIfAny(queryString, errorMessage, errorMessageMap);
            }

            //log error
            storeAndLogErrorMessageIfAny(queryString, errorMessage, errorMessageMap);
        }
    }

    private void storeAndLogErrorMessageIfAny(String queryString, String errorMessage, Map<String, String> errorMessageMap) {
        if(errorMessage != null || errorMessage != "") {
            System.out.println(errorMessage);
            errorMessageMap.put(queryString, errorMessage);
        }
    }

    private String fileName(String text) {
        String filename = text;
        filename = Normalizer.normalize(filename, Normalizer.Form.NFD);
        filename = filename.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        filename = filename.replaceAll("[^a-zA-Z0-9.-]", "_");
        int maxFilenameSize = 50;
        if(filename.length() > maxFilenameSize) {
            filename = filename.substring(0,maxFilenameSize);
        }
        return filename;
    }

    private String stackTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
