package com.interlinguatts;

import com.interlinguatts.domain.Word;
import com.interlinguatts.repository.Repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.Normalizer;

public class WordToPhonetics {

    private final Repository<Word> wordRepository;
    private final InterlinguaNumberWriter numberWriter;

    public WordToPhonetics(Repository<Word> wordRepository, InterlinguaNumberWriter numberWriter) {
        this.wordRepository = wordRepository;
        this.numberWriter = numberWriter;
    }

    public String toIpa(String wordText, boolean singleWord) {
        //wordText = treatNumbers(wordText);

        Word myWord = wordRepository.findByWord(wordText);
        //more than one word to a same expression, convert separately

        if (myWord == null) {
            //ignorar caracteres estranhos no meio da palavra, tratá-los como separadores
            String wordString = wordText.replaceAll("[.,;:?!“”\"'\\-–]", " ");
            String[] wordStrings = wordString.split(" ");
            if (wordStrings.length > 1) {
                String ipa = "";
                for (String currentWord : wordStrings) {
                    ipa += " " + toIpa(currentWord, false);
                }
                return ipa;
            }
        }

        String wordRespell = null;
        if (myWord != null) {
            wordRespell = myWord.getRespell();
        } else {
            wordRespell = respellDerivateWord(wordText, wordRespell);
            if (wordRespell == null) wordRespell = wordText;
        }

        String phoneticInvariant = toPhoneticInvariant(wordRespell, singleWord);
        return phoneticInvariantToIpa(phoneticInvariant, singleWord);
    }

    private String treatNumbers(String wordText) {
        if (isCardinalNumber(wordText)) {
            wordText = numberWriter.write(new BigDecimal(wordText.replace(",",".")));
        } else if (isOrdinalNumber(wordText)) {
            wordText = numberWriter.writeOrdinal(new BigInteger(wordText.replaceAll("[^0-9]","")));
        } else if(isOrdinalNumberAdverb(wordText)) {
            wordText = numberWriter.writeOrdinalAdverb(new BigInteger(wordText.replaceAll("[^0-9]","")));
        } else {
            //isolates number to speak separate
            wordText = wordText.replaceAll("([0-9]+)", " $1 ");
            if(wordText.startsWith(" ")) wordText = wordText.substring(1);
        }
        return wordText;
    }

    private boolean isOrdinalNumberAdverb(String wordText) {
        return wordText.matches("[0-9]*[017]mo") ||
                wordText.matches("[0-9]*[2]ndo") ||
                wordText.matches("[0-9]*[3]tio") ||
                wordText.matches("[0-9]*[456]to") ||
                wordText.matches("[0-9]*8vo") ||
                wordText.matches("[0-9]*9no");
    }

    private boolean isCardinalNumber(String wordText) {
        return wordText.matches("[0-9]+[,]?[0-9]*");
    }

    private boolean isOrdinalNumber(String wordText) {
        return wordText.matches("[0-9]*[017]me") ||
                    wordText.matches("[0-9]*[2]nde") ||
                    wordText.matches("[0-9]*[3]tie") ||
                    wordText.matches("[0-9]*[456]te") ||
                    wordText.matches("[0-9]*8ve") ||
                    wordText.matches("[0-9]*9ne");
    }

    private String respellDerivateWord(String wordText, String wordRespell) {
        //tempores
        if (wordRespell == null && endsWithOneOf(wordText, "ava", "eva", "iva")) wordRespell = respellChangingSuffix(wordText, "r", "va");
        if (wordRespell == null && endsWithOneOf(wordText, "ara", "era", "ira")) wordRespell = respellChangingSuffix(wordText, "r", "rā");
        if (wordRespell == null && endsWithOneOf(wordText, "area", "erea", "irea")) wordRespell = respellChangingSuffix(wordText, "r", "rēa");

        //participio
        if (wordRespell == null && wordText.endsWith("ate")) wordRespell = respellChangingSuffix(wordText, "ar", "ate");
        if (wordRespell == null && wordText.endsWith("ite")) wordRespell = respellChangingSuffix(wordText, "er", "ite");
        if (wordRespell == null && wordText.endsWith("ite")) wordRespell = respellChangingSuffix(wordText, "ir", "ite");

        //gerundio
        if (wordRespell == null && wordText.endsWith("ante"))
            wordRespell = respellChangingSuffix(wordText, "ar", "ante");
        if (wordRespell == null && wordText.endsWith("iente"))
            wordRespell = respellChangingSuffix(wordText, "ir", "iente");
        if (wordRespell == null && wordText.endsWith("iente"))
            wordRespell = respellChangingSuffix(wordText, "er", "iente");
        if (wordRespell == null && wordText.endsWith("ente"))
            wordRespell = respellChangingSuffix(wordText, "er", "ente");

        //presente
        if (wordRespell == null && (wordText.endsWith("a") || wordText.endsWith("e") || wordText.endsWith("i")))
            wordRespell = respellChangingSuffix(wordText, "r", "");

        //plural
        if (wordRespell == null && wordText.endsWith("ches"))
            wordRespell = respellChangingSuffixKeepStress(wordText, "c", "ches");
        if (wordRespell == null && wordText.endsWith("es"))
            wordRespell = respellChangingSuffixKeepStress(wordText, "", "es");
        if (wordRespell == null && wordText.endsWith("s"))
            wordRespell = respellChangingSuffixKeepStress(wordText, "", "s");

        //issime
        if(wordRespell == null && wordText.endsWith("issime")) {
            wordRespell = respellChangingSuffix(wordText, "a", "īssime");
        }
        if(wordRespell == null && wordText.endsWith("issime")) {
            wordRespell = respellChangingSuffix(wordText, "e", "īssime");
        }
        if(wordRespell == null && wordText.endsWith("issime")) {
            wordRespell = respellChangingSuffix(wordText, "i", "īssime");
        }
        if(wordRespell == null && wordText.endsWith("issime")) {
            wordRespell = respellChangingSuffix(wordText, "o", "īssime");
        }
        if(wordRespell == null && wordText.endsWith("issime")) {
            wordRespell = respellChangingSuffix(wordText, "u", "īssime");
        }
        if(wordRespell == null && wordText.endsWith("issime")) {
            wordRespell = respellChangingSuffix(wordText, "y", "īssime");
        }
        if(wordRespell == null && wordText.endsWith("issime")) {
            wordRespell = respellChangingSuffixEvenIfNotFound(wordText, "", "īssime");
        }
        if(wordRespell == null && wordText.endsWith("issimes")) {
            wordRespell = respellChangingSuffix(wordText, "a", "īssimes");
        }
        if(wordRespell == null && wordText.endsWith("issimes")) {
            wordRespell = respellChangingSuffix(wordText, "e", "īssimes");
        }
        if(wordRespell == null && wordText.endsWith("issimes")) {
            wordRespell = respellChangingSuffix(wordText, "i", "īssimes");
        }
        if(wordRespell == null && wordText.endsWith("issimes")) {
            wordRespell = respellChangingSuffix(wordText, "o", "īssimes");
        }
        if(wordRespell == null && wordText.endsWith("issimes")) {
            wordRespell = respellChangingSuffix(wordText, "u", "īssimes");
        }
        if(wordRespell == null && wordText.endsWith("issimes")) {
            wordRespell = respellChangingSuffix(wordText, "y", "īssimes");
        }
        if(wordRespell == null && wordText.endsWith("issimes")) {
            wordRespell = respellChangingSuffixEvenIfNotFound(wordText, "", "īssimes");
        }

        return wordRespell;
    }

    private boolean endsWithOneOf(String wordText, String... endings) {
        for (String ending : endings) {
            if(wordText.endsWith(ending)) {
                return true;
            }
        }
        return false;
    }

    private String respellChangingSuffix(String word, String storedSuffix, String wordSuffix) {
        Word base = wordRepository.findByWord(word.substring(0, word.length() - wordSuffix.length()) + storedSuffix);
        if (base == null) {
            return null;
        }
        String baseRespell = base.getRespell();
        word = unstress(baseRespell.substring(0, baseRespell.length() - storedSuffix.length())) + wordSuffix;
        return word;
    }

    private String respellChangingSuffixEvenIfNotFound(String word, String storedSuffix, String wordSuffix) {
        String search = word.substring(0, word.length() - wordSuffix.length()) + storedSuffix;
        Word base = wordRepository.findByWord(search);
        String baseRespell = (base == null)? search : base.getRespell();
        word = unstress(baseRespell.substring(0, baseRespell.length() - storedSuffix.length())) + wordSuffix;
        return word;
    }

    private String respellChangingSuffixKeepStress(String wordRespell, String storedSuffix, String wordSuffix) {
        Word base = wordRepository.findByWord(wordRespell.substring(0, wordRespell.length() - wordSuffix.length()) + storedSuffix);
        if (base == null) {
            return null;
        }
        String baseRespell = base.getRespell();
        wordRespell = baseRespell.substring(0, baseRespell.length() - storedSuffix.length()) + wordSuffix;
        return wordRespell;
    }

    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    private static String toPhoneticInvariant(String word, boolean singleWord) {

        word = word.toLowerCase();
        word = superscoreToSingleQuoute(word);
        word = preserveValidAccentsAsAcute(word);
        word = stripAccents(word);
        word = restoreValidAccentsFromAcute(word);
        word = word.replaceAll("-", "");
        word = singleQuoteToSuperscore(word);

        //y before vowels => semivowel y (temporary using ÿ)
        word = word.replaceAll("(y)([aeiouāēīōūǎěǐǒǔ])", "ÿ$2");

        //y after vowels => semivowel y (temporary using ÿ)
        word = word.replaceAll("([aeiouāēīōūǎěǐǒǔ])(y)", "$1ÿ");

        //y as vowel => i
        word = word.replaceAll("y", "i");

        //removing temporary notation ÿ => y
        word = word.replaceAll("ÿ", "y");

        //s between vowels => z
        word = word.replaceAll("([aeiouyāēīōūȳǎěǐǒǔ])[s]([aeiouyāēīōūȳǎěǐǒǔ])","$1z$2");

        //double c => kc
        word = word.replaceAll("cc", "kc");

        //double consonant => single consonant
        word = word.replaceAll("([^aeiouāēīōūǎěǐǒǔ])\\1", "$1");
        //word = word.replaceAll("ss", "s");

        //ch => k
        word = word.replaceAll("ch", "k");

        //ph => f
        word = word.replaceAll("ph", "f");

        //rh => r
        word = word.replaceAll("rh", "r");

        //th => t
        word = word.replaceAll("th", "t");

        //gh => g
        word = word.replaceAll("gh", "g");

        //suffix age => authomatic detection
        /*
        if(!word.endsWith("phage") && !word.endsWith("frage")) {
            word = replaceEndIfMatches(word, "age", "aje");
        } //not possible to distinct variations automatically giose, giator
        */

        //controversial => removing h after most consonants
        word = word.replaceAll("([^aeiouyāēīōūȳǎěǐǒǔ])h", "$1");

        // gu => gw
        word = word.replaceAll("gu([aeioy])", "gw$1");

        // gu => qw
        word = word.replaceAll("qu", "qw");

        // au eu ou => aw ew ow
        word = word.replaceAll("([aeoāēōǎěǒ])[uw]", "$1w");

        if(singleWord) word = removeNonStressMarker(word);

        //vowel before last consonant (ignore last c and s as a consonant) => marking accent as '
        if (!haveStress(word) && !haveNonStressMarker(word)) {
            if(word.endsWith("ic")) {
                word = word.replaceAll("([aeiou][^aeiou\\s]*ic\\b)", "'$1");
            } else if(word.endsWith("s")) {
                word = word.replaceAll("([aeiou][^aeiou\\s]+[aeiou]*s\\b)", "'$1");
            } else {
                word = word.replaceAll("([aeiou][^aeiou\\s]+[aeiou]*\\b)", "'$1");
            }
            word = singleQuoteToSuperscore(word);
        }

        //if stress rule above can not be applied
        if (!haveStress(word) && !haveNonStressMarker(word)) {
            word = word.replaceFirst("([aeiou])", "'$1");
            word = singleQuoteToSuperscore(word);
        }

        word = removeNonStressMarker(word);

        //replace unstressed i in ia ie io iu => ya ye yo yu  => exception starting with "anti" and before stressed sylabe
        /*
        String copy = null;
        while (copy != word) {
            copy = word;
            word = word.replaceAll("([āēīōūaeiou]+.*)i([aeou])", "$1y$2");
        }*/
        word = word.replaceAll("(?<!^ant)i([aeou])", "y$1");
        word = word.replaceAll("(?<!^ant)i([āēōū])", "y$1");


        //replace unstressed tya/tye/tyo/tyu (tia/tie/tio/tiu) => cya, cye, cyo, cyu (exception: after s or x)
        word = word.replaceAll("(?<![sx])t(?=y[aouāōū])", "c");
        //tye (except tyer/tyer (fructiera))
        word = word.replaceAll("(?<![sx])ty([eē])(?!r[aeiouāēīōū])", "cy$1");


        //stress vowel + i/y = diphtongs: ai ei oi ui => ay ey oy uy (temporary using ÿ)   (aī aȳ is not diphtong)
        word = word.replaceAll("([āēīōū])[iy]", "$1y");

        return word;
    }

    private static String removeNonStressMarker(String word) {
        word = word.replaceAll("ǎ" , "a");
        word = word.replaceAll("ě" , "e");
        word = word.replaceAll("ǐ" , "i");
        word = word.replaceAll("ǒ" , "o");
        word = word.replaceAll("ǔ" , "u");
        return word;
    }

    private static boolean haveNonStressMarker(String word) {
        return word.matches(".*[ǎěǐǒǔ].*");
    }

    private static String restoreValidAccentsFromAcute(String word) {
        word = word.replaceAll("´s", "ś");
        word = word.replaceAll("´c", "ć");
        word = word.replaceAll(">a", "ǎ");
        word = word.replaceAll(">e", "ě");
        word = word.replaceAll(">i", "ǐ");
        word = word.replaceAll(">o", "ǒ");
        word = word.replaceAll(">u", "ǔ");
        return word;
    }

    private static String preserveValidAccentsAsAcute(String word) {
        word = word.replaceAll("ś", "´s");
        word = word.replaceAll("ć", "´c");
        word = word.replaceAll("ǎ" , ">a");
        word = word.replaceAll("ě" , ">e");
        word = word.replaceAll("ǐ" , ">i");
        word = word.replaceAll("ǒ" , ">o");
        word = word.replaceAll("ǔ" , ">u");
        return word;
    }


    private static String replaceEndIfMatches(String word, String oldEnd, String newEnd) {
        if (word.endsWith(oldEnd)) {
            word = word.substring(0, word.length() - oldEnd.length()) + newEnd;
        }
        return word;
    }

    private static boolean haveStress(String word) {
        return word.matches(".*[āēīōūȳ].*");
    }

    public static String stripAccents(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return s;
    }

    private static String phoneticInvariantToIpa(String word, boolean singleWord) {
        word = superscoreToSingleQuoute(word);
        word = word.replaceAll("'y", "'i");

        //stress
        //ignore stress in second last vowel if it's the firsT vowel (for carla voice)
        //word = word.replaceAll("^([^aeiou]+)'([aeiou][^aeiou]*[aeiou][^aeiou]*)$", "$1$2");

        word = word.replaceAll("'", "ˈ");


        //j temp
        word = word.replaceAll("j", "J");
        //y temp
        word = word.replaceAll("y", "Y");

        //j
        word = word.replaceAll("J", "ʒ");
        word = word.replaceAll("dʒ", "d͡ʒ");

        //y
        word = word.replaceAll("Y", "j");

        //c
        word = word.replaceAll("ce", "t͡se");
        word = word.replaceAll("cˈe", "t͡sˈe");
        word = word.replaceAll("ci", "t͡si");
        word = word.replaceAll("cˈi", "t͡sˈi");
        word = word.replaceAll("cj", "t͡sj");
        word = word.replaceAll("ts", "t͡s");


        word = word.replaceAll("c", "k");
        word = word.replaceAll("kk", "k"); //ecclesia
        word = word.replaceAll("kt͡s", "kts"); //Sounds better

        word = word.replaceAll("ćh", "ć");
        word = word.replaceAll("ć", "t͡ʃ");

        word = word.replaceAll("śh", "ś");
        word = word.replaceAll("ś", "ʃ");


        //q
        word = word.replaceAll("q", "k");

        //x
        word = word.replaceAll("x", "ks");



        //soft transition vowels
        //word = word.replaceAll("([aeiou])([aeiou])", "$1?$2");
        /*
        if(!singleWord) {
            //only one vowel, no stress
            word = word.replaceAll("^([^aeiou]*)?([aeiou])([^aeiou]*)$", "$1$2$3");
        }
        */


        //move accent to before preceding consonant
        word = word.replaceAll("([^aeioujw]*)([^aeiou])ˈ([aeiou])", "ˈ$1$2$3");

        //when word starts with vowel - add . to break syllabe
        //word = word.replaceAll("^([ˈ]?[aeiou]+)", ".$1");


        //word = word.replaceAll("[^\\p{InIPA_EXTENSIONS}a-zA-Z]", "");

        return word;
    }





    private static String superscoreToSingleQuoute(String word) {
        word = word.replaceAll("ā", "'a");
        word = word.replaceAll("ē", "'e");
        word = word.replaceAll("ī", "'i");
        word = word.replaceAll("ō", "'o");
        word = word.replaceAll("ū", "'u");
        word = word.replaceAll("ȳ", "'y");
        return word;
    }

    private static String singleQuoteToSuperscore(String word) {
        word = word.replaceAll("'a", "ā");
        word = word.replaceAll("'e", "ē");
        word = word.replaceAll("'i", "ī");
        word = word.replaceAll("'o", "ō");
        word = word.replaceAll("'u", "ū");
        word = word.replaceAll("'y", "ȳ");
        return word;
    }

    private static String unstress(String word) {
        word = word.replaceAll("ā", "a");
        word = word.replaceAll("ē", "e");
        word = word.replaceAll("ī", "i");
        word = word.replaceAll("ō", "o");
        word = word.replaceAll("ū", "u");
        word = word.replaceAll("ȳ", "y");
        return word;
    }
}
