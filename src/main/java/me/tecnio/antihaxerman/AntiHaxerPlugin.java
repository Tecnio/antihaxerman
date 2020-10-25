package me.tecnio.antihaxerman;

import me.godead.lilliputian.Dependency;
import me.godead.lilliputian.Lilliputian;
import me.godead.lilliputian.Repository;

import me.tecnio.antihaxerman.AntiHaxerman;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.retrooper.packetevents.PacketEvents;

public final class AntiHaxerPlugin extends JavaPlugin {

    private AntiHaxerman antiHaxerMain = new AntiHaxerman();

    @Override
    public void onEnable() {
        final Lilliputian lilliputian = new Lilliputian(this);  
        lilliputian.getDependencyBuilder()
                .addDependency(new Dependency(
                        Repository.JITPACK,
                        "com.github.retrooper",
                        "packetevents",
                        "1.6.9"))
        .loadDependencies();
        antiHaxerMain.start(this);
    }

    @Override
    public void onDisable() {
       PacketEvents.stop();
    }
}
