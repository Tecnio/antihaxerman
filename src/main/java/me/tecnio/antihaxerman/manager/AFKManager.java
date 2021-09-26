package me.tecnio.antihaxerman.manager;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class AFKManager
{
    private final HashMap<Player, Integer> afkList;
    private final HashMap<Player, Integer> afkAmount;
    public static AFKManager INSTANCE;

    public AFKManager() {
        INSTANCE = this;
        this.afkList = new HashMap<>();
        this.afkAmount = new HashMap<>();
    }

    public boolean isAFK(final Player p) {
        if (this.afkList.containsKey(p)) {
            if (p.getLocation().getYaw() == this.afkList.get(p)) {
                return true;
            }
            this.afkList.remove(p);
            if (this.afkAmount.containsKey(p)) {
                this.afkAmount.remove(p);
            }
        }
        this.afkList.put(p, (int)p.getLocation().getYaw());
        return false;
    }

    public void updatePlayer(final Player p) {
        if (this.isAFK(p)) {
            if (this.afkAmount.containsKey(p)) {
                this.afkAmount.put(p, this.afkAmount.get(p) + 1);
            }
            else {
                this.afkAmount.put(p, 1);
            }
        }
    }

    public void removePlayer(final Player p) {
        if (this.afkList.containsKey(p)) {
            this.afkList.remove(p);
        }
        if (this.afkAmount.containsKey(p)) {
            this.afkAmount.remove(p);
        }
    }
}
