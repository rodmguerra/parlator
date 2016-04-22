package com.interlinguatts.domain;

public class Word {
    private final String word;
    private final String respell;
    private final String wordClass;

    public Word(String word, String respell, String wordClass) {
        this.word = word;
        this.respell = respell!=null ? respell : word;
        this.wordClass = wordClass;
    }

    public String getWord() {
        return word;
    }

    public String getRespell() {
        return respell;
    }

    public String getWordClass() {
        return wordClass;
    }
}

