package me.tecnio.antihaxerman.processors;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packet.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.blockdig.WrappedPacketInBlockDig;
import io.github.retrooper.packetevents.packetwrappers.in.entityaction.WrappedPacketInEntityAction;
import io.github.retrooper.packetevents.packetwrappers.in.flying.WrappedPacketInFlying;
import me.tecnio.antihaxerman.playerdata.DataManager;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import me.tecnio.antihaxerman.utils.MathUtils;
import me.tecnio.antihaxerman.utils.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class PacketProcessor {

    @SuppressWarnings("Deprecated")
    public static void process(PacketReceiveEvent event) {
        PlayerData data = DataManager.INSTANCE.getUser(event.getPlayer().getUniqueId());
        if (data != null) {
            if (PacketType.Client.Util.isInstanceOfFlying(event.getPacketId())) {
                WrappedPacketInFlying wrappedPacketInFlying = new WrappedPacketInFlying(event.getNMSPacket());

                data.setTicks(data.getTicks() + 1);

                data.setServerOnGround(PlayerUtils.onGround(data));
                data.setOnGround(wrappedPacketInFlying.isOnGround());
                if (!data.getPlayer().isOnGround()) {
                    data.setAirTicks(data.getAirTicks() + 1);
                    data.setGroundTicks(0);
                } else data.setAirTicks(0);data.setGroundTicks(data.getGroundTicks() + 1);
                if (PlayerUtils.onGround(data)) data.setLastOnGroundLocation(data.getLocation());

                if (wrappedPacketInFlying.isPosition()){
                    data.setLastLocation(data.getLocation() != null ? data.getLocation() : data.getPlayer().getLocation());
                    data.setLocation(new Location(data.getPlayer().getWorld(), wrappedPacketInFlying.getX(), wrappedPacketInFlying.getY(), wrappedPacketInFlying.getZ()));

                    data.setLastDeltaXZ(data.getDeltaXZ());
                    data.setDeltaXZ(data.getLocation().clone().toVector().setY(0).distance(data.getLastLocation().clone().toVector().setY(0)));

                    data.setLastDeltaY(data.getDeltaY());
                    data.setDeltaY(data.getLocation().getY() - data.getLastLocation().getY());
                }

                if (wrappedPacketInFlying.isLook()){
                    data.setLastDeltaPitch(data.getDeltaPitch());
                    data.setLastDeltaYaw(data.getDeltaYaw());
                    data.setDeltaYaw(Math.abs(MathUtils.getAngleDiff(wrappedPacketInFlying.getYaw(), data.getYaw())));
                    data.setDeltaPitch(Math.abs(wrappedPacketInFlying.getPitch() - data.getPitch()));

                    data.setYaw(wrappedPacketInFlying.getYaw());
                    data.setPitch(wrappedPacketInFlying.getPitch());

                    data.setDirection(new Vector(-Math.sin(data.getPlayer().getEyeLocation().getYaw() * 3.1415927F / 180.0F) * (float) 1 * 0.5F, 0, Math.cos(data.getPlayer().getEyeLocation().getYaw() * 3.1415927F / 180.0F) * (float) 1 * 0.5F));
                }

                if (PlayerUtils.isOnIce(data)) data.setIceTicks(data.getTicks());
                if (PlayerUtils.isOnSlime(data)) data.setSlimeTicks(data.getTicks());
                if (PlayerUtils.blockNearHead(data)) data.setUnderBlockTicks(data.getTicks());
                if (PlayerUtils.inLiquid(data)) data.setLiquidTicks(data.getTicks());
                if (data.isSprinting()) data.setSprintingTicks(data.getTicks());

                if (data.getTicks() - data.getLegitTick() == 0)data.setLastLegitLocation(data.getLocation());
            } else if (event.getPacketId() == PacketType.Client.ENTITY_ACTION) {
                WrappedPacketInEntityAction packet = new WrappedPacketInEntityAction(event.getNMSPacket());
                switch (packet.getAction()){
                    case START_SPRINTING: data.setSprinting(true);break;
                    case STOP_SPRINTING: data.setSprinting(false);break;
                    case START_SNEAKING: data.setSneaking(true);break;
                    case STOP_SNEAKING: data.setSneaking(false);break;
                }
            } else if (event.getPacketId() == PacketType.Client.BLOCK_DIG){
                WrappedPacketInBlockDig wrappedPacketInBlockDig = new WrappedPacketInBlockDig(event.getNMSPacket());
                if (wrappedPacketInBlockDig.getDigType() == WrappedPacketInBlockDig.PlayerDigType.START_DESTROY_BLOCK)data.setDigging(true);
                else if (wrappedPacketInBlockDig.getDigType() == WrappedPacketInBlockDig.PlayerDigType.ABORT_DESTROY_BLOCK
                        || wrappedPacketInBlockDig.getDigType() == WrappedPacketInBlockDig.PlayerDigType.STOP_DESTROY_BLOCK){
                    data.setDigging(false);
                }
            }
        }
    }
}
