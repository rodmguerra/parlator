package com.interlinguatts;

import java.io.OutputStream;

public class LexiconTextToSpeech extends BaseTextToSpeech implements TextToSpeech {
    private final VoiceGenerator voiceGenerator;
    private final TextToPhonetics textToPhonetics;

    public LexiconTextToSpeech(VoiceGenerator voiceGenerator, TextToPhonetics textToPhonetics) {
        this.voiceGenerator = voiceGenerator;
        this.textToPhonetics = textToPhonetics;
    }

    @Override
    public void textToSpeech(OutputStream outputStream, Voice voice, String text) {
        TextAndLexicon textAndLexicon = textToPhonetics.textAndLexicon(text, voice);
        voiceGenerator.textAndLexiconToAudio(outputStream, voice, text, textAndLexicon.getLexicon());
    }
}
