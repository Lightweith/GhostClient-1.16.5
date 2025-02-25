package com.example.mod.modules;

import com.example.mod.settings.Setting;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Module {
    private final String name;
    private final Category category;
    private boolean toggled;
    private final List<Setting<?>> settings = new ArrayList<>();

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isToggled() {
        return toggled;
    }

    public void toggle() {
        toggled = !toggled;
        if (toggled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public abstract void onEnable();
    public abstract void onDisable();

    public List<Setting<?>> getSettings() {
        return settings;
    }

    public void addSetting(Setting setting) {
        settings.add(setting);
    }

    public Optional<Setting<?>> getSetting(String name) {
        return settings.stream()
                .filter(s -> s.getName().equalsIgnoreCase(name))
                .findFirst();
    }
}
