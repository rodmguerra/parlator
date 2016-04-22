package com.interlinguatts;

import com.interlinguatts.domain.Word;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WordCsvReader {

    InputStream stream;

    public WordCsvReader(String csvFile) {
        try {
            this.stream = new FileInputStream(csvFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public WordCsvReader(InputStream stream) {
        this.stream = stream;
    }

    public List<Word> readWords() {
        BufferedReader reader = null;

        try {
            String line;
            reader = new BufferedReader(new InputStreamReader(stream));
            List<Word> words = new ArrayList<Word>();
            while ((line = reader.readLine()) != null) {
                String[] lineSplit = line.split(";",-1);
                Word word = new Word(lineSplit[0], lineSplit[1], lineSplit[2]);
                words.add(word);
            }
            return words;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }



}
