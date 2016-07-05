package com.interlinguatts;

import java.io.OutputStream;

public interface TextToSpeach {
    void textToSpeech(String text, String speechFile, Voice voice);
    void textToSpeech(String text, OutputStream outputStream, Voice voice);
}
