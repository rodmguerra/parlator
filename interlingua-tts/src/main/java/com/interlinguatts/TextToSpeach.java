package com.interlinguatts;

import com.ivona.services.tts.model.Voice;

public interface TextToSpeach {
    void textToSpeech(String text, String speechFile, Voice voice);
}
