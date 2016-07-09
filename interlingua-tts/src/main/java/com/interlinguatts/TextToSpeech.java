package com.interlinguatts;

import java.io.InputStream;
import java.io.OutputStream;

public interface TextToSpeech {
    void textToSpeech(String speechFile, Voice voice, String text);
    InputStream textToSpeech(Voice voice, String text);
}
