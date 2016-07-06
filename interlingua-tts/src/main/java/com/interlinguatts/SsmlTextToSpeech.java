package com.interlinguatts;

import java.io.OutputStream;

public class SsmlTextToSpeech extends BaseTextToSpeech implements TextToSpeech {
    private final VoiceGenerator voiceGenerator;
    private final TextToPhonetics textToPhoneticsConverter;

    public SsmlTextToSpeech(VoiceGenerator voiceGenerator, TextToPhonetics textToTextAndLexicon) {
        this.voiceGenerator = voiceGenerator;
        this.textToPhoneticsConverter = textToTextAndLexicon;
    }

    @Override
    public void textToSpeech(OutputStream outputStream, Voice voice, String text) {
        String ssml = textToPhoneticsConverter.textToSsml(text, voice);
        System.out.println(ssml);
        voiceGenerator.ssmlToAudio(ssml, outputStream, voice);
    }
}
