
package me.tecnio.antihaxerman;

import org.bukkit.plugin.java.JavaPlugin;

public final class AntiHaxermanPlugin extends JavaPlugin {

    @Override
    public void onLoad() {
        AntiHaxerman.INSTANCE.load(this);
    }

    @Override
    public void onEnable() {
        AntiHaxerman.INSTANCE.start(this);
    }

    @Override
    public void onDisable() {
        AntiHaxerman.INSTANCE.stop(this);
    }

}
