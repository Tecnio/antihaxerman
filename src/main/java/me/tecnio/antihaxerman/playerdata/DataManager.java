package me.tecnio.antihaxerman.playerdata;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public enum DataManager {
    INSTANCE;

    private List<PlayerData> playersData = new ArrayList<>();

    public void register(PlayerData user) {
        playersData.add(user);
    }
    public void unregister(PlayerData user) {
        playersData.remove(user);
    }
    public PlayerData getUser(UUID uuid) {
        return playersData.stream().filter(user -> user.getPlayer().getUniqueId() == uuid).findFirst().orElse(null);
    }
    public List<PlayerData> getUsers() {
        return playersData;
    }
}
