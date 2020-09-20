package me.tecnio.antihaxerman.checks.impl.player.badpackets;

import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;

@CheckInfo(name = "BadPackets", type = "E")
public final class BadPacketsE extends Check {
    public BadPacketsE(PlayerData data) {
        super(data);
    }

    @Override
    public void onAttack(WrappedPacketInUseEntity packet) {
        if (packet.getEntity() == data.getPlayer())flag(data, "player hit themselves.");
    }
}
