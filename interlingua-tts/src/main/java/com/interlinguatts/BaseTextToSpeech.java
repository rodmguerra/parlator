package com.interlinguatts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public abstract class BaseTextToSpeech implements TextToSpeech {
    @Override
    public void textToSpeech(String fileName, Voice voice, String text, MediaType mediaType) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        Map<String, String> errorMessageMap = null;
        try {
            inputStream = textToSpeech(voice, text, mediaType);
            //
            byte[] buffer = new byte[2 * 1024];
            int readBytes;
            outputStream = new FileOutputStream(new File(fileName));

            while ((readBytes = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, readBytes);
            }

            //System.out.println("\nFile saved: " + outputFileName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {

            //close inputstream
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            //close outputsteam
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void textToSpeech(String speechFile, Voice voice, String text) {
        textToSpeech(speechFile, voice, text, getDefaultMediaType());
    }

    @Override
    public InputStream textToSpeech(Voice voice, String text) {
        return textToSpeech(voice, text, getDefaultMediaType());
    }
}
