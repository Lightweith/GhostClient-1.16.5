package com.example.mod.settings;

public class FloatSetting extends Setting<Float> {
    private float min, max;

    public FloatSetting(String name, Float value, float min, float max) {
        super(name, value);
        this.min = min;
        this.max = max;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }
}
