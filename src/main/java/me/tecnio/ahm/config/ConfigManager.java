package me.tecnio.ahm.config;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.SneakyThrows;
import me.tecnio.ahm.AHM;
import me.tecnio.ahm.check.api.CheckManager;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
public final class ConfigManager {

    private final Map<String, Boolean> enabledMap = Maps.newHashMap();
    private final Map<String, Boolean> punishMap = Maps.newHashMap();
    private final Map<String, Integer> maxViolationsMap = Maps.newHashMap();

    private final Map<String, List<String>> commandsMap = Maps.newHashMap();

    @SneakyThrows
    public void generate() {
        final JavaPlugin plugin = AHM.get().getPlugin();
        final FileConfiguration config = plugin.getConfig();
        final List<Class<?>> checks = AHM.get(CheckManager.class).getChecks();

        plugin.saveDefaultConfig();

        /*
         * Simple config generation, no need to make it more complicated and it is quite
         * easy to navigate, this also allows us to have the default values saved on temp
         */
        for (final Class<?> check : checks) {
            final CheckManifest info = check.getAnnotation(CheckManifest.class);

            if (info == null) break;

            final String name = info.name();
            final String type = info.type();
            final int threshold = info.threshold();

            final String header = "checks." + name + "." + type;
            if (config.contains(header)) continue;

            write(header, "");

            final String enabled = header + ".enabled";
            final String banning = header + ".punish";
            final String maxVl = header + ".max-vl";

            write(enabled, true);
            write(banning, true);
            write(maxVl, threshold);

            final String commands = header + ".commands";

            write(commands, Collections.singletonList("ban %player% Cheating"));
        }

        /*
         * Make sure to make the config changes before this point, otherwise
         * they will only be saved on temp and it wont function properly
         */
        plugin.saveConfig();
        plugin.reloadConfig();
    }

    @SneakyThrows
    public void load() {
        final JavaPlugin plugin = AHM.get().getPlugin();
        final FileConfiguration config = plugin.getConfig();

        final List<Class<?>> checks = AHM.get(CheckManager.class).getChecks();

        plugin.saveDefaultConfig();

        for (final Class<?> check : checks) {
            final CheckManifest info = check.getAnnotation(CheckManifest.class);

            final String name = info.name();
            final String type = info.type();

            final String header = "checks." + name + "." + type;
            final String checkId = name + "." + type;

            final String enabled = header + ".enabled";
            final String banning = header + ".punish";
            final String maxVl = header + ".max-vl";

            this.enabledMap.put(checkId, config.getBoolean(enabled));
            this.punishMap.put(checkId, config.getBoolean(banning));
            this.maxViolationsMap.put(checkId, config.getInt(maxVl));

            final String commands = header + ".commands";

            this.commandsMap.put(checkId, config.getStringList(commands));
        }
    }

    private void write(final String header, final Object value) {
        final JavaPlugin plugin = AHM.get().getPlugin();

        if (plugin.getConfig().contains(header)) return;

        plugin.getConfig().set(header, value);
    }
}
