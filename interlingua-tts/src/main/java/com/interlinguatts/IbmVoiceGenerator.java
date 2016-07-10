package com.interlinguatts;


import com.google.common.collect.ImmutableMap;
import com.ibm.watson.developer_cloud.http.HttpMediaType;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice.*;

public class IbmVoiceGenerator implements VoiceGenerator {

    private final String userName;
    private final String password;
    private final com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech ibmTts;
    private static final ImmutableMap<String, Map<String,String>> NATIONALITATES = ImmutableMap.<String, Map<String,String>>builder()
            .put("it-IT", ImmutableMap.of("male", "italiano", "female", "italiana"))
            .put("de-DE", ImmutableMap.of("male", "germano", "female", "germana"))
            .put("en-US", ImmutableMap.of("male", "americano", "female", "americana"))
            .put("es-ES", ImmutableMap.of("male", "espaniol", "female", "espaniola"))
            .put("fr-FR", ImmutableMap.of("male", "francese", "female", "francesa"))
            .put("es-US", ImmutableMap.of("male", "hispano-americano", "female", "hispano-americana"))
            .put("en-GB", ImmutableMap.of("male", "britannico", "female", "britannica"))
            .put("pt-BR", ImmutableMap.of("male", "brasiliano", "female", "brasiliana"))
            .build();

    public IbmVoiceGenerator(String userName, String password) {
        this.userName = userName;
        this.password = password;
        ibmTts  = new WatsonTextToSpeach();
        ibmTts.setUsernameAndPassword(userName, password);
        ES_LAURA.setLanguage("es-ES");
    }

    public IbmVoiceGenerator () {
        //this("f4176739-83a4-41cb-94b6-2ea8e1ee5261","ZzpcCdTFwaVF");
        this("d369d71d-c536-4eb7-b1b5-8bc8fe6daf69","7Gw2M5FL6Rvu");
    }

    @Override
    public List<Voice> getVoices() {
        List<com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice> voices = ibmTts.getVoices();
        List<Voice> apiVoices = new ArrayList<Voice>();
        for (com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice voice : voices) {
            Voice apiVoice = apiVoice(voice);
            apiVoices.add(apiVoice);
        }
        return apiVoices;
    }

    private Voice apiVoice(com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice voice) {
        Voice apiVoice = new Voice();
        apiVoice.setName(voice.getName());
        apiVoice.setLanguage(voice.getLanguage());
        apiVoice.setGender(voice.getGender());

        String voiceName = apiVoice.getName();
        String voiceLanguage = apiVoice.getLanguage();

        String description;

        //generate description
        if(NATIONALITATES.containsKey(voiceLanguage)) {
            description = voiceName.substring(voiceLanguage.length() + 1, voiceName.length() - "Voice".length()) + " (un " + nationalitate(voice.getGender(), voice.getLanguage()) + ")";
        } else {
            description = voice.getDescription();
        }

        apiVoice.setDescription(description);

        //copy url (if present)
        String voiceUrl = voice.getUrl();
        apiVoice.setUrl(voiceUrl);
        apiVoice.setUrl(voiceUrl != null? voiceUrl : WatsonTextToSpeach.URL + "/v1/voices" + "/" + voiceName);
        return apiVoice;
    }

    private String nationalitate(String gender, String language) {
        return NATIONALITATES.get(language).get(gender);
    }

    @Override
    public List<Voice> getGoodVoicesForInterlingua() {
        /*
        List<Voice> allVoices = getVoices();
        List<Voice> goodVoices = new ArrayList<Voice>();
        goodVoices.add(getFromList(allVoices, apiVoice(com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice.IT_FRANCESCA)));
        goodVoices.add(getFromList(allVoices, apiVoice(com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice.ES_ENRIQUE)));
        goodVoices.add(getFromList(allVoices, apiVoice(com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice.ES_LAURA)));
        goodVoices.add(getFromList(allVoices, apiVoice(com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice.FR_RENEE)));
        goodVoices.add(getFromList(allVoices, apiVoice(com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice.ES_SOFIA)));
        goodVoices.add(getFromList(allVoices, apiVoice(com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice.DE_DIETER)));
        */

        List<Voice> goodVoices = new ArrayList<Voice>();
        goodVoices.add(apiVoice(com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice.ES_SOFIA));
        goodVoices.add(apiVoice(com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice.ES_ENRIQUE));
        goodVoices.add(apiVoice(com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice.ES_LAURA));
        goodVoices.add(apiVoice(com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice.IT_FRANCESCA));
        //goodVoices.add(apiVoice(com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice.FR_RENEE));
        //goodVoices.add(apiVoice(com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice.DE_DIETER));

        return goodVoices;
    }

    private <T> T getFromList(List<T> list, T element) {
        return list.get(list.indexOf(element));
    }

    @Override
    public InputStream ssmlToAudio(Voice voice, String text) {
        return ibmTts.synthesize(text, toIbmVoice(voice), HttpMediaType.AUDIO_OGG);
    }

    private com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice toIbmVoice(Voice voice) {
        return new com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice(voice.getName(), voice.getGender(), voice.getLanguage());
    }

    @Override
    public InputStream textAndLexiconToAudio(Voice voice, String text, Map<String, String> graphemePhonemeMap) {
        throw new RuntimeException();
    }

    @Override
    public Voice getDefaultVoice() {
        return getGoodVoicesForInterlingua().get(0);
    }
}
