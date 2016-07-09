package com.interlinguatts;

public class DummyVoiceBugFixer implements VoiceBugFixer {
    @Override
    public String getForcingIpaFix(Voice voice, String word, int length) {
        return null;
    }

    @Override
    public String getChangingIpaFix(Voice voice, String ipa) {
        return ipa;
    }
}
