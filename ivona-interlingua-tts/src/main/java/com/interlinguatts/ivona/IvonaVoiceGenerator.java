package com.interlinguatts.ivona;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.polly.AmazonPollyClient;
import com.amazonaws.services.polly.model.*;
import com.amazonaws.services.polly.model.Voice;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.interlinguatts.*;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class IvonaVoiceGenerator implements VoiceGenerator {
    private AmazonPollyClient speechCloud;
    private final List<MediaType> mediaTypes = ImmutableList.<MediaType>of(
            new MediaType("audio/mpeg", "mp3"),
            new MediaType("audio/ogg".toString(), "ogg")
    );
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    public IvonaVoiceGenerator() {
        /*speechCloud = new AmazonPollyClient(
                new ClasspathPropertiesFileCredentialsProvider("polly.properties"));
        speechCloud.setEndpoint("https://tts.us-east-1.ivonacloud.com");         */
        speechCloud = new AmazonPollyClient(new BasicAWSCredentials("AKIAJSG6IZIFY2NQV33A", "JbjTEWPJXssG3pW71zHfM/JgcP1mDbzjOZxVJgcW"));
        speechCloud.setEndpoint("polly.us-west-2.amazonaws.com");
    }

    public void putLexicon(String name, String contents) {
        PutLexiconRequest putLexiconRequest = new PutLexiconRequest().withName(name).withContent(contents);
        speechCloud.putLexicon(putLexiconRequest);
    }

    public List<LexiconDescription> getLexiconDescriptions() {
        ListLexiconsRequest listLexiconsRequest = new ListLexiconsRequest();
        ListLexiconsResult result = speechCloud.listLexicons(listLexiconsRequest);
        return result.getLexicons();
    }

    public List<String> getLexiconNames(List<LexiconDescription> descriptions) {
        List<String> lexiconNames = new ArrayList<String>();
        for (LexiconDescription lexiconDescription : descriptions) {
            lexiconNames.add(lexiconDescription.getName());
        }
        return lexiconNames;
    }



    public List<com.interlinguatts.Voice> getVoices() {
        DescribeVoicesRequest listVoicesRequest = new DescribeVoicesRequest();
        DescribeVoicesResult result = speechCloud.describeVoices(listVoicesRequest);
        List<com.interlinguatts.Voice> apiVoices = new ArrayList<com.interlinguatts.Voice>();
        for (Voice voice : result.getVoices()) {
           apiVoices.add(toApiVoice(voice));
        }
        return apiVoices;
    }

    public List<com.interlinguatts.Voice> getGoodVoicesForInterlingua() {
        List<com.interlinguatts.Voice> voices = getVoices();
        List<com.interlinguatts.Voice> goodVoices = new ArrayList<com.interlinguatts.Voice>();
        List<String> goodVoiceNames = Lists.newArrayList("Carla", "Giorgio");
        for (String voiceName : goodVoiceNames) {
            for (com.interlinguatts.Voice voice : voices) {
                if(voice.getName().equals(voiceName)) {
                    goodVoices.add(voice);
                    break;
                }
            }
        }
        return goodVoices;
    }

    private InputStream textAndLexiconToAudio(com.interlinguatts.Voice voice, String text, List<String> lexiconNames, MediaType mediaType) {
        SynthesizeSpeechRequest request = new SynthesizeSpeechRequest();
        OutputFormat format = outputFormatFromMediaType(mediaType.getExtension());
        request.setLexiconNames(lexiconNames);
        if("Giorgio".equals(voice.getName())) {
            String rate = "slow";
            text = "<speak><prosody rate=\"" + rate + "\">" + text + "</prosody></speak>";
        } else {
            text = "<speak>"+ text + "</speak>";
        }
        request.setText(text);
        request.setVoiceId(toVoiceId(voice));
        request.setOutputFormat(format);
        request.setTextType(TextType.Ssml);

    /*
        Parameters parameters = new Parameters();
        parameters.setRate("slow");
        request.setParameters(parameters);
      */
        SynthesizeSpeechResult createSpeechResult = speechCloud.synthesizeSpeech(request);
        return createSpeechResult.getAudioStream();
    }

    public void deleteLexicon(String lexiconName) {
        DeleteLexiconRequest request = new DeleteLexiconRequest().withName(lexiconName);
        speechCloud.deleteLexicon(request);
    }

    private com.interlinguatts.Voice toApiVoice(Voice ivonaVoice) {
        com.interlinguatts.Voice apiVoice = new com.interlinguatts.Voice();
        apiVoice.setGender(ivonaVoice.getGender());
        apiVoice.setName(ivonaVoice.getName());
        apiVoice.setLanguage(ivonaVoice.getLanguageCode());
        apiVoice.setDescription(ivonaVoice.getName());
        return apiVoice;
    }

    private VoiceId toVoiceId(com.interlinguatts.Voice apiVoice) {
        VoiceId ivonaVoice = VoiceId.fromValue(apiVoice.getName());
        return ivonaVoice;
    }

    @Override
    public InputStream ssmlToAudio(com.interlinguatts.Voice voice, String text, MediaType mediaType) {
        SynthesizeSpeechRequest request = new SynthesizeSpeechRequest();
        String extension = mediaType.getExtension();
        OutputFormat format = outputFormatFromMediaType(extension);
        request.setTextType(TextType.Ssml);
        request.setVoiceId(toVoiceId(voice));
        request.setOutputFormat(format);
        request.setText(text);
        /*
        Parameters parameters = new Parameters();
        parameters.setRate("slow");
        request.setParameters(parameters);
        */
        SynthesizeSpeechResult createSpeechResult = speechCloud.synthesizeSpeech(request);
        return createSpeechResult.getAudioStream();
    }

    private OutputFormat outputFormatFromMediaType(String extension) {
        OutputFormat format = extension.equals("mp3")? OutputFormat.Mp3 : extension.equals("ogg")? OutputFormat.Ogg_vorbis: null;
        if(format == null) throw new IllegalArgumentException("Extension " + extension + " not supported.");
        return format;
    }

    @Override
    public List<MediaType> getAvailableMediaTypes() {
        return mediaTypes;
    }

    @Override
    public synchronized InputStream textAndLexiconToAudio(com.interlinguatts.Voice voice, String text, Map<String, String> lexemes, MediaType mediaType) {
        List<String> lexiconNames = putLexiconSafe(lexemes, voice.getLanguage());
        return textAndLexiconToAudio(voice, text, lexiconNames, mediaType);
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
        List<String> lexicons = lexiconXmlBuilder.toXml(lexemes, language, 4000);
        int lexiconCount = lexicons.size();
        int maxLexiconSlotCount = 5;

        if(lexiconCount > maxLexiconSlotCount) {
            throw new RuntimeException(String.format("Too much lexicons! needed: %s available: %s", lexiconCount, maxLexiconSlotCount));
        }

        SortedSet<String> existentLexiconNames = Sets.newTreeSet(getLexiconNames(getLexiconDescriptions()));
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
