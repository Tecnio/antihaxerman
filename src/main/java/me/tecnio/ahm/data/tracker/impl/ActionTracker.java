package me.tecnio.ahm.data.tracker.impl;

import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientBlockDig;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientBlockPlace;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientEntityAction;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientUseEntity;
import lombok.Getter;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.data.tracker.Tracker;
import me.tecnio.ahm.util.player.TickTimer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Entity;

@Getter
public final class ActionTracker extends Tracker {

    private Entity target;
    private boolean sprinting, lastSprinting, sneaking, lastSneaking;

    private final TickTimer attackTimer = new TickTimer(data);
    private int hits;

    private boolean blocking, placing;

    public ActionTracker(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientEntityAction) {
            final GPacketPlayClientEntityAction wrapper = ((GPacketPlayClientEntityAction) packet);

            switch (wrapper.getAction()) {
                case START_SPRINTING: {
                    this.sprinting = true;
                    break;
                }

                case STOP_SPRINTING: {
                    this.sprinting = false;
                    break;
                }

                case START_SNEAKING: {
                    this.sneaking = true;
                    break;
                }

                case STOP_SNEAKING: {
                    this.sneaking = false;
                    break;
                }
            }
        } else if (packet instanceof GPacketPlayClientUseEntity) {
            final GPacketPlayClientUseEntity wrapper = ((GPacketPlayClientUseEntity) packet);

            if (wrapper.getType() == PlayerEnums.UseType.ATTACK) {
                this.attackTimer.reset();
                ++this.hits;

                final net.minecraft.server.v1_8_R3.Entity target = ((CraftWorld) data.getPlayer().getWorld())
                        .getHandle().a(wrapper.getEntityId());

                if (target != null) {
                    this.target = target.getBukkitEntity();
                } else {
                    this.target = null;
                }
            }
        } else if (packet instanceof GPacketPlayClientBlockPlace) {
            if (data.getPlayer().getItemInHand().toString().contains("SWORD")) this.blocking = true;


            this.placing = true;
        } else if (packet instanceof GPacketPlayClientBlockDig) {
            final GPacketPlayClientBlockDig wrapper = ((GPacketPlayClientBlockDig) packet);

            switch (wrapper.getType()) {
                case RELEASE_USE_ITEM: {
                    this.blocking = false;
                    break;
                }
            }
        } else if (packet instanceof PacketPlayClientFlying) {
            if (!data.getPlayer().getItemInHand().toString().contains("SWORD")) this.blocking = false;
        }
    }

    @Override
    public void handlePost(final GPacket packet) {
        if (packet instanceof PacketPlayClientFlying) {
            this.lastSneaking = this.sneaking;
            this.lastSprinting = this.sprinting;

            this.placing = false;

            this.hits = 0;
        }
    }
}