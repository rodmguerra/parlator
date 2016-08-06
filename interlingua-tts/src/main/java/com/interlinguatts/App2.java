package com.interlinguatts;

public class App2 {
    public static void main(String[] args) {
        TestApplicationContext context = TestApplicationContext.getInstance();
        String text = "Le juvene senior reguarda le juvene dama. Illa es un senioretta belle, e ille la reguarda con interesse. Nostre amico es un senior elegante. Sed illa tamen le reguarda sin interesse. Nos debe constatar iste facto tragic jam nunc. Ille dice a illa: “Excusa me, senioretta! Esque vos permitte que io me sede?”. Illa non responde per parolas, sed face un signo con le capite.";

        Voice voice = context.voiceGenerator().getDefaultVoice();
        TextToSpeech tts = context.tts();

        //for(Voice voice : voices) {
            tts.textToSpeech("C:\\Users\\rodmguerra\\Desktop\\" + voice.getLanguage() + "_" + voice.getGender() + "_" + voice.getName() + ".mp3", voice, text);
        //}
    }


}
