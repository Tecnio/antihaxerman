package me.tecnio.antihaxerman.check.impl.inventory;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.helditemslot.WrappedPacketInHeldItemSlot;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

@CheckInfo(name = "Inventory", type = "A")
public final class InventoryA extends Check {
    public InventoryA(PlayerData data) {
        super(data);
    }

    private int lastSlot = -1;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketId() == PacketType.Client.HELD_ITEM_SLOT) {
            final WrappedPacketInHeldItemSlot wrapper = new WrappedPacketInHeldItemSlot(event.getNMSPacket());
            final int slot = wrapper.getCurrentSelectedSlot();

            final boolean invalid = slot == lastSlot;

            if (invalid) {
                flag();
            }

            lastSlot = slot;
        }
    }
}
