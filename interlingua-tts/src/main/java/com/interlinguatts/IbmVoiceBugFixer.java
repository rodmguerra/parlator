package com.interlinguatts;
import static com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice.*;

public class IbmVoiceBugFixer implements VoiceBugFixer {
    @Override
    public String getForcingIpaFix(Voice voice, String word, int length) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getChangingIpaFix(Voice voice, String ipa) {
        if(IT_FRANCESCA.getName().equals(voice.getName())){
            return ipa.replaceAll("(d͡)?ʒ","dʒ");
        } else if(ES_ENRIQUE.getName().equals(voice.getName())){
            ipa = ipa.replaceAll("(t͡)?ʃ", "tʃ").replaceAll("(d͡)?ʒ", "dz").replaceAll("v","β").replaceAll("r","ɾ");
        } else if(ES_SOFIA.getName().equals(voice.getName()) || ES_LAURA.getName().equals(voice.getName())){
            ipa = ipa.replaceAll("(t͡)?ʃ", "tʃ").replaceAll("(d͡)?ʒ", "ʎ").replaceAll("v","β").replaceAll("r","ɾ");;
        }

        return ipa;
    }
}
