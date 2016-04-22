package com.interlinguatts;


import com.interlinguatts.domain.Word;
import com.interlinguatts.repository.WordRepository;
import org.apache.commons.dbcp.BasicDataSource;

import java.io.InputStream;
import java.util.List;

public class WordInsertApp {

    public static void main2(String[] args) {
        String originalFile = "C:\\Users\\rodmguerra\\Projetos Pessoais\\intergrammar\\dictionary\\original.txt";
        String respellFile = "C:\\Users\\rodmguerra\\Projetos Pessoais\\intergrammar\\dictionary\\respell.txt";
        WordFileReader wordReader = new WordFileReader(originalFile, respellFile);
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://pellefant.db.elephantsql.com:5432/xoyaewgn");
        dataSource.setUsername("xoyaewgn");
        dataSource.setPassword("t9Y27piepSw8YbhM5WzRT19f747J7uNU");
        dataSource.setDefaultAutoCommit(false);
        WordRepository wordRepository = new WordRepository(dataSource);
        List<Word> words = wordReader.readWords();
        wordRepository.insert(words);
    }


    public static void main(String[] args) {
        String file = "dictionary.csv";
        InputStream in = WordInsertApp.class.getClassLoader().getResourceAsStream(file);
        WordCsvReader wordReader = new WordCsvReader(in);
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://pellefant.db.elephantsql.com:5432/xoyaewgn");
        dataSource.setUsername("xoyaewgn");
        dataSource.setPassword("t9Y27piepSw8YbhM5WzRT19f747J7uNU");
        dataSource.setDefaultAutoCommit(false);
        WordRepository wordRepository = new WordRepository(dataSource);
        wordRepository.deleteAll();
        List<Word> words = wordReader.readWords();
        wordRepository.insert(words);
    }
}
