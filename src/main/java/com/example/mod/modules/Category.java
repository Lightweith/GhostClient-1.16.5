package com.example.mod.modules;

import com.example.mod.modules.obf.ObfuscatedFragment;

public enum Category {
    KCH(new ObfuscatedFragment[]{ // combat
            new ObfuscatedFragment("Com"),
            new ObfuscatedFragment("b"),
            new ObfuscatedFragment("at")
    }),
    BAY(new ObfuscatedFragment[]{ // visuals
            new ObfuscatedFragment("Vis"),
            new ObfuscatedFragment("u"),
            new ObfuscatedFragment("als")
    }),
    BEG(new ObfuscatedFragment[]{ // movement
            new ObfuscatedFragment("Mov"),
            new ObfuscatedFragment("e"),
            new ObfuscatedFragment("ment")
    }),
    OST(new ObfuscatedFragment[]{ // misc
            new ObfuscatedFragment("Mi"),
            new ObfuscatedFragment("s"),
            new ObfuscatedFragment("c")
    });

    private final ObfuscatedFragment[] fragments;

    Category(ObfuscatedFragment[] fragments) {
        this.fragments = fragments;
    }

    public String getName() {
        StringBuilder sb = new StringBuilder();
        for (ObfuscatedFragment fragment : fragments) {
            sb.append(fragment.getOriginal());
        }
        return sb.toString();
    }

    public String getObfuscatedName() {
        StringBuilder sb = new StringBuilder();
        for (ObfuscatedFragment fragment : fragments) {
            sb.append(fragment.getObfuscated());
        }
        return sb.toString();
    }
}
