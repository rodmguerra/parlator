package com.intergrammar.ivona.test.test;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Resources;
import com.interlinguatts.TextToSpeech;
import com.interlinguatts.Voice;
import com.interlinguatts.VoiceGenerator;

import java.io.IOException;
import java.net.URL;
import java.text.Normalizer;
import java.util.List;

public class CarlaAndGiorgioFromFile {

    public static void main(String[] args) throws IOException {


        IvonaTestApplicationContext context = IvonaTestApplicationContext.getInstance();
        VoiceGenerator voiceGenerator = context.voiceGenerator();
        List<Voice> voices = voiceGenerator.getGoodVoicesForInterlingua();
        Voice voice1 = voices.get(0);  //Carla
        Voice voice2 = voices.get(1);  //Giorgio
        TextToSpeech tts = context.tts();

        int[] lessons = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        //int[] lessons = { 9 };

        int firstPhrase = 1;

        for (int lesson : lessons) {
            URL url = Resources.getResource("lesson-" + Strings.padStart(""+(lesson),3,'0') + ".txt");
            List<String> phrases = Resources.readLines(url, Charsets.UTF_8);

            for (int i=0; i<phrases.size(); i++) {
                String directory = "C:\\Users\\rodmguerra\\Desktop\\";
                String fileName = Strings.padStart(""+(lesson),3,'0') + "." + Strings.padStart(""+(i+firstPhrase),3,'0') + ". " + fileName(phrases.get(i)) + ".mp3";
                tts.textToSpeech(directory + "Carla\\" + fileName, voice1, phrases.get(i), tts.getDefaultMediaType());
                tts.textToSpeech(directory + "Giorgio\\" + fileName, voice2, phrases.get(i), tts.getDefaultMediaType());
            }
        }

    }

    private static String fileName(String text) {
        String filename = text;
        filename = Normalizer.normalize(filename, Normalizer.Form.NFD);
        filename = filename.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        filename = filename.replaceAll("[^a-zA-Z0-9.-]", "_");
        int maxFilenameSize = 50;
        if(filename.length() > maxFilenameSize) {
            filename = filename.substring(0,maxFilenameSize);
        }
        return filename;
    }

}
