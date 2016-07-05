package com.interlinguatts.ivona;

import com.google.common.collect.Sets;
import com.interlinguatts.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.interlinguatts.InterlinguaIpaProvider.padRight;

public class IvonaLexiconInterlinguaTTS implements TextToSpeach {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private final InterlinguaIpaProvider provider;
    private final IvonaConnector ivonaFacade;
    private final InterlinguaTTSPreProcessor preProcessor;
    private final IvonaVoiceBugFixer ivonaVoiceBugFixer = new IvonaVoiceBugFixer();


    public IvonaLexiconInterlinguaTTS(IvonaConnector ivonaFacade, InterlinguaIpaProvider provider, InterlinguaTTSPreProcessor preProcessor) {
        this.ivonaFacade = ivonaFacade;
        this.provider = provider;
        this.preProcessor = preProcessor;
    }

    public IvonaLexiconInterlinguaTTS(InterlinguaIpaProvider provider, InterlinguaTTSPreProcessor preProcessor) {
        this(new IvonaConnector(), provider, preProcessor);
    }


    public Map<String,String> graphemePhonemeMap(String text, Voice voice) {
        TreeMap<String, String> graphemePhonemeMap = new TreeMap<String, String>();

        String[] words = extractWords(text);

        for(String word : words) {
            if(word.isEmpty()) {
                continue;
            }

            String bugFix = ivonaVoiceBugFixer.fixVoiceBugsBefore(voice, word, words.length);
            String ipa = (bugFix==null)? provider.toIpa(word, words.length == 1) : bugFix;
            ipa = ivonaVoiceBugFixer.fixVoiceBugsAfter(voice, ipa);
            graphemePhonemeMap.put(word, ipa);
        }
        Map<String,String> descending = graphemePhonemeMap.descendingMap();
        for (String word : descending.keySet()) {
            String ipa = descending.get(word);
            System.out.println("word=\"" + padRight(word + "\"",30)  + "ipa=\"" + padRight(ipa + "\"", 30));
        }
        return descending;
    }

    private String[] extractWords(String text) {
        String intermediateText = text.replaceAll("[.,;:?!“”\"'\\-–\\)\\]\\}]+ ", " ");  //special + space
        intermediateText = intermediateText.replaceAll("[.,;:?!“”\"'\\-–\\)\\]\\}]+$", " ");  //special + end
        intermediateText = intermediateText.replaceAll("[.,;:?!“”\"'\\-–\\)\\]\\}]+\n", " \n");  //special + line end
        intermediateText = intermediateText.replaceAll(" [“”\"'\\-–\\(\\[\\{]+", " ");  //space + quotes
        intermediateText = intermediateText.replaceAll("^[“”\"'\\-–\\(\\[\\{]+", " ");  //start + quotes
        intermediateText = intermediateText.replaceAll("\n[“”\"'\\-–\\(\\[\\{]+", "\n ");  //line start + quotes
        return intermediateText.split("[ &\n]");
    }

    public void textToSpeech(String text, String speechFileName, Voice voice) {
        try {
            OutputStream outputStream = new FileOutputStream(new File(speechFileName));
            textToSpeech(text, outputStream, voice);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void textToSpeech(String text, OutputStream outputStream, Voice voice) {

        text = preProcessor.preProcessText(text);

        Map<String, String> graphemePhonemeMap = graphemePhonemeMap(text, voice);

        textToSpeechSynchronizedPart(text, graphemePhonemeMap, voice, outputStream);
    }

    private synchronized void textToSpeechSynchronizedPart(String text, Map<String, String> graphemePhonemeMap, Voice voice, OutputStream outputStream) {
        List<String> lexiconNames = putLexiconSafe(graphemePhonemeMap, voice.getLanguage());
        System.out.println(text);
        com.ivona.services.tts.model.Voice ivonaVoice = new com.ivona.services.tts.model.Voice();
        ivonaVoice.setGender(voice.getGender());
        ivonaVoice.setLanguage(voice.getLanguage());
        ivonaVoice.setName(voice.getName());
        ivonaFacade.textToSpeech(ivonaVoice, text, outputStream, lexiconNames);
    }

    private String lexiconNameFromTimestamp() {
        return DATE_FORMAT.format(new Date());
    }

    private List<String> putLexiconSafe(Map<String, String> lexemes, String language) {

        LexiconXmlBuilder lexiconXmlBuilder = new LexiconXmlBuilder();
        List<String> lexicons = lexiconXmlBuilder.toXml(lexemes, language, 4096);
        int lexiconCount = lexicons.size();
        int maxLexiconSlotCount = 5;

        if(lexiconCount > maxLexiconSlotCount) {
            throw new RuntimeException(String.format("Too much lexicons! needed: %s available: %s", lexiconCount, maxLexiconSlotCount));
        }

        SortedSet<String> existentLexiconNames = Sets.newTreeSet(ivonaFacade.getLexiconNames());
        int emptySlotCount = maxLexiconSlotCount - existentLexiconNames.size();
        for(int i=0; i<lexiconCount-emptySlotCount; i++) {
            String oldestLexiconName = existentLexiconNames.first();
            existentLexiconNames.remove(oldestLexiconName);
            ivonaFacade.deleteLexicon(oldestLexiconName);
        }

        List<String> lexiconNames = new ArrayList<String>();
        for(String lexicon : lexicons) {
            String lexiconName = lexiconNameFromTimestamp();
            ivonaFacade.putLexicon(lexiconName, lexicon);
            lexiconNames.add(lexiconName);
        }
        return lexiconNames;
    }
}
