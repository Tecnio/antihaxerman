package me.tecnio.antihaxerman.processors;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketSendEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.transaction.WrappedPacketInTransaction;
import io.github.retrooper.packetevents.packetwrappers.out.entityvelocity.WrappedPacketOutEntityVelocity;
import io.github.retrooper.packetevents.packetwrappers.out.transaction.WrappedPacketOutTransaction;
import me.tecnio.antihaxerman.playerdata.DataManager;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import org.bukkit.util.Vector;

import java.util.Random;

public final class VelocityProcessor {
    public static void processSend(PacketSendEvent e){
        if (e.getPacketId() == PacketType.Server.ENTITY_VELOCITY) {
            WrappedPacketOutEntityVelocity event = new WrappedPacketOutEntityVelocity(e.getNMSPacket());

            PlayerData data = DataManager.INSTANCE.getUser(e.getPlayer().getUniqueId());
            if (event.getEntity().equals(data.getPlayer())) {
                data.setLastVelocity(new Vector(event.getVelocityX(), event.getVelocityY(), event.getVelocityZ()));

                data.setVerifyingVelocity(true);

                data.setVelocityID((short) new Random().nextInt(32767));
                PacketEvents.getAPI().getPlayerUtils().sendPacket(e.getPlayer(), new WrappedPacketOutTransaction(0, data.getVelocityID(), false));
            }
        }
    }

    public static void processReceive(PacketReceiveEvent e) {
        if (e.getPacketId() == PacketType.Client.TRANSACTION) {
            WrappedPacketInTransaction wrappedPacketInTransaction = new WrappedPacketInTransaction(e.getNMSPacket());

            PlayerData data = DataManager.INSTANCE.getUser(e.getPlayer().getUniqueId());
            if (data.isVerifyingVelocity() && wrappedPacketInTransaction.getActionNumber() == data.getVelocityID()){
                data.setVelTick(data.getTicks());
                data.setVerifyingVelocity(false);
                data.setMaxVelTicks((int) (((data.getLastVelocity().getX() + data.getLastVelocity().getZ()) / 2D + 2D) * 15D));
            }
        }else if (PacketType.Client.Util.isInstanceOfFlying(e.getPacketId())) {
            PlayerData data = DataManager.INSTANCE.getUser(e.getPlayer().getUniqueId());
            if (data.isTakingVelocity()) {
                data.setVelocityTicks(data.getVelocityTicks() + 1);
            }else data.setVelocityTicks(0);
        }
    }
}
