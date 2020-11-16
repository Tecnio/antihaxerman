package me.tecnio.antihaxerman;

import io.github.retrooper.packetevents.PacketEvents;
import lombok.Getter;
import me.tecnio.antihaxerman.check.CheckManager;
import me.tecnio.antihaxerman.commands.api.CommandManager;
import me.tecnio.antihaxerman.commands.impl.AlertsCommand;
import me.tecnio.antihaxerman.commands.impl.LogsCommand;
import me.tecnio.antihaxerman.commands.impl.VerboseCommand;
import me.tecnio.antihaxerman.listeners.NetworkListener;
import me.tecnio.antihaxerman.listeners.RegistrationListener;
import me.tecnio.antihaxerman.manager.TickManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AntiHaxerman extends JavaPlugin {

    @Getter private static AntiHaxerman instance;

    @Getter private static final ExecutorService checkExecutor = Executors.newSingleThreadExecutor();
    @Getter private final TickManager tickProcessor = new TickManager();

    @Override
    public void onLoad() {
        PacketEvents.load();
    }

    @Override
    public void onEnable() {
        instance = this;

        setupConfig();

        CheckManager.registerChecks();

        setupProcessors();

        registerListeners();
        setupCommands();

        startPacketEvents();
    }

    private void setupConfig() {
        saveDefaultConfig();
        Config.updateSettings();
    }

    private void setupProcessors() {
        tickProcessor.start();
    }

    private void setupCommands() {
        CommandManager.setup(this);

        CommandManager.register(new AlertsCommand());
        CommandManager.register(new LogsCommand());
        CommandManager.register(new VerboseCommand());
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new RegistrationListener(), this);

        PacketEvents.getAPI().getEventManager().registerListeners(new NetworkListener());
    }

    private void startPacketEvents() {
        PacketEvents.getSettings().injectAsync(true);
        PacketEvents.getSettings().ejectAsync(true);

        PacketEvents.getSettings().checkForUpdates(false);

        PacketEvents.init(this);
    }
}
