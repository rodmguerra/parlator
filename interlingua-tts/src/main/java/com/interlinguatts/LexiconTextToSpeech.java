package com.interlinguatts;

import java.io.InputStream;

public class LexiconTextToSpeech extends BaseTextToSpeech implements TextToSpeech {
    private final VoiceGenerator voiceGenerator;
    private final TextToPhonetics textToPhonetics;

    public LexiconTextToSpeech(VoiceGenerator voiceGenerator, TextToPhonetics textToPhonetics) {
        this.voiceGenerator = voiceGenerator;
        this.textToPhonetics = textToPhonetics;
    }

    @Override
    public InputStream textToSpeech(Voice voice, String text) {
        TextAndLexicon textAndLexicon = textToPhonetics.textAndLexicon(text, voice);
        return voiceGenerator.textAndLexiconToAudio(voice, text, textAndLexicon.getLexicon());
    }
}
