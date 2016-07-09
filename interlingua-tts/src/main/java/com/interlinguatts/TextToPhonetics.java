package com.interlinguatts;

import com.google.common.xml.XmlEscapers;

import java.util.Map;
import java.util.TreeMap;

public class TextToPhonetics {
    public static final String VALID_IPA = "a-zˈ͡ʃʒʎβɾcgh";
    private final WordToPhonetics provider;
    private final VoiceBugFixer voiceBugFixer;
    private final InterlinguaTtsPreProcessor preProcessor;


    public TextToPhonetics(WordToPhonetics provider, VoiceBugFixer voiceBugFixer, InterlinguaTtsPreProcessor preProcessor) {
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
            System.out.println("word=\"" + WordToPhonetics.padRight(word + "\"", 30) + "ipa=\"" + WordToPhonetics.padRight(ipa + "\"", 30));
        }

        return new TextAndLexicon(text, descendingLexicon);
    }

    private String[] extractWords(String text) {
        String intermediateText = text.replaceAll("[.,;:?!“”‘’\"'\\-–\\)\\]\\}]+ ", " ");  //special + space
        intermediateText = intermediateText.replaceAll("[.,;:?!“”\"'\\-–\\)\\]\\}]+$", " ");  //special + end
        intermediateText = intermediateText.replaceAll("[.,;:?!“”\"'\\-–\\)\\]\\}]+\n", " \n");  //special + line end
        intermediateText = intermediateText.replaceAll(" [“”\"'\\-–\\(\\[\\{]+", " ");  //space + quotes
        intermediateText = intermediateText.replaceAll("^[“”\"'\\-–\\(\\[\\{]+", " ");  //start + quotes
        intermediateText = intermediateText.replaceAll("\n[“”\"'\\-–\\(\\[\\{]+", "\n ");  //line start + quotes
        return intermediateText.split("[ &\n]");
    }

    public String textToSsml(String text, Voice voice, PhoneticAlphabet phoneticAlphabet) {
        TextAndLexicon textAndLexicon = textAndLexicon(text, voice);
        text = textAndLexicon.getText();
        Map<String, String> lexicon = textAndLexicon.getLexicon();
        if(phoneticAlphabet == PhoneticAlphabet.XSAMPA) {
            for (String word : lexicon.keySet()) {
                String phoneme = lexicon.get(word);
                text = text.replaceAll("\\b"+ word +"\\b(?!([^<]+)?>)", "<phoneme alphabet=\"x-sampa\" ph=\""+ XmlEscapers.xmlAttributeEscaper().escape(toXsampa(phoneme)) +"\"/>" );
            }
        } else {
            for (String word : lexicon.keySet()) {
                String phoneme = lexicon.get(word);
                text = text.replaceAll("\\b"+ word +"\\b(?!([^<]+)?>)", XmlEscapers.xmlAttributeEscaper().escape(phoneme));  //not inside tags
            }
            text = text.replaceAll("(["+ VALID_IPA +"]+["+ VALID_IPA+"\\s]*)", "<phoneme alphabet=\"ipa\" ph=\"$1\"/>");
        }
        return text;
    }

    public static void main(String[] args) {
        String text = "le ˈdʒuβene seˈnjor reˈgwarda le ˈdʒuvene ˈdama. Ila es.";
        System.out.println(text.replaceAll("([" + VALID_IPA + "]+[" + VALID_IPA + "\\s]*)", "<phoneme alphabet=\"ipa\" ph=\"$1\"/>"));;
    }

    private String toXsampa(String phoneme) {
        phoneme = phoneme.replaceAll("ˈ", "\""); //stress
        phoneme = phoneme.replaceAll("͡", ""); //double
        phoneme = phoneme.replaceAll("ʒ", "Z"); //special
        phoneme = phoneme.replaceAll("ʃ", "S"); //special
        phoneme = phoneme.replaceAll("β", "B"); //special
        phoneme = phoneme.replaceAll("ʎ", "L"); //special
        return phoneme;
    }

}