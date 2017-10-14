package com.interlinguatts;

import com.interlinguatts.domain.Word;
import com.interlinguatts.repository.MemoryWordRepository;
import com.interlinguatts.repository.Repository;
import com.interlinguatts.repository.WordRepository;
import org.apache.commons.dbcp.BasicDataSource;

public class TestApplicationContext {

    private static TestApplicationContext instance;

    public static TestApplicationContext getInstance() {
        if(instance == null) {
            instance = new TestApplicationContext();
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

            VoiceGenerator voiceGenerator = voiceGenerator();
            VoiceBugFixer ivonaVoiceBugFixer;
            try {
                Class voiceBugFixerClass = Class.forName("com.interlinguatts.ivona.IvonaVoiceBugFixer");
                ivonaVoiceBugFixer = (VoiceBugFixer) voiceBugFixerClass.newInstance();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Ivona TTS jar not found.", e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            TextToPhonetics textToPhonetics = new TextToPhonetics(wordToPhonetics, ivonaVoiceBugFixer, preProcessor);
            tts = new LexiconTextToSpeech(voiceGenerator, textToPhonetics);
            //tts = new SsmlTextToSpeech(voiceGenerator, textToPhonetics, PhoneticAlphabet.IPA);
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
            //tts = new LexiconTextToSpeech(voiceGenerator, textToPhonetics);
            tts = new SsmlTextToSpeech(voiceGenerator, textToPhonetics, PhoneticAlphabet.IPA);
        }

        return tts;
    }
    */

    private VoiceGenerator voiceGenerator;

    //ivona
    public VoiceGenerator voiceGenerator() {
        if(voiceGenerator == null) {
            try {
                Class voiceGeneratorClass = Class.forName("com.interlinguatts.ivona.IvonaVoiceGenerator");
                voiceGenerator = (VoiceGenerator) voiceGeneratorClass.newInstance();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Ivona TTS jar not found.", e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return voiceGenerator;
    }

    /*
    public VoiceGenerator voiceGenerator() {
        if(voiceGenerator == null) {
            voiceGenerator = new IbmVoiceGenerator();
        }
        return voiceGenerator;
    }
    */

}
