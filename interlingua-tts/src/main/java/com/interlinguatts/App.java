package com.interlinguatts;

import java.util.List;

public class App {
    public static void main(String[] args) {
        TestApplicationContext context = TestApplicationContext.getInstance();
        String text = "Le juvene senior reguarda le juvene dama. Illa es un senioretta belle, e ille la reguarda con interesse. Nostre amico es un senior elegante. Sed illa tamen le reguarda sin interesse. Nos debe constatar iste facto tragic jam nunc. Ille dice a illa: “Excusa me, senioretta! Esque vos permitte que io me sede?”. Illa non responde per parolas, sed face un signo con le capite.";

        List<Voice> voices = context.voiceGenerator().getVoices();
        TextToSpeech tts = context.tts();

        for(Voice voice : voices) {
            tts.textToSpeech("C:\\Users\\rodmguerra\\Projetos Pessoais\\intergrammar\\java\\interlingua-tts\\interlingua-tts\\src\\main\\resources\\newaudio\\" + voice.getLanguage() + "_" + voice.getGender() + "_" + voice.getName() + ".mp3", voice, text);
        }
    }


}
