

package me.tecnio.antihaxerman.manager;

import me.tecnio.antihaxerman.data.PlayerData;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerDataManager {


    public List<Player> suspectedPlayers = new ArrayList<>();

    @Getter
    private static final PlayerDataManager instance = new PlayerDataManager();

    private final Map<UUID, PlayerData> playerDataMap = new ConcurrentHashMap<>();

    public PlayerData getPlayerData(final Player player) {
        return playerDataMap.get(player.getUniqueId());
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
