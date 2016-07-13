package com.interlinguatts;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public interface VoiceGenerator {
    public Voice getDefaultVoice();
    public List<Voice> getVoices();
    public List<Voice> getGoodVoicesForInterlingua();

    InputStream textAndLexiconToAudio(Voice voice, String text, Map<String, String> graphemePhonemeMap, MediaType mediaType);
    InputStream ssmlToAudio(Voice voice, String text, MediaType mediaType);

    List<MediaType> getAvailableMediaTypes();
}
