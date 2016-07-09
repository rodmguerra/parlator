package com.interlinguatts;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public interface VoiceGenerator {
    public List<Voice> getVoices();
    public List<Voice> getGoodVoicesForInterlingua();
    InputStream ssmlToAudio(Voice voice, String text);
    InputStream textAndLexiconToAudio(Voice voice, String text, Map<String, String> graphemePhonemeMap);
    public Voice getDefaultVoice();
}
