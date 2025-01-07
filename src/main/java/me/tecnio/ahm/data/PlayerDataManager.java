package me.tecnio.ahm.data;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerDataManager {

    private final Map<UUID, PlayerData> playerDataMap = new ConcurrentHashMap<>();

    public PlayerData get(final UUID uuid) {
        return this.playerDataMap.get(uuid);
    }

    public void add(final Player player) {
        this.playerDataMap.put(player.getUniqueId(), new PlayerData(player));
    }

    public boolean has(final Player player) {
        return this.playerDataMap.containsKey(player.getUniqueId());
    }

    public void remove(final Player player) {
        this.playerDataMap.remove(player.getUniqueId());
    }
}