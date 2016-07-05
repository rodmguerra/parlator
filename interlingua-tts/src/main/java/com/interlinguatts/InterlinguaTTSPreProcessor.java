package com.interlinguatts;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InterlinguaTTSPreProcessor {
    final InterlinguaNumberWriter numberWriter;

    public InterlinguaTTSPreProcessor(InterlinguaNumberWriter numberWriter) {
        this.numberWriter = numberWriter;
    }

    String replaceSeparatedOrdinal(String input) {
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

    String replaceSeparatedOrdinalAdverb(String input) {
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

    String replaceNonSeparatedOrdinal(String input) {
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

    String replaceNonSeparatedOrdinalAdverb(String input) {
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

    String replaceSeparatedCardinal(String input) {  //max 4 digit
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

    String replaceLimitedNonSeparatedCardinal(String input) {  //max 4 digit
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

    static String replaceSymbols(String text) {
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

    String replaceNumbers(String text) {
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

    String replaceCardinals(String test) {
        test = replaceSeparatedCardinal(test);
        test = replaceLimitedNonSeparatedCardinal(test);
        return test;
    }

    String replaceOrdinals(String test) {
        test = replaceSeparatedOrdinal(test);

        test = replaceSeparatedOrdinalAdverb(test);

        test = replaceNonSeparatedOrdinal(test);

        test = replaceNonSeparatedOrdinalAdverb(test);
        return test;
    }

    static String separateFollowingLettersFromNumbers(String test) {
        return test.replaceAll("([0-9])(\\p{L})", "$1 $2");
    }

    static String separatePrecedingLettersFromNumbers(String test) {
        test = test.replaceAll("(\\p{L})([0-9])", "$1 $2");
        return test;
    }

    String replaceOtherNumbers(String input) {

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

    public String preProcessText(String text) {

        //text = text.replaceAll("\\b\\.\\b", ", puncto, "); //good for emails, bad for U.S.A.
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
        return text;
    }

    String replaceAbbreviations(String text) {
        text = text.replaceAll("sr\\.", "senior");
        text = text.replaceAll("sra\\.", "seniora");
        text = text.replaceAll("Sr\\.", "senior");
        text = text.replaceAll("Sra\\.", "seniora");
        text = text.replaceAll("etc\\.", "etcetera");
        text = text.replaceAll("Etc\\.", "Etcetera");
        return text;
    }

    public static void main(String[] args) {
        InterlinguaTTSPreProcessor preProcessor = new InterlinguaTTSPreProcessor(new InterlinguaNumberWriter());
        //String text = "2.500me, le 10000me ha apportate depost de9.999nesj. Ille ha2.456,777 annos. Su telephone es45646546545646565465";

        //String text = "Entre parênteses (=), palavras=grudadas, grudada= palavra, palavra =grudada. Solto = Solto";

        //text = replaceSymbols(text);
        //System.out.println(text);

        String test = "a - 5 + 4 -3 +222-";
        System.out.println(test);
        test = preProcessor.replaceNumbers(test);



        System.out.println(test);
    }
}
