package com.interlinguatts;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LexiconXmlBuilder {
    private static final String LEXICON_TEMPLATE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<lexicon version=\"1.0\" xmlns=\"http://www.w3.org/2005/01/pronunciation-lexicon\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
            "xsi:schemaLocation=\"http://www.w3.org/2005/01/pronunciation-lexicon http://www.w3.org/TR/2007/CR-pronunciation-lexicon-20071212/pls.xsd\" " +
            "alphabet=\"ipa\" xml:lang=\"{0}\">{1}</lexicon>";
    private static final int LEXICON_TEMPLATE_BYTES = getByteUtf8Count(LEXICON_TEMPLATE);

    public static final String LEXEME_TEMPLATE = "<lexeme><grapheme>{0}</grapheme><phoneme>{1}</phoneme></lexeme>";
    private static final int LEXEME_TEMPLATE_BYTES = getByteUtf8Count(LEXEME_TEMPLATE);

    public String toXml(Map<String, String> lexemes, String language) {
        String lexemesXml = "";
        for(String grapheme : lexemes.keySet()) {
            String phoneme = lexemes.get(grapheme);
            lexemesXml = lexemesXml + lexemeXml(grapheme, phoneme);
        }
        return MessageFormat.format(LEXICON_TEMPLATE, language, lexemesXml);
    }

    private static int getByteUtf8Count(String text) {
        try {
            return text.getBytes("UTF-8").length;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> toXml(Map<String, String> lexemes, String language, int bytes) {
        List<String> lexiconXmls = new ArrayList<String>();

        //init variables
        int byteCount = LEXICON_TEMPLATE_BYTES;
        String lexemesXml = "";

        for(String grapheme : lexemes.keySet()) {
            String phoneme = lexemes.get(grapheme);
            String lexemeXml = lexemeXml(grapheme, phoneme);
            int lexemeLength = getByteUtf8Count(lexemeXml);
            if(byteCount + lexemeLength > bytes) {
                //finish lexicon and add to list
                String lexiconXml = MessageFormat.format(LEXICON_TEMPLATE, language, lexemesXml);
                lexiconXmls.add(lexiconXml);

                //reset variables, starting new lexicon
                byteCount = LEXICON_TEMPLATE_BYTES;
                lexemesXml = "";
            }

            lexemesXml += lexemeXml;
            byteCount += lexemeLength;
        }

        String lexiconXml = MessageFormat.format(LEXICON_TEMPLATE, language, lexemesXml);
        lexiconXmls.add(lexiconXml);

        return lexiconXmls;
    }

    private String lexemeXml(String grapheme, String phoneme) {
        return MessageFormat.format(LEXEME_TEMPLATE, grapheme, phoneme);
    }
}
