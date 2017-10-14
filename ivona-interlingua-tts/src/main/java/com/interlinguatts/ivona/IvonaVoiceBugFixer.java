package com.interlinguatts.ivona;

import com.google.common.collect.ImmutableMap;
import com.interlinguatts.Voice;
import com.interlinguatts.VoiceBugFixer;

import java.util.Map;

public class IvonaVoiceBugFixer implements VoiceBugFixer {
    public IvonaVoiceBugFixer() {
    }

    public String getForcingIpaFix(Voice voice, String word, int length) {
        if (voice.getName().equals("Carla") && length > 1) {
            Map<String, String> bugWords = ImmutableMap.of(
                    "cata", "kata",
                    "multo", "multo",
                    "multe", "multe",
                    "multes", "multes"
            );
            for (String bugWord : bugWords.keySet()) {
                if (word.equalsIgnoreCase(bugWord)) {
                    return bugWords.get(bugWord);
                }
            }
        }
        return null;
    }

    @Override
    public String getChangingIpaFix(Voice voice, String ipa, int length) {
        if (voice.getName().equals("Carla")) {
            //ipa = ipa.replaceAll("([^ˈ]*)([ˈ]?[^aeiou]*ʒa)", "$1 $2");  //...aja...
            ipa = ipa.replaceAll("(ˈ[^aeiou]*)(d͡)?ʒa", "$1d͡ʒa"); //já => djá
            ipa = ipa.replaceAll("(d͡)?ʒa([^aeiou]*)\\b", "d͡ʒa$2"); //___ja => _____dja, _____jax => ______djax
            ipa = ipa.replaceAll("(oˈ?)ʒ", "$1d͡ʒ");    //oj => odj
            return ipa;
        } else if (voice.getName().equals("Giorgio")) {
            ipa = ipa.replaceAll("(d͡)?ʒe\\B", "d͡ʒe");  //cambia je a dje in le medio del parola
            ipa = ipa.replaceAll("([^aiun])(d͡)?ʒe\\b", "$1d͡ʒe"); //cambia je a dje in le medio del parola excepto in aje, ije, uje, nje
            ipa = ipa.replaceAll("([aiun])ʒe\\b", "$1ʒee"); //adde un e additional a aje, ije, uje, nje
            return ipa;
        }
        return ipa;
    }
}