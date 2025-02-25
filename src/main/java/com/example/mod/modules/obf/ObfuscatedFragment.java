package com.example.mod.modules.obf;

public class ObfuscatedFragment {
    private final String obfuscated;
    private final int shift;

    public ObfuscatedFragment(String original) {
        int len = original.length();
        if (len > 1) {
            shift = (int) (Math.random() * len);
        } else {
            shift = 0;
        }
        obfuscated = original.substring(shift) + original.substring(0, shift);
    }

    public String getObfuscated() {
        return obfuscated;
    }

    public String getOriginal() {
        int len = obfuscated.length();
        int invShift = len - shift;
        return obfuscated.substring(invShift) + obfuscated.substring(0, invShift);
    }
}
