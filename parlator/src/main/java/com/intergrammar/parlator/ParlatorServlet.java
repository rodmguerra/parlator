package com.intergrammar.parlator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interlinguatts.MediaType;
import com.interlinguatts.TextToSpeech;
import com.interlinguatts.Voice;
import com.interlinguatts.VoiceGenerator;

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
    private final ObjectMapper objectMapper = new ObjectMapper();
    public ParlatorServlet() {
        System.out.println("Starting parlator servlet!");
        ApplicationContext context = ApplicationContext.getInstance();
        tts = context.tts();
        voiceGenerator =  context.voiceGenerator();
        sessionHandler = context.sessionHandler();
        System.out.println("Parlator servlet 2 loaded!");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.io.IOException {
        try {
            processRequest(request, response);
        } catch (Throwable e) {
            String stackTrace = stackTrace(e);
            System.out.println(stackTrace);
            response.sendError(500, stackTrace);
        }
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) {
        InputStream inputStream = null;
        String queryString = null;
        OutputStream responseOutputStream = null;
        Map<String,byte[]> audioMap = null;
        try {
            HttpSession session = request.getSession();
            audioMap = sessionHandler.getAudioMap(session);

            queryString = request.getQueryString();
            String text = request.getParameter("text");

            MediaType mediaType;
            String mediaTypeJson = request.getParameter("mediaType");
            if(mediaTypeJson == null) {
                mediaType = tts.getDefaultMediaType();
            } else {
                mediaType = objectMapper.readValue(mediaTypeJson, MediaType.class);
            }

            if(audioMap.containsKey(queryString)) {
                inputStream = new ByteArrayInputStream(audioMap.get(queryString));
            } else {
                String voiceJson = request.getParameter("voice");
                Voice voice = voiceJson == null? voiceGenerator.getDefaultVoice() : objectMapper.readValue(voiceJson, Voice.class);
                inputStream = tts.textToSpeech(voice, text, mediaType);
            }

            response.setContentType(mediaType.getContentType());
            response.addHeader("Content-Disposition", "attachment; filename=" + fileName(text) + "." + mediaType.getExtension());

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
            throw new RuntimeException(e);
        } finally {
            //close inputstream
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                //exception on finally
                System.out.println("excepton on finally: " + stackTrace(e));
            }
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
