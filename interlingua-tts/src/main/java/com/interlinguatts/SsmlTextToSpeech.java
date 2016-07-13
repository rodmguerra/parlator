package com.interlinguatts;

import java.io.InputStream;

public class SsmlTextToSpeech extends BaseTextToSpeech implements TextToSpeech {
    private final VoiceGenerator voiceGenerator;
    private final TextToPhonetics textToPhoneticsConverter;
    private final PhoneticAlphabet alphabet;

    public SsmlTextToSpeech(VoiceGenerator voiceGenerator, TextToPhonetics textToTextAndLexicon, PhoneticAlphabet alphabet) {
        this.voiceGenerator = voiceGenerator;
        this.textToPhoneticsConverter = textToTextAndLexicon;
        this.alphabet = alphabet;
    }

    @Override
    public InputStream textToSpeech(Voice voice, String text, MediaType mediaType) {
        String ssml = textToPhoneticsConverter.textToSsml(text, voice, alphabet);
        System.out.println(ssml);
        return voiceGenerator.ssmlToAudio(voice, ssml, mediaType);
    }

    @Override
    public MediaType getDefaultMediaType() {
        return voiceGenerator.getAvailableMediaTypes().get(0);
    }
}
