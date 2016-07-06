package com.interlinguatts;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public interface VoiceGenerator {
    public List<Voice> getVoices();
    public List<Voice> getGoodVoicesForInterlingua();
    void ssmlToAudio(String text, OutputStream outputStream, Voice voice);
    void textAndLexiconToAudio(OutputStream text, Voice outputStream, String voice, Map<String, String> graphemePhonemeMap);
    public Voice getDefaultVoice();
}
