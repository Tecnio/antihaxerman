package me.tecnio.antihaxerman.manager;

import lombok.Getter;
import me.tecnio.antihaxerman.data.PlayerData;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public final class PlayerDataManager {
    @Getter private static final ConcurrentHashMap<UUID, PlayerData> playerData = new ConcurrentHashMap<>();
}
