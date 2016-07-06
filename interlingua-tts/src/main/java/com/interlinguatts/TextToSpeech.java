package com.interlinguatts;

import java.io.OutputStream;
import java.util.Map;

public interface TextToSpeech {
    void textToSpeech(String speechFile, Voice voice, String text);
    void textToSpeech(OutputStream outputStream, Voice voice, String text);
}
