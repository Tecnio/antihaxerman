package me.tecnio.ahm.util.player;

import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerPosition;
import lombok.Data;

import java.util.Set;

@Data
public final class Teleport {
    private final double x, y, z;
    private final Set<GPacketPlayServerPosition.PlayerTeleportFlags> flags;
}
