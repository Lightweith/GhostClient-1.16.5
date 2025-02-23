package com.example.mod.settings.obf;

import java.util.List;

public class ObfuscatedModeSetting extends ObfuscatedSetting<String> {
    private final List<String> modes;

    public ObfuscatedModeSetting(String[] parts, String value, List<String> modes) {
        super(parts, value);
        this.modes = modes;
    }

    public List<String> getModes() {
        return modes;
    }
}
