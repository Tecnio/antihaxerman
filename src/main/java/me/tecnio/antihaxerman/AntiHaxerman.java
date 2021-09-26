package me.tecnio.antihaxerman;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import me.tecnio.antihaxerman.check.impl.movement.speed.SpeedF;
import me.tecnio.antihaxerman.check.impl.player.fastbow.FastBowA;
import me.tecnio.antihaxerman.command.CommandManager;
import me.tecnio.antihaxerman.command.VerusCommand;
import me.tecnio.antihaxerman.config.Config;
import me.tecnio.antihaxerman.gui.GuiManager;
import me.tecnio.antihaxerman.listener.bukkit.BukkitEventManager;
import me.tecnio.antihaxerman.listener.bukkit.RegistrationListener;
import me.tecnio.antihaxerman.listener.packet.NetworkManager;
import me.tecnio.antihaxerman.packet.processor.ReceivingPacketProcessor;
import me.tecnio.antihaxerman.packet.processor.SendingPacketProcessor;
import me.tecnio.antihaxerman.update.UpdateChecker;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import lombok.Getter;
import lombok.Setter;
import me.tecnio.antihaxerman.manager.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginAwareness;
import org.bukkit.plugin.messaging.Messenger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public enum AntiHaxerman {

    INSTANCE;

    private AntiHaxermanPlugin plugin;

    @Setter
    private YamlConfiguration yaml;

    private long startTime;
    private final TickManager tickManager = new TickManager();
    private final ReceivingPacketProcessor receivingPacketProcessor = new ReceivingPacketProcessor();
    private final SendingPacketProcessor sendingPacketProcessor = new SendingPacketProcessor();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final CommandManager commandManager = new CommandManager(this.getPlugin());

    private final String version = "B11";
    private final UpdateChecker updateChecker = new UpdateChecker();
    private GuiManager guiManager;

    public void load(final AntiHaxermanPlugin plugin) {
        this.plugin = plugin;
        assert plugin != null : "Error while starting Antihaxerman.";

        setupPacketEvents();
    }

    public void start(final AntiHaxermanPlugin plugin) {
        runPacketEvents();

        getPlugin().saveDefaultConfig();
        File checks = new File(getPlugin().getDataFolder(), "checks.yml");
        if(!checks.exists()) {
            getPlugin().saveResource("checks.yml", false);
            checks = new File(getPlugin().getDataFolder(), "checks.yml");
        }
        yaml = YamlConfiguration.loadConfiguration(checks);
        Config.updateConfig();

        CheckManager.setup();
        Bukkit.getOnlinePlayers().forEach(player -> PlayerDataManager.getInstance().add(player));

        guiManager = new GuiManager();
        getPlugin().getCommand("ahm").setExecutor(commandManager);
        getPlugin().getCommand("verus").setExecutor(new VerusCommand());

        tickManager.start();

        new AFKManager();

        final Messenger messenger = Bukkit.getMessenger();
        messenger.registerIncomingPluginChannel(plugin, "MC|Brand", new ClientBrandListener());

        startTime = System.currentTimeMillis();

        registerEvents();

    }

    public void stop(final AntiHaxermanPlugin plugin) {
        this.plugin = plugin;
        assert plugin != null : "Error while shutting down AHM.";

        tickManager.stop();

        stopPacketEvents();
        Bukkit.getScheduler().cancelAllTasks();
    }

    private void setupPacketEvents() {
        PacketEvents.create(plugin).getSettings()
                .backupServerVersion(ServerVersion.v_1_8_8);

        PacketEvents.get().load();
    }

    private void runPacketEvents() {
        PacketEvents.get().init(plugin);
    }

    private void stopPacketEvents() {
        PacketEvents.get().terminate();
    }

    private void registerEvents() {
        Bukkit.getServer().getPluginManager().registerEvents(new RegistrationListener(), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new BukkitEventManager(), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new SpeedF(), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new ClientBrandListener(), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new FastBowA(), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new GuiManager(), plugin);
        PacketEvents.get().getEventManager().registerListener(new NetworkManager());
    }

    public InputStream getResource(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }

        try {
            URL url = AntiHaxerman.INSTANCE.getPlugin().getClass().getClassLoader().getResource(filename);

            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }

    public void reloadConfig() {
        yaml = YamlConfiguration.loadConfiguration(new File(AntiHaxerman.INSTANCE.getPlugin().getDataFolder(), "checks.yml"));

        final InputStream defConfigStream = getResource("config.yml");
        if (defConfigStream == null) {
            return;
        }

        final YamlConfiguration defConfig;
        if (getPlugin().getDescription().getAwareness().contains(PluginAwareness.Flags.UTF8) || FileConfiguration.UTF8_OVERRIDE) {
            defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8));
        } else {
            final byte[] contents;
            defConfig = new YamlConfiguration();
            try {
                contents = ByteStreams.toByteArray(defConfigStream);
            } catch (final IOException e) {
                return;
            }

            final String text = new String(contents, Charset.defaultCharset());
            if (!text.equals(new String(contents, Charsets.UTF_8))) {
            }

            try {
                defConfig.loadFromString(text);
            } catch (final InvalidConfigurationException e) {
            }
        }

        yaml.setDefaults(defConfig);
    }

}
