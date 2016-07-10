package com.interlinguatts;

public interface VoiceBugFixer {
    public String getForcingIpaFix(Voice voice, String word, int length);
    public String getChangingIpaFix(Voice voice, String ipa, int length);
}
