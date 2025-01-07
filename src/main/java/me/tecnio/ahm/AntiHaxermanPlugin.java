package me.tecnio.ahm;

import org.bukkit.plugin.java.JavaPlugin;

public final class AntiHaxermanPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        AntiHaxerman.INSTANCE.start(this);
    }

    @Override
    public void onDisable() {
        AntiHaxerman.INSTANCE.end();
    }
}
