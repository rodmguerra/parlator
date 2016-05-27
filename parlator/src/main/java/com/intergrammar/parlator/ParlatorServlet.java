package com.intergrammar.parlator;

import com.interlinguatts.InterlinguaIpaProvider;
import com.interlinguatts.InterlinguaNumberWriter;
import com.interlinguatts.InterlinguaTextToSpeach;
import com.interlinguatts.IvonaFacade;
import com.interlinguatts.repository.MemoryWordRepository;
import com.interlinguatts.domain.Word;
import com.interlinguatts.repository.Repository;
import com.interlinguatts.repository.WordRepository;
import com.ivona.services.tts.model.Voice;
import org.apache.commons.dbcp.BasicDataSource;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.Normalizer;

public class ParlatorServlet extends HttpServlet {

    private InterlinguaTextToSpeach tts;

    public ParlatorServlet() {
        System.out.println("Starting parlator servlet!");
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://pellefant.db.elephantsql.com:5432/xoyaewgn");
        dataSource.setUsername("xoyaewgn");
        dataSource.setPassword("t9Y27piepSw8YbhM5WzRT19f747J7uNU");
        dataSource.setDefaultAutoCommit(false);
        WordRepository wordRepository = new WordRepository(dataSource);
        Repository<Word> memoryWordRepository = new MemoryWordRepository(wordRepository.findAll());
        IvonaFacade ivonaFacade = new IvonaFacade();
        InterlinguaNumberWriter numberWriter = new InterlinguaNumberWriter();
        InterlinguaIpaProvider provider = new InterlinguaIpaProvider(memoryWordRepository, numberWriter);
        tts = new InterlinguaTextToSpeach(ivonaFacade, provider, numberWriter);
        System.out.println("Parlator servlet loaded!");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.io.IOException {
        response.setContentType("audio/mpeg");
        String voiceName = request.getParameter("voiceName");
        if (voiceName == null || voiceName.isEmpty()) {
            voiceName = "Carla";
        }

        String voiceLanguage = request.getParameter("voiceLanguage");
        if (voiceLanguage == null || voiceLanguage.isEmpty()) {
            voiceLanguage = "it-IT";
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
            tts.textToSpeech(text, response.getOutputStream(), voice);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            System.out.println(sw);
            request.getSession().setAttribute(request.getQueryString(), sw.toString());
        }
    }
}
