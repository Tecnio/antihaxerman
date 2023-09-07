package me.tecnio.ahm;

import org.bukkit.plugin.java.JavaPlugin;

public final class AHMPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        AHM.INSTANCE.start(this);
    }

    @Override
    public void onDisable() {
        AHM.INSTANCE.end();
    }
}
