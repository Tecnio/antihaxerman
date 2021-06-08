/*
 *  Copyright (C) 2020 - 2021 Tecnio
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>
 */

package me.tecnio.antihaxerman;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import lombok.Getter;
import lombok.Setter;
import me.tecnio.antihaxerman.command.CommandManager;
import me.tecnio.antihaxerman.config.Config;
import me.tecnio.antihaxerman.listener.bukkit.BukkitEventManager;
import me.tecnio.antihaxerman.listener.bukkit.RegistrationListener;
import me.tecnio.antihaxerman.listener.packet.NetworkManager;
import me.tecnio.antihaxerman.manager.CheckManager;
import me.tecnio.antihaxerman.manager.ClientBrandListener;
import me.tecnio.antihaxerman.manager.PlayerDataManager;
import me.tecnio.antihaxerman.manager.TickManager;
import me.tecnio.antihaxerman.packet.processor.ReceivingPacketProcessor;
import me.tecnio.antihaxerman.packet.processor.SendingPacketProcessor;
import me.tecnio.antihaxerman.update.UpdateChecker;
import me.tecnio.antihaxerman.util.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.messaging.Messenger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public enum AntiHaxerman {

    INSTANCE;

    private AntiHaxermanPlugin plugin;

    private long startTime;
    private final TickManager tickManager = new TickManager();

    private final ReceivingPacketProcessor receivingPacketProcessor = new ReceivingPacketProcessor();
    private final SendingPacketProcessor sendingPacketProcessor = new SendingPacketProcessor();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final CommandManager commandManager = new CommandManager(this.getPlugin());

    private final String version = "3.2.1";
    private final UpdateChecker updateChecker = new UpdateChecker();

    @Setter private boolean updateAvailable;

    public void load(final AntiHaxermanPlugin plugin) {
        this.plugin = plugin;
        assert plugin != null : "Error while starting AntiHaxerman.";

        setupPacketEvents();
    }

    public void start(final AntiHaxermanPlugin plugin) {
        runPacketEvents();

        this.getPlugin().saveDefaultConfig();
        Config.updateConfig();

        CheckManager.setup();

        Bukkit.getOnlinePlayers().forEach(player -> PlayerDataManager.getInstance().add(player));

        getPlugin().saveDefaultConfig();
        getPlugin().getCommand("antihaxerman").setExecutor(commandManager);

        tickManager.start();

        final Messenger messenger = Bukkit.getMessenger();
        messenger.registerIncomingPluginChannel(plugin, "MC|Brand", new ClientBrandListener());

        startTime = System.currentTimeMillis();

        registerEvents();

        if (Config.UPDATE_CHECKER_ENABLED) updateChecker.checkUpdates();

        if (updateAvailable) {
            Bukkit.getLogger().info("New update available for AntiHaxerman! You have " + version + " latest is " +  updateChecker.getLatestVersion() + ".");
        }

        // Start bStats
        final int pluginId = 11350; // <-- Replace with the id of your plugin!
        new Metrics(plugin, pluginId);
    }

    public void stop(final AntiHaxermanPlugin plugin) {
        this.plugin = plugin;
        assert plugin != null : "Error while shutting down AntiHaxerman.";

        tickManager.stop();

        stopPacketEvents();
        Bukkit.getScheduler().cancelAllTasks();
    }

    private void setupPacketEvents() {
        PacketEvents.create(plugin).getSettings()
                .compatInjector(false)
                .checkForUpdates(false)
                .backupServerVersion(ServerVersion.v_1_7_10);

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
        Bukkit.getServer().getPluginManager().registerEvents(new ClientBrandListener(), plugin);

        PacketEvents.get().getEventManager().registerListener(new NetworkManager());
    }
}
