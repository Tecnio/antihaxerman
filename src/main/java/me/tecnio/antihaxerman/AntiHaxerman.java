package me.tecnio.antihaxerman;

import me.tecnio.antihaxerman.checks.CheckManager;
import me.tecnio.antihaxerman.commands.AntiHaxermanCommand;
import me.tecnio.antihaxerman.listeners.BukkitListener;
import io.github.retrooper.packetevents.PacketEvents;
import me.tecnio.antihaxerman.listeners.NetworkListener;
import me.tecnio.antihaxerman.playerdata.DataManager;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class AntiHaxerman extends JavaPlugin {

    private static AntiHaxerman instance;

    @Override
    public void onLoad() { PacketEvents.load(); }

    @Override
    public void onEnable() {
        instance = this;

        CheckManager.registerChecks();

        saveDefaultConfig();
        Config.updateConfig();

        getCommand("antihaxerman").setExecutor(new AntiHaxermanCommand());

        //PacketEvents
        PacketEvents.getSettings().setIdentifier("antihaxerman_handler");
        PacketEvents.getSettings().setUninjectAsync(true);
        PacketEvents.getSettings().setInjectAsync(true);
        PacketEvents.init(this);

        // Register listeners
        PacketEvents.getAPI().getEventManager().registerListener(new NetworkListener());
        getServer().getPluginManager().registerEvents(new BukkitListener(), this);

        for (Player player : getServer().getOnlinePlayers()){ DataManager.INSTANCE.register(new PlayerData(player.getUniqueId())); }
    }

    @Override
    public void onDisable() {
       PacketEvents.stop();
    }

    public static AntiHaxerman getInstance() {
        return instance;
    }
}
