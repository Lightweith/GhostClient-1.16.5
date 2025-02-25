package com.example.mod.modules;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.mod.modules.visuals.*;
import com.example.mod.modules.movement.*;
import com.example.mod.modules.misc.*;
import com.example.mod.modules.combat.*;

public class ModuleManager {
    private final Map<String, Module> modules = new HashMap<>();

    public ModuleManager() {
        registerModule(new HandPosChange());
        registerModule(new InventoryWalk());
        registerModule(new SelfDestruct());
        registerModule(new HitBox());
        registerModule(new FakePlayer());
        registerModule(new ESPGlow());
        registerModule(new ESP2D());
    }

    public void registerModule(Module module) {
        modules.put(module.getName(), module);
    }

    public Collection<Module> getModules() {
        return modules.values();
    }

    public List<Module> getModulesByCategory(Category category) {
        return modules.values().stream()
                .filter(module -> module.getCategory() == category)
                .collect(Collectors.toList());
    }

    public <T extends Module> Optional<T> getModuleByClass(Class<T> clazz) {
        return modules.values().stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .findFirst();
    }
}
