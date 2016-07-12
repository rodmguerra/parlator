package com.interlinguatts;
import static com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice.*;

public class IbmVoiceBugFixer implements VoiceBugFixer {
    @Override
    public String getForcingIpaFix(Voice voice, String word, int length) {
        return null;
    }

    @Override
    public String getChangingIpaFix(Voice voice, String ipa, int length) {
        if(IT_FRANCESCA.getName().equals(voice.getName())){
            return ipa.replaceAll("(d͡)?ʒ","dʒ");
        } else if(ES_ENRIQUE.getName().equals(voice.getName())){
            ipa = ipa.replaceAll("(t͡)?ʃ", "tʃ").replaceAll("(d͡)?ʒ", "d͡z").replaceAll("v","β").replaceAll("r","ɾ").replace("h","x");
        } else if(ES_LAURA.getName().equals(voice.getName())) {
            ipa = ipa.replaceAll("(t͡)?ʃ", "tʃ").replaceAll("(d͡)?ʒ", "ʝ").replaceAll("v","β").replaceAll("r","ɾ").replace("h","x");
        } else if(ES_SOFIA.getName().equals(voice.getName())){
            ipa = ipa.replaceAll("(t͡)?ʃ", "tʃ").replaceAll("(d͡)?ʒ", "ʎ").replaceAll("v","β").replaceAll("r","ɾ");
            ipa = ipa.replaceAll("^ˈio$", "ˈijo");
            if(length == 1) {
                ipa = ipa.replaceAll("(ˈ[^aeiou]*)aʎe$","$1aaʎe");
            }
        } else if(FR_RENEE.getName().equals(voice.getName())){
            ipa = ipa.replaceAll("r","ɾ").replace("h","r");
        }

        return ipa;
    }
}
