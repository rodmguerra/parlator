package com.interlinguatts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public abstract class BaseTextToSpeech implements TextToSpeech {
    @Override
    public void textToSpeech(String speechFileName, Voice voice, String text) {
        try {
            OutputStream outputStream = new FileOutputStream(new File(speechFileName));
            textToSpeech(outputStream, voice, text);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
