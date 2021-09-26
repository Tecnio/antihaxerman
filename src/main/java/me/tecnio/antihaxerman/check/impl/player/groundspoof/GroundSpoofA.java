

package me.tecnio.antihaxerman.check.impl.player.groundspoof;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;

@CheckInfo(name = "GroundSpoof", type = "A", description = "Uses math to calculate if the player is onGround or not.")
public final class GroundSpoofA extends Check {

    public GroundSpoofA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            final WrappedPacketInFlying wrapper = new WrappedPacketInFlying(packet.getRawPacket());

            final boolean clientGround = wrapper.isOnGround();
            final boolean serverGround = wrapper.getY() % 0.015625 == 0.0;

            final boolean exempt = isExempt(ExemptType.NEARSTAIRS, ExemptType.BOAT, ExemptType.LIQUID, ExemptType.CLIMBABLE, ExemptType.VEHICLE, ExemptType.TELEPORT_DELAY, ExemptType.CHUNK, ExemptType.SLIME, ExemptType.FLYING, ExemptType.PISTON);
            final boolean invalid = clientGround != serverGround;

            if (invalid && !exempt) {
                if (increaseBuffer() > 4) {
                    fail();
                }
            } else {
                decreaseBufferBy(0.25);
            }
        }
    }
}
