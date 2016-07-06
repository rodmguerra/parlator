package com.interlinguatts;

import java.util.Map;
import java.util.TreeMap;

public class TextToPhonetics {
    private final InterlinguaIpaProvider provider;
    private final VoiceBugFixer voiceBugFixer;
    private final InterlinguaTtsPreProcessor preProcessor;


    public TextToPhonetics(InterlinguaIpaProvider provider, VoiceBugFixer voiceBugFixer, InterlinguaTtsPreProcessor preProcessor) {
        this.provider = provider;
        this.voiceBugFixer = voiceBugFixer;
        this.preProcessor = preProcessor;
    }

    public TextAndLexicon textAndLexicon(String text, Voice voice) {
        text = preProcessor.preProcessText(text);
        TreeMap<String, String> graphemePhonemeMap = new TreeMap<String, String>();

        String[] words = extractWords(text);

        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            String bugFix = voiceBugFixer.getForcingIpaFix(voice, word, words.length);
            String ipa = (bugFix == null) ? provider.toIpa(word, words.length == 1) : bugFix;
            ipa = voiceBugFixer.getChangingIpaFix(voice, ipa);
            graphemePhonemeMap.put(word, ipa);
        }

        Map<String, String> descendingLexicon = graphemePhonemeMap.descendingMap();
        //log map
        for (String word : descendingLexicon.keySet()) {
            String ipa = descendingLexicon.get(word);
            System.out.println("word=\"" + InterlinguaIpaProvider.padRight(word + "\"", 30) + "ipa=\"" + InterlinguaIpaProvider.padRight(ipa + "\"", 30));
        }

        return new TextAndLexicon(text, descendingLexicon);
    }

    private String[] extractWords(String text) {
        String intermediateText = text.replaceAll("[.,;:?!“”\"'\\-–\\)\\]\\}]+ ", " ");  //special + space
        intermediateText = intermediateText.replaceAll("[.,;:?!“”\"'\\-–\\)\\]\\}]+$", " ");  //special + end
        intermediateText = intermediateText.replaceAll("[.,;:?!“”\"'\\-–\\)\\]\\}]+\n", " \n");  //special + line end
        intermediateText = intermediateText.replaceAll(" [“”\"'\\-–\\(\\[\\{]+", " ");  //space + quotes
        intermediateText = intermediateText.replaceAll("^[“”\"'\\-–\\(\\[\\{]+", " ");  //start + quotes
        intermediateText = intermediateText.replaceAll("\n[“”\"'\\-–\\(\\[\\{]+", "\n ");  //line start + quotes
        return intermediateText.split("[ &\n]");
    }

    public String textToSsml(String text, Voice voice) {
        TextAndLexicon textAndLexicon = textAndLexicon(text, voice);
        text = textAndLexicon.getText();
        Map<String, String> lexicon = textAndLexicon.getLexicon();
        for (String word : lexicon.keySet()) {
            String phoneme = lexicon.get(word);
            text = text.replaceAll("\\b"+ word +"\\b", "<phoneme alphabet=\"ipa\" ph=\""+ phoneme +"\"/>" );
        }
        return text;
    }
}