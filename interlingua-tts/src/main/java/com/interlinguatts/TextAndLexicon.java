package com.interlinguatts;

import java.util.Map;

public class TextAndLexicon {
    private final String text;
    private final Map<String,String> lexicon;

    public TextAndLexicon(String text, Map<String, String> lexicon) {
        this.text = text;
        this.lexicon = lexicon;
    }

    public String getText() {
        return text;
    }

    public Map<String, String> getLexicon() {
        return lexicon;
    }
}