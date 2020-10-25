package me.tecnio.antihaxerman;

import me.tecnio.antihaxerman.checks.CheckManager;
import me.tecnio.antihaxerman.commands.AntiHaxermanCommand;
import me.tecnio.antihaxerman.AntiHaxerPlugin;
import me.tecnio.antihaxerman.listeners.BukkitListener;
import io.github.retrooper.packetevents.PacketEvents;
import me.tecnio.antihaxerman.playerdata.DataManager;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

public final class AntiHaxerman {

    private static AntiHaxerPlugin instance;

    public void start(AntiHaxerPlugin instance) {
        this.instance = instance;

        PacketEvents.load();

        CheckManager.registerChecks();

        instance.saveDefaultConfig();
        Config.updateConfig();

        instance.getCommand("antihaxerman").setExecutor(new AntiHaxermanCommand());

        //PacketEvents
        PacketEvents.getSettings().setIdentifier("antihaxerman_handler");
        PacketEvents.getSettings().setUninjectAsync(true);
        PacketEvents.getSettings().setInjectAsync(true);
        PacketEvents.init(instance);

        // Register listeners
        instance.getServer().getPluginManager().registerEvents(new BukkitListener(), instance);

        for (Player player : instance.getServer().getOnlinePlayers()){ DataManager.INSTANCE.register(new PlayerData(player.getUniqueId())); }
    }

    public static AntiHaxerPlugin getInstance() {
        return instance;
    }
}
