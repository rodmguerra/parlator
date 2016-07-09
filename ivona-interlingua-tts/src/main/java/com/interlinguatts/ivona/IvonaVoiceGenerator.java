package com.interlinguatts.ivona;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.google.common.collect.Sets;
import com.interlinguatts.LexiconXmlBuilder;
import com.interlinguatts.VoiceGenerator;
import com.ivona.services.tts.IvonaSpeechCloudClient;
import com.ivona.services.tts.model.*;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class IvonaVoiceGenerator implements VoiceGenerator {
    private IvonaSpeechCloudClient speechCloud;
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    public IvonaVoiceGenerator() {
        speechCloud = new IvonaSpeechCloudClient(
                new ClasspathPropertiesFileCredentialsProvider("ivona.properties"));
        speechCloud.setEndpoint("https://tts.us-east-1.ivonacloud.com");
    }

    public void putLexicon(String name, String contents) {
        Lexicon lexicon = new Lexicon().withName(name).withContents(contents);
        PutLexiconRequest putLexiconRequest = new PutLexiconRequest().withLexicon(lexicon);
        speechCloud.putLexicon(putLexiconRequest);
    }

    public List<String> getLexiconNames() {
        ListLexiconsResult result = speechCloud.listLexicons();
        return result.getLexiconNames();
    }

    public List<com.interlinguatts.Voice> getVoices() {
        ListVoicesRequest listVoicesRequest = new ListVoicesRequest();
        ListVoicesResult result = speechCloud.listVoices(listVoicesRequest);
        List<com.interlinguatts.Voice> apiVoices = new ArrayList<com.interlinguatts.Voice>();
        for (Voice voice : result.getVoices()) {
           apiVoices.add(toApiVoice(voice));
        }
        return apiVoices;
    }

    public List<com.interlinguatts.Voice> getGoodVoicesForInterlingua() {
        List<com.interlinguatts.Voice> voices = getVoices();
        List<com.interlinguatts.Voice> goodVoices = new ArrayList<com.interlinguatts.Voice>();
        List<String> goodVoiceNames = Arrays.asList("Carla", "Giorgio");
        for (com.interlinguatts.Voice voice : voices) {
            if (goodVoiceNames.contains(voice.getName())) {
                goodVoices.add(voice);
            }
        }
        return goodVoices;
    }

    private InputStream textAndLexiconToAudio(com.interlinguatts.Voice voice, String text, List<String> lexiconNames) {
        CreateSpeechRequest request = new CreateSpeechRequest();
        Input input = new Input();
        OutputFormat format = new OutputFormat();
        format.setCodec("MP3");
        input.setData(text);
        request.setLexiconNames(lexiconNames);
        request.setInput(input);
        request.setVoice(toIvonaVoice(voice));
        request.setOutputFormat(format);

        Parameters parameters = new Parameters();
        parameters.setRate("slow");
        request.setParameters(parameters);
        CreateSpeechResult createSpeechResult = speechCloud.createSpeech(request);
        return createSpeechResult.getBody();
    }

    public void deleteLexicon(String lexiconName) {
        DeleteLexiconRequest request = new DeleteLexiconRequest().withLexiconName(lexiconName);
        speechCloud.deleteLexicon(request);
    }

    private com.interlinguatts.Voice toApiVoice(Voice ivonaVoice) {
        com.interlinguatts.Voice apiVoice = new com.interlinguatts.Voice();
        apiVoice.setGender(ivonaVoice.getGender());
        apiVoice.setName(ivonaVoice.getName());
        apiVoice.setLanguage(ivonaVoice.getLanguage());
        return apiVoice;
    }

    private Voice toIvonaVoice(com.interlinguatts.Voice apiVoice) {
        Voice ivonaVoice = new Voice();
        ivonaVoice.setGender(apiVoice.getGender());
        ivonaVoice.setName(apiVoice.getName());
        ivonaVoice.setLanguage(apiVoice.getLanguage());
        return ivonaVoice;
    }

    @Override
    public InputStream ssmlToAudio(com.interlinguatts.Voice voice, String text) {
        CreateSpeechRequest request = new CreateSpeechRequest();
        Input input = new Input();
        OutputFormat format = new OutputFormat();
        format.setCodec("MP3");
        input.setData(text);
        input.setType("application/ssml+xml");
        request.setInput(input);
        request.setVoice(toIvonaVoice(voice));
        request.setOutputFormat(format);
        Parameters parameters = new Parameters();
        parameters.setRate("slow");
        request.setParameters(parameters);
        CreateSpeechResult createSpeechResult = speechCloud.createSpeech(request);
        return createSpeechResult.getBody();
    }

    @Override
    public synchronized InputStream textAndLexiconToAudio(com.interlinguatts.Voice voice, String text, Map<String, String> lexemes) {
        List<String> lexiconNames = putLexiconSafe(lexemes, voice.getLanguage());
        return textAndLexiconToAudio(voice, text, lexiconNames);
    }

    @Override
    public com.interlinguatts.Voice getDefaultVoice() {
        com.interlinguatts.Voice voice = new com.interlinguatts.Voice();
        voice.setName("Carla");
        voice.setLanguage("it-IT");
        return voice;
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

        SortedSet<String> existentLexiconNames = Sets.newTreeSet(getLexiconNames());
        int emptySlotCount = maxLexiconSlotCount - existentLexiconNames.size();
        for(int i=0; i<lexiconCount-emptySlotCount; i++) {
            String oldestLexiconName = existentLexiconNames.first();
            existentLexiconNames.remove(oldestLexiconName);
            deleteLexicon(oldestLexiconName);
        }

        List<String> lexiconNames = new ArrayList<String>();
        for(String lexicon : lexicons) {
            String lexiconName = lexiconNameFromTimestamp();
            putLexicon(lexiconName, lexicon);
            lexiconNames.add(lexiconName);
        }
        return lexiconNames;
    }

}
