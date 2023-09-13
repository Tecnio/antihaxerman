package me.tecnio.ahm;

import cc.ghast.packet.PacketAPI;
import cc.ghast.packet.PacketManager;
import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import me.tecnio.ahm.alert.AlertManager;
import me.tecnio.ahm.check.api.CheckManager;
import me.tecnio.ahm.command.impl.AlertsCommand;
import me.tecnio.ahm.command.impl.ChecksCommand;
import me.tecnio.ahm.command.impl.DefaultCommand;
import me.tecnio.ahm.command.impl.InfoCommand;
import me.tecnio.ahm.config.ConfigManager;
import me.tecnio.ahm.data.PlayerDataManager;
import me.tecnio.ahm.listener.bukkit.RegistrationListener;
import me.tecnio.ahm.listener.network.NetworkListener;
import me.tecnio.ahm.util.registry.ServiceRegistry;
import me.tecnio.ahm.util.registry.ServiceRegistryImpl;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.logging.Logger;

@Getter
public enum AHM {

    INSTANCE;

    public static final boolean TEST_MODE = false;

    private final Logger logger = Logger.getLogger("ahm");

    private final ServiceRegistry serviceRegistry = new ServiceRegistryImpl();

    @Getter
    private JavaPlugin plugin;

    private PlayerDataManager dataManager;
    private CheckManager checkManager;
    private ConfigManager configManager;
    private AlertManager alertManager;

    public void start(final JavaPlugin plugin) {
        this.plugin = plugin;

        registerMetrics();
        registerManagers();
        registerConfiguration();
        registerListeners();
        registerPacketAPI();
        registerCommands();
    }

    public void end() {
        terminateManagers();
    }

    public void registerMetrics() {
        new Metrics(this.plugin, 11350);
    }

    public void registerManagers() {
        install(CheckManager.class, new CheckManager());
        install(ConfigManager.class, new ConfigManager());
        install(PlayerDataManager.class, new PlayerDataManager());
        install(AlertManager.class, new AlertManager());
        install(PaperCommandManager.class, new PaperCommandManager(this.plugin));
    }

    public void registerConfiguration() {
        get(ConfigManager.class).generate();
        get(ConfigManager.class).load();
    }

    public void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new RegistrationListener(), this.plugin);
    }

    public void registerPacketAPI() {
        PacketManager.INSTANCE.init(this.plugin);
        PacketAPI.addListener(new NetworkListener());
    }

    public void registerCommands() {
        get(PaperCommandManager.class).enableUnstableAPI("help");

        Arrays.asList(
                new DefaultCommand(),
                new AlertsCommand(),
                new InfoCommand(),
                new ChecksCommand()
        ).forEach(ahmCommand -> {
            get(PaperCommandManager.class).registerCommand(ahmCommand);
        });
    }

    public void terminateManagers() {
        this.dataManager = null;
        this.checkManager = null;
        this.alertManager = null;
        this.configManager = null;
    }

    public static AHM get() {
        return INSTANCE;
    }

    public static <T> T install(final Class<T> key, final T service) {
        return INSTANCE.serviceRegistry.put(key, service);
    }

    public static <T> T get(final Class<T> tClass) {
        return INSTANCE.serviceRegistry.get(tClass);
    }
}