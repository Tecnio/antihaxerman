

package me.tecnio.antihaxerman.check.impl.player.inventory;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.helditemslot.WrappedPacketInHeldItemSlot;

@CheckInfo(name = "Inventory", type = "A", description = "Checks for slot change to same slot.")
public final class InventoryA extends Check {

    private int lastSlot = -1;
    private boolean server;

    public InventoryA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isIncomingHeldItemSlot() && !isExempt(ExemptType.JOINED)) {
            final WrappedPacketInHeldItemSlot wrapper = new WrappedPacketInHeldItemSlot(packet.getRawPacket());

            final int slot = wrapper.getCurrentSelectedSlot();

            final boolean invalid = slot == lastSlot;
            final boolean exempt = server;

            if (invalid && !exempt) {
                fail();
            }

            lastSlot = slot;
            server = false;
        } else if (packet.isOutgoingHeldItemSlot()) {
            server = true;
        }
    }
}
