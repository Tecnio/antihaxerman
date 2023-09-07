package me.tecnio.ahm.data;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerDataManager {

    // skidded from OG AHM... W
    private final Map<UUID, PlayerData> playerDataMap = new ConcurrentHashMap<>();

    public PlayerData getPlayerData(final UUID uuid) {
        return playerDataMap.getOrDefault(uuid, null);
    }

    public void add(final Player player) {
        playerDataMap.put(player.getUniqueId(), new PlayerData(player));
    }

    public boolean has(final Player player) {
        return this.playerDataMap.containsKey(player.getUniqueId());
    }

    public void remove(final Player player) {
        playerDataMap.remove(player.getUniqueId());
    }

    public Collection<PlayerData> getAllData() {
        return playerDataMap.values();
    }
}