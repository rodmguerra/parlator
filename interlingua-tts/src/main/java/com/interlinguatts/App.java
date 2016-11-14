package com.interlinguatts;

import com.google.common.base.Strings;

import java.text.Normalizer;
import java.util.List;

public class App {

    public static void main(String[] args) {
        String[][] phrases = {
                {
                        "holla",
                        "que nos va!",
                        "io",
                        "io lo regretta",
                        "excusa me",
                        "si",
                        "no",
                        "per favor",
                        "die",
                        "matino",
                        "meridie",
                        "postmeridie",
                        "vespere",
                        "nocte",
                        "bon die",
                        "bon postmeridie",
                        "bon vespere",
                        "bon nocte",
                        "qual es le novas?",
                        "salute!",
                        "a revider!",
                        "adeo"
                },

            {
                    "como",
                    "tu",
                    "vos",
                    "vader",
                    "io va",
                    "tu va",
                    "vos va",
                    "multo",
                    "multe",
                    "multo ben",
                    "como va tu?",
                    "como va vos?",
                    "io va multo ben",
                    "appellar",
                    "te",
                    "como te appella tu?",
                    "como vos appella vos?",
                    "io me appella",
                    "le genio",
                    "tu es un genio!",
                    "gratias",
                    "de nihil"
            },

            {
                    "felice",
                    "triste",
                    "enoiate",
                    "in cholera",
                    "errate",
                    "fatigate",
                    "malade",
                    "haber",
                    "ration",
                    "haber ration",
                    "fame",
                    "sete",
                    "haber fame",
                    "io es felice",
                    "tu es felice",
                    "es tu felice?",
                    "es io errate?",
                    "tu ha ration",
                    "io ha fame",
                    "ha tu sete?"},

            {
                    "alimento",
                    "pan",
                    "pasta",
                    "ris",
                    "un patata",
                    "un legumine",
                    "un vegetal",
                    "un fructo",
                    "carne",
                    "salata",
                    "un pomo",
                    "un banana",
                    "un orange",
                    "un limon",
                    "un snack",
                    "suppa",
                    "un ovo",
                    "caseo",
                    "gallina",
                    "porco",
                    "bove",
                    "pisce",
                    "aqua",
                    "caffe",
                    "the",
                    "bira",
                    "vino",
                    "lacte",
                    "succo",
                    "sauce",
                    "butyro"},

            {
                    "placer",
                    "il me place",
                    "il non me place",
                    "il te place",
                    "ille",
                    "illa",
                    "illo",
                    "il",
                    "il ha",
                    "isto",
                    "isto es",
                    "deliciose",
                    "disgustose",
                    "meraviliose",
                    "e",
                    "il me place pan",
                    "il non me place pasta",
                    "il me place oranges",
                    "il non me place pomos",
                    "illo es deliciose",
                    "le caffe es deliciose",
                    "il me place the e caffe"
            },

            {
                    "interlingua",
                    "portugese",
                    "italiano",
                    "francese",
                    "espaniol",
                    "anglese",
                    "brasilian",
                    "american",
                    "un brasiliano",
                    "un brasiliana",
                    "un americano",
                    "un americana",
                    "io es portugese",
                    "io es brasilian",
                    "io es anglese",
                    "io es american",
                    "e tu?",
                    "e vos?",
                    "Es tu brasilian?",
                    "io non es brasilian",
                    "es tu american?",
                    "io non es american"},

            {
                    "le numero",
                    "le telephono",
                    "le numero de telephono",
                    "zero",
                    "un",
                    "duo",
                    "tres",
                    "quatro",
                    "cinque",
                    "sex",
                    "septe",
                    "octo",
                    "novem",
                    "dece",
                    "qual",
                    "qual es tu numero de telephono?",
                    "qual es vostre numero de telephono?",
                    "mi numero de telephono es"},

            {
                    "illes",
                    "illas",
                    "illos",
                    "haber",
                    "ha",
                    "vos ha",
                    "pensar",
                    "pensa",
                    "io pensa",
                    "vader",
                    "va",
                    "illas va",
                    "esser",
                    "es",
                    "io es",
                    "illes es",
                    "illas es",
                    "illos es",
                    "bibe",
                    "illes bibe",
                    "mangiar",
                    "nos mangia"},

            {
                    "un restaurante",
                    "un tabula",
                    "le menu",
                    "le factura",
                    "un cultello",
                    "un furchetta",
                    "un coclear",
                    "poter",
                    "io pote",
                    "tu pote",
                    "demandar",
                    "mangiar",
                    "biber",
                    "preste",
                    "pro",
                    "nos",
                    "me",
                    "te",
                    "pro me",
                    "pro te",
                    "il es pro te",
                    "es il pro me?",
                    "un tabula pro duo, per favor",
                    "portar",
                    "apportar",
                    "pro portar",
                    "pote nos haber, per favor?",
                    "pote nos haber le menu, per favor?",
                    "es vos preste?",
                    "es vos preste a ordinar?",
                    "si, per favor",
                    "no, gratias"},

            {
                    "excellente",
                    "belle",
                    "bellissime",
                    "fede",
                    "forte",
                    "fragile",
                    "grasse",
                    "magre",
                    "agradabile",
                    "amusante",
                    "grande",
                    "parve",
                    "alte",
                    "basse",
                    "longe",
                    "curte",
                    "illa es belle",
                    "ille es forte",
                    "illo es amusante",
                    "illo es agradabile",
                    "que",
                    "que meravilia!",
                    "alsi",
                    "que pensa tu?",
                    "io pensa que illo es amusante",
                    "io pensa que illo es troppo grande",
                    "io pensa que si",
                    "io pensa que no",
                    "io alsi lo pensa"
                }
        };

        TestApplicationContext context = TestApplicationContext.getInstance();
        Voice voice = context.voiceGenerator().getGoodVoicesForInterlingua().get(3);
        TextToSpeech tts = context.tts();
        for (int i=0; i<phrases.length; i++) {
            for (int j = 0; j < phrases[i].length; j++) {
                String fileName = "C:\\Users\\rodmguerra\\Desktop\\ia1 - francesca\\" + Strings.padStart(""+(i+1),2,'0') + "\\" + Strings.padStart(""+(j+1),3,'0') + ". " + fileName(phrases[i][j]) + ".flac";
                tts.textToSpeech(fileName, voice, phrases[i][j], new MediaType("audio/flac", "flac"));
            }
        }

    }

    private static String fileName(String text) {
        String filename = text;
        filename = Normalizer.normalize(filename, Normalizer.Form.NFD);
        filename = filename.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        filename = filename.replaceAll("[^a-zA-Z0-9.-]", "_");
        int maxFilenameSize = 50;
        if(filename.length() > maxFilenameSize) {
            filename = filename.substring(0,maxFilenameSize);
        }
        return filename;
    }


    public static void main2(String[] args) {
        TestApplicationContext context = TestApplicationContext.getInstance();
        String text = "Le juvene senior reguarda le juvene dama. Illa es un senioretta belle, e ille la reguarda con interesse. Nostre amico es un senior elegante. Sed illa tamen le reguarda sin interesse. Nos debe constatar iste facto tragic jam nunc. Ille dice a illa: “Excusa me, senioretta! Esque vos permitte que io me sede?”. Illa non responde per parolas, sed face un signo con le capite.";

        Voice voice = context.voiceGenerator().getDefaultVoice();
        TextToSpeech tts = context.tts();

        //for(Voice voice : voices) {
            tts.textToSpeech("C:\\Users\\rodmguerra\\Desktop\\" + voice.getLanguage() + "_" + voice.getGender() + "_" + voice.getName() + ".mp3", voice, text);
        //}
    }


}
