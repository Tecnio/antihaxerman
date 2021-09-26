

package me.tecnio.antihaxerman.check.impl.player.badpackets;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "BadPackets", type = "E", description = "Checks for blink by checking if client doesn't send flying while being still connected.", experimental = true)
public final class BadPacketsE extends Check {
    public BadPacketsE(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
    }
}
