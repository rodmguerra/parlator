package com.interlinguatts;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.ivona.services.tts.model.Voice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.interlinguatts.InterlinguaIpaProvider.padRight;

public class InterlinguaTextToSpeach implements TextToSpeach {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private final InterlinguaIpaProvider provider;
    private final IvonaFacade ivonaFacade;
    private final InterlinguaNumberWriter numberWriter;


    private String replaceSeparatedOrdinal(String input) {
        String patternString = "(?<![\\.,0-9])(([1-9][0-9]{0,2}|0)(\\.[0-9]{3})*((?<=[017])me|(?<=2)nde|(?<=3)tie|(?<=[456])te|(?<=8)ve|(?<=9)ne))([s]?)(?!\\p{L})";
        StringBuffer output = new StringBuffer();
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            String numberString = matcher.group(1);
            String replacement = numberWriter.writeOrdinal(new BigInteger(numberString.replaceAll("[^0-9]", "")));
            matcher.appendReplacement(output, replacement + matcher.group(5));
        }
        matcher.appendTail(output);
        return output.toString();
    }

    private String replaceSeparatedOrdinalAdverb(String input) {
        //não é ordinal se tiver outra letra grudada no final (que não é o s do plural)
        String patternString = "(?<![\\.,0-9])(([1-9][0-9]{0,2}|0)(\\.[0-9]{3})*((?<=[017])mo|(?<=2)ndo|(?<=3)tio|(?<=[456])to|(?<=8)vo|(?<=9)no))(?!\\p{L})";
        StringBuffer output = new StringBuffer();
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            String numberString = matcher.group();
            String replacement = numberWriter.writeOrdinalAdverb(new BigInteger(numberString.replaceAll("[^0-9]", "")));
            matcher.appendReplacement(output, replacement);
        }
        matcher.appendTail(output);
        return output.toString();
    }


    private String replaceNonSeparatedOrdinal(String input) {
        //não é ordinal se tiver outra letra grudada no final (que não é o s do plural)
        String patternString = "(?<![\\.,0-9])([0-9]*[017]me|[0-9]*[2]nde|[0-9]*[3]tie|[0-9]*[456]te|[0-9]*8ve|[0-9]*9ne)([s]?)(?!\\p{L})";
        StringBuffer output = new StringBuffer();
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            String numberString = matcher.group(1);
            String replacement = numberWriter.writeOrdinal(new BigInteger(numberString.replaceAll("[^0-9]", "")));
            matcher.appendReplacement(output, replacement + matcher.group(2));
        }
        matcher.appendTail(output);
        return output.toString();
    }

    private String replaceNonSeparatedOrdinalAdverb(String input) {
        //não é ordinal se tiver outra letra grudada no final
        String patternString = "(?<![\\.,0-9])([0-9]*[017]mo|[0-9]*[2]ndo|[0-9]*[3]tio|[0-9]*[456]to|[0-9]*8vo|[0-9]*9no)(?!\\p{L})";
        StringBuffer output = new StringBuffer();
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            String numberString = matcher.group();
            String replacement = numberWriter.writeOrdinalAdverb(new BigInteger(numberString.replaceAll("[^0-9]", "")));
            matcher.appendReplacement(output, replacement);
        }
        matcher.appendTail(output);
        return output.toString();
    }

    private String replaceSeparatedCardinal(String input) {  //max 4 digit
        StringBuffer output = new StringBuffer();
        Pattern pattern = Pattern.compile("(?<![\\.,0-9])(([1-9][0-9]{0,2}|0)(\\.[0-9]{3})*(\\,[0-9]+)?)(?![\\.,]?[0-9])");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            String numberString = matcher.group();
            String replacement = numberWriter.write(new BigDecimal(numberString.replace(".", "").replace(",", ".")));
            matcher.appendReplacement(output, replacement);
        }
        matcher.appendTail(output);
        return output.toString();

        //|(^| )[1-9][0-9]{0,3}($| )
    }

    private String replaceLimitedNonSeparatedCardinal(String input) {  //max 4 digit
        StringBuffer output = new StringBuffer();
        Pattern pattern = Pattern.compile("(?<![\\.,0-9])([1-9][0-9]{0,3}(\\,[0-9]+)?)(?![\\.,]?[0-9])");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            String numberString = matcher.group();
            String replacement = numberWriter.write(new BigDecimal(numberString.replace(".", "").replace(",", ".")));
            matcher.appendReplacement(output, replacement);
        }
        matcher.appendTail(output);
        return output.toString();

        //|(^| )[1-9][0-9]{0,3}($| )
    }

    public static void main(String[] args) {
        InterlinguaTextToSpeach tts = new InterlinguaTextToSpeach(new IvonaFacade(),null, new InterlinguaNumberWriter());
        //String text = "2.500me, le 10000me ha apportate depost de9.999nesj. Ille ha2.456,777 annos. Su telephone es45646546545646565465";

        //String text = "Entre parênteses (=), palavras=grudadas, grudada= palavra, palavra =grudada. Solto = Solto";

        //text = replaceSymbols(text);
        //System.out.println(text);

        String test = "a - 5 + 4 -3 +222-";
        System.out.println(test);
        test = tts.replaceNumbers(test);



        System.out.println(test);
    }

    private static String replaceSymbols(String text) {
        text = text.replaceAll("(\\p{L}+)\\+(\\p{L}+)", "$1 plus $2");
        text = text.replaceAll("\\+(\\p{L}+)", " plus $1");
        text = text.replaceAll("(\\p{L}+)\\+", "$1 plus ");
        text = text.replaceAll("\\+", " plus ");

        text = text.replaceAll("(\\p{L}+)=(\\p{L}+)", "$1 es $2");
        text = text.replaceAll("=(\\p{L}+)", " es $1");
        text = text.replaceAll("(\\p{L}+)=", "$1 es ");
        text = text.replaceAll("=", " es ");

        text = text.replaceAll("(\\p{L}+)>(\\p{L}+)", "$1 major que $2");
        text = text.replaceAll(">(\\p{L}+)", " major que $1");
        text = text.replaceAll("(\\p{L}+)>", "$1 major que ");
        text = text.replaceAll(">", " major que ");

        text = text.replaceAll("(\\p{L}+)<(\\p{L}+)", "$1 minor que $2");
        text = text.replaceAll("<(\\p{L}+)", " minor que $1");
        text = text.replaceAll("(\\p{L}+)<", "$1 minor que ");
        text = text.replaceAll("<", " minor que ");

        text = text.replaceAll("(\\p{L}+)×(\\p{L}+)", "$1 vices $2");
        text = text.replaceAll("×(\\p{L}+)", " vices $1");
        text = text.replaceAll("(\\p{L}+)×", "$1 vices ");
        text = text.replaceAll("×", " vices ");

        text = text.replaceAll("(\\p{L}+)%(\\p{L}+)", "$1 per cento $2");
        text = text.replaceAll("%(\\p{L}%)", " per cento $1");
        text = text.replaceAll("(\\p{L}+)%", "$1 per cento ");
        text = text.replaceAll("%", " per cento ");

        return text;
    }

    private String replaceNumbers(String text) {
        text = text.replaceAll("(?s)(\\p{L})\\-\\s*([0-9]+)", "$1 minus $2");
        text = text.replaceAll("(?s)\\-\\s*([0-9]+)", " minus $1");
        text = text.replaceAll("(?s)([0-9]+\\s*)\\-", "$1 minus ");
        text = separatePrecedingLettersFromNumbers(text);
        text = replaceOrdinals(text);
        text = separateFollowingLettersFromNumbers(text);
        text = replaceCardinals(text);
        text = replaceOtherNumbers(text);
        text = replaceSymbols(text);
        return text;
    }

    private String replaceCardinals(String test) {
        test = replaceSeparatedCardinal(test);
        test = replaceLimitedNonSeparatedCardinal(test);
        return test;
    }

    private String replaceOrdinals(String test) {
        test = replaceSeparatedOrdinal(test);

        test = replaceSeparatedOrdinalAdverb(test);

        test = replaceNonSeparatedOrdinal(test);

        test = replaceNonSeparatedOrdinalAdverb(test);
        return test;
    }

    private static String separateFollowingLettersFromNumbers(String test) {
        return test.replaceAll("([0-9])(\\p{L})", "$1 $2");
    }

    private static String separatePrecedingLettersFromNumbers(String test) {
        test = test.replaceAll("(\\p{L})([0-9])", "$1 $2");
        return test;
    }

    private String replaceOtherNumbers(String input) {

        //eliminate commas and points
        input = input.replaceAll("([0-9])\\.([0-9])", "$1 puncto $2");
        input = input.replaceAll("([0-9]),([0-9])", "$1 comma $2");
        input = input.replaceAll("\\.([0-9])", " puncto $1");
        input = input.replaceAll(",([0-9])", " comma $1");

        StringBuffer output = new StringBuffer();
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            String numberString = matcher.group();
            String replacement = numberWriter.writeDigitwise(numberString);
            matcher.appendReplacement(output, replacement);
        }
        matcher.appendTail(output);
        return output.toString();
    }

    public InterlinguaTextToSpeach(IvonaFacade ivonaFacade, InterlinguaIpaProvider provider, InterlinguaNumberWriter numberWriter) {
        this.ivonaFacade = ivonaFacade;
        this.provider = provider;
        this.numberWriter = numberWriter;
    }


    public Map<String,String> graphemePhonemeMap(String text, Voice voice) {
        TreeMap<String, String> graphemePhonemeMap = new TreeMap<String, String>();

        String intermediateText = text.replaceAll("[.,;:?!“”\"'\\-–\\)\\]\\}]+ ", " ");  //special + space
        intermediateText = intermediateText.replaceAll("[.,;:?!“”\"'\\-–\\)\\]\\}]+$", " ");  //special + end
        intermediateText = intermediateText.replaceAll("[.,;:?!“”\"'\\-–\\)\\]\\}]+\n", " \n");  //special + line end
        intermediateText = intermediateText.replaceAll(" [“”\"'\\-–\\(\\[\\{]+", " ");  //space + quotes
        intermediateText = intermediateText.replaceAll("^[“”\"'\\-–\\(\\[\\{]+", " ");  //start + quotes
        intermediateText = intermediateText.replaceAll("\n[“”\"'\\-–\\(\\[\\{]+", "\n ");  //line start + quotes

        String[] words = intermediateText.split("[ &\n]");
        for(String word : words) {
            if(word.isEmpty()) {
                continue;
            }

            String bugFix = fixVoiceBugsBefore(voice, word, words.length);
            String ipa = (bugFix==null)? provider.toIpa(word, words.length == 1) : bugFix;
            ipa = fixVoiceBugsAfter(voice, ipa);
            graphemePhonemeMap.put(word, ipa);
        }
        Map<String,String> descending = graphemePhonemeMap.descendingMap();
        for (String word : descending.keySet()) {
            String ipa = descending.get(word);
            System.out.println("word=\"" + padRight(word + "\"",30)  + "ipa=\"" + padRight(ipa + "\"", 30));
        }
        return descending;
    }

    public void textToSpeech(String text, String speechFileName, Voice voice) {
        try {
            OutputStream outputStream = new FileOutputStream(new File(speechFileName));
            textToSpeech(text, outputStream, voice);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String fixVoiceBugsBefore(Voice voice, String word, int length) {
        if(voice.getName().equals("Carla") && length > 1) {
            Map<String,String> bugWords = ImmutableMap.of(
                    "cata","kata",
                    "multo","multo",
                    "multe","multe",
                    "multes","multes"
            );
            for (String bugWord : bugWords.keySet()) {
                if(word.equalsIgnoreCase(bugWord)) {
                    return bugWords.get(bugWord);
                }
            }
        }
        return null;
    }

    private String fixVoiceBugsAfter(Voice voice, String ipa) {
        if(voice.getName().equals("Carla")) {
            //ipa = ipa.replaceAll("([^ˈ]*)([ˈ]?[^aeiou]*ʒa)", "$1 $2");  //...aja...
            ipa = ipa.replaceAll("(ˈ[^aeiou]*)(d͡)?ʒa", "$1d͡ʒa"); //já => djá
            ipa = ipa.replaceAll("(d͡)?ʒa([^aeiou]*)\\b", "d͡ʒa$2"); //___ja => _____dja, _____jax => ______djax
            ipa = ipa.replaceAll("(oˈ?)ʒ", "$1d͡ʒ");    //oj => odj
            return ipa;
        } else if(voice.getName().equals("Giorgio")) {
            ipa = ipa.replaceAll("(oˈ?)ʒ", "$1d͡ʒ");    //oj => odj
            ipa = ipa.replaceAll("(ˈ?)(d͡)?ʒe\\B", "$1d͡ʒe");    //je => dje
            ipa = ipa.replaceAll("ˈʒe", "ˈd͡ʒe");
            return ipa;
        }
        return ipa;
    }

    public void textToSpeech(String text, OutputStream outputStream, Voice voice) {

        text = text.replaceAll("(?s)(^|[^0-9\\s])(\\s+)[\\-–]+(\\s*)($|[^0-9\\s])", "$1,$2$3$4");
                                                                      //parola - parola => parola, parola
                                                                      //parola -parola => parola, parola
                                                                      //keep: parola-parola => parola-parola
                                                                      //keep: -1-1- - 1 - 1 -
        text = text.replaceAll("(?s)(^|[^0-9\\s])(\\s*)[\\-–]+(\\s+)($|[^0-9\\s])", "$1,$2$3$4");
                                                                      //parola - parola => parola, parola
                                                                      //parola- parola => parola, parola
                                                                      //keep: -1-1- - 1 - 1 -


        text = text.replaceAll("(?s)^\\s*[\\-–]+\\s*([^0-9\\s])", "$1");   //-parola        //- parola
        text = text.replaceAll("(?s)([^0-9\\s])\\s*[\\-–]+\\s*$", "$1");   //parola-        //parola -

        String lastText = "";
        while (!lastText.equals(text)) {
            lastText = text;
            text = text.replaceAll("\\b[']\\B+", "");    //''''''parola => parola, keep parola'parola
            text = text.replaceAll("\\B[']+\\b", "");    //parola'''''' => parola, keep parola'parola
            text = text.replaceAll("(?s)(?<!^)\\s*\\(([^\\(]+)\\)", ", $1, ");
            text = text.replaceAll("(?s)(?<!^)\\s*\\[([^\\[]+)\\]", ", $1, ");
            text = text.replaceAll("(?s)(?<!^)\\s*\\{([^\\{]+)\\}", ", $1, ");
        }

        text = text.replaceAll("\\b@\\b", ", ad, ");
        text = text.replaceAll("\\b@\\B", ", ad");
        text = text.replaceAll("\\B@\\b", "ad, ");
        text = text.replaceAll("\\B@\\B", "ad");

        text = replaceNumbers(text);
        text = replaceAbbreviations(text);

        //text = text.replaceAll("\\b\\.\\b", ", puncto, "); //good for emails, bad for U.S.A.

        text = text.replaceAll("(?s)\\B[\\-–]+\\B", " minus ");

        Map<String, String> graphemePhonemeMap = graphemePhonemeMap(text, voice);



        textToSpeechSynchronizedPart(text, graphemePhonemeMap, voice, outputStream);
    }

    private String replaceAbbreviations(String text) {
        text = text.replaceAll("sr\\.", "senior");
        text = text.replaceAll("sra\\.", "seniora");
        text = text.replaceAll("Sr\\.", "senior");
        text = text.replaceAll("Sra\\.", "seniora");
        text = text.replaceAll("etc\\.", "etcetera");
        text = text.replaceAll("Etc\\.", "Etcetera");
        return text;
    }

    private synchronized void textToSpeechSynchronizedPart(String text, Map<String, String> graphemePhonemeMap, Voice voice, OutputStream outputStream) {
        List<String> lexiconNames = putLexiconSafe(graphemePhonemeMap, voice.getLanguage());
        System.out.println(text);
        ivonaFacade.textToSpeech(voice, text, outputStream, lexiconNames);
    }

    private String lexiconNameFromTimestamp() {
        return DATE_FORMAT.format(new Date());
    }

    private List<String> putLexiconSafe(Map<String, String> lexemes, String language) {

        LexiconXmlBuilder lexiconXmlBuilder = new LexiconXmlBuilder();
        List<String> lexicons = lexiconXmlBuilder.toXml(lexemes, language, 4096);
        int lexiconCount = lexicons.size();
        int maxLexiconSlotCount = 5;

        if(lexiconCount > maxLexiconSlotCount) {
            throw new RuntimeException(String.format("Too much lexicons! needed: %s available: %s", lexiconCount, maxLexiconSlotCount));
        }

        SortedSet<String> existentLexiconNames = Sets.newTreeSet(ivonaFacade.getLexiconNames());
        int emptySlotCount = maxLexiconSlotCount - existentLexiconNames.size();
        for(int i=0; i<lexiconCount-emptySlotCount; i++) {
            String oldestLexiconName = existentLexiconNames.first();
            existentLexiconNames.remove(oldestLexiconName);
            ivonaFacade.deleteLexicon(oldestLexiconName);
        }

        List<String> lexiconNames = new ArrayList<String>();
        for(String lexicon : lexicons) {
            String lexiconName = lexiconNameFromTimestamp();
            ivonaFacade.putLexicon(lexiconName, lexicon);
            lexiconNames.add(lexiconName);
        }
        return lexiconNames;
    }
}
