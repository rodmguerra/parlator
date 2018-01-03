package com.intergrammar.parlator;

import com.interlinguatts.*;
import com.interlinguatts.domain.Word;
import com.interlinguatts.repository.MemoryWordRepository;
import com.interlinguatts.repository.Repository;
import com.interlinguatts.repository.WordRepository;
import org.apache.commons.dbcp.BasicDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

public class ApplicationContext {

    private static ApplicationContext instance;
    public static ApplicationContext getInstance() {
        if(instance == null) {
            instance = new ApplicationContext();
        }
        return instance;
    }

    private TextToSpeech tts;

    //Ivona
    public TextToSpeech tts() {
        if(tts == null) {
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setDriverClassName("org.postgresql.Driver");
            dataSource.setUrl("jdbc:postgresql://pellefant.db.elephantsql.com:5432/xoyaewgn");
            dataSource.setUsername("xoyaewgn");
            dataSource.setPassword("t9Y27piepSw8YbhM5WzRT19f747J7uNU");
            dataSource.setDefaultAutoCommit(false);
            WordRepository wordRepository = new WordRepository(dataSource);
            Repository<Word> memoryWordRepository = new MemoryWordRepository(wordRepository.findAll());

            InterlinguaNumberWriter numberWriter = new InterlinguaNumberWriter();
            WordToPhonetics wordToPhonetics = new WordToPhonetics(memoryWordRepository, numberWriter);
            InterlinguaTTSPreProcessor preProcessor = new InterlinguaTTSPreProcessor(numberWriter);

            VoiceBugFixer voiceBugFixer = instance("com.interlinguatts.ivona.IvonaVoiceBugFixer", VoiceBugFixer.class);

            TextToPhonetics textToPhonetics = new TextToPhonetics(wordToPhonetics, voiceBugFixer, preProcessor);
            tts = new LexiconTextToSpeech(voiceGenerator(), textToPhonetics);
            //tts = new SsmlTextToSpeech(voiceGenerator(), textToPhonetics, PhoneticAlphabet.IPA);
        }

        return tts;
    }

    /*
    //ibm
    public TextToSpeech tts() {
        if(tts == null) {
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setDriverClassName("org.postgresql.Driver");
            dataSource.setUrl("jdbc:postgresql://pellefant.db.elephantsql.com:5432/xoyaewgn");
            dataSource.setUsername("xoyaewgn");
            dataSource.setPassword("t9Y27piepSw8YbhM5WzRT19f747J7uNU");
            dataSource.setDefaultAutoCommit(false);
            WordRepository wordRepository = new WordRepository(dataSource);
            Repository<Word> memoryWordRepository = new MemoryWordRepository(wordRepository.findAll());

            InterlinguaNumberWriter numberWriter = new InterlinguaNumberWriter();
            WordToPhonetics wordToPhonetics = new WordToPhonetics(memoryWordRepository, numberWriter);
            InterlinguaTtsPreProcessor preProcessor = new InterlinguaTtsPreProcessor(numberWriter);

            VoiceGenerator voiceGenerator = voiceGenerator();
            VoiceBugFixer voiceBugFixer = new IbmVoiceBugFixer();
            TextToPhonetics textToPhonetics = new TextToPhonetics(wordToPhonetics, voiceBugFixer, preProcessor);
            tts = new LexiconTextToSpeech(voiceGenerator, textToPhonetics);
            //tts = new SsmlTextToSpeech(voiceGenerator, textToPhonetics, PhoneticAlphabet.IPA);
        }

        return tts;
    }
    */

    private VoiceGenerator voiceGenerator;

    //ivona
    public VoiceGenerator voiceGenerator() {
        if(voiceGenerator == null) {
            System.out.println("voice generator instance");
            Properties properties = properties("parlator.properties");
            String user = properties.getProperty("parlator.amazon.user");
            String password = properties.getProperty("parlator.amazon.password");
            String endpoint = properties.getProperty("parlator.amazon.endpoint");
            voiceGenerator = instance("com.interlinguatts.ivona.AmazonVoiceGenerator",
                    VoiceGenerator.class,
                    new Credentials(user, password, endpoint)
            );
            System.out.println("voice generator instance created");
        }
        return voiceGenerator;
    }

    private <T> T instance(String className, Class<T> interfaz) {
        try {
            Class clazz = Class.forName(className);
            return (T) clazz.newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Ivona TTS jar not found.", e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T instance(String className, Class<T> interfaz, Object... params) {
        try {
            Class clazz = Class.forName(className);
            Class<?>[] paramTypes = new Class[params.length];
            for (int i = 0; i < params.length; i++) {
                paramTypes[i] = params[i].getClass();
            }
            Constructor constructor = clazz.getConstructor(paramTypes);
            return (T) constructor.newInstance(params);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Ivona TTS jar not found.", e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }


    /*
    //ibm
    public VoiceGenerator voiceGenerator() {
        if(voiceGenerator == null) {
            voiceGenerator = new IbmVoiceGenerator();
        }
        return voiceGenerator;
    }
    */
    public SessionHandler sessionHandler() {
        return new SessionHandler();
    }

    public Properties properties(String resource) {

        try {
            Properties properties = new Properties();
            properties.load(this.getClass().getClassLoader().getResourceAsStream(resource));
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
