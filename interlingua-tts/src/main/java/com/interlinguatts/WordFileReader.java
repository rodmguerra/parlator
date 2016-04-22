package com.interlinguatts;

import com.interlinguatts.domain.Word;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WordFileReader {

    private final String originalFile;
    private final String respellFile;

    public WordFileReader(String originalFile, String respellFile) {
        this.originalFile = originalFile;
        this.respellFile = respellFile;
    }

    public List<Word> readWords() {
        BufferedReader originalReader = null;
        BufferedReader respellReader = null;

        try {
            String originalLine;
            String respellLine;

            originalReader = new BufferedReader(new FileReader(originalFile));
            respellReader = new BufferedReader(new FileReader(respellFile));
            List<Word> words = new ArrayList<Word>();
            int i=0;
            while ((originalLine = originalReader.readLine()) != null && (respellLine = respellReader.readLine()) != null) {
                Word word = new Word(originalLine, respellLine, null);
                words.add(word);
            }
            return words;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (originalReader != null) originalReader.close();
                if (respellReader != null) originalReader.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }



}
