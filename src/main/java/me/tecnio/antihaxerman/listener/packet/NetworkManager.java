/*
 *  Copyright (C) 2020 - 2021 Tecnio
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>
 */

package me.tecnio.antihaxerman.listener.packet;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.github.retrooper.packetevents.event.PacketListenerAbstract;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent;
import io.github.retrooper.packetevents.event.impl.PostPlayerInjectEvent;
import io.github.retrooper.packetevents.event.priority.PacketEventPriority;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packettype.PacketType.Play.Server;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import io.github.retrooper.packetevents.utils.immutableset.ImmutableSetCustom;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.manager.AlertManager;
import me.tecnio.antihaxerman.manager.PlayerDataManager;
import me.tecnio.antihaxerman.packet.Packet;
import org.bukkit.Bukkit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class NetworkManager extends PacketListenerAbstract {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor(
            new ThreadFactoryBuilder().setNameFormat("AntiHaxerman Thread").build());

    public NetworkManager() {
        super(PacketEventPriority.MONITOR);

        // Filter all of the packets because retrooper good.
        serverSidedPlayAllowance = new ImmutableSetCustom<>();

        // Whitelist packets that we want to listen.
        addServerSidedPlayFilter(
                Server.ENTITY_VELOCITY,
                Server.TRANSACTION,
                Server.KEEP_ALIVE,
                Server.POSITION,
                Server.HELD_ITEM_SLOT
        );
    }

    @Override
    public void onPacketPlayReceive(final PacketPlayReceiveEvent event) {
        final PlayerData data = PlayerDataManager.getInstance().getPlayerData(event.getPlayer());

        if (data != null) {
            if (PacketType.Play.Client.Util.isInstanceOfFlying(event.getPacketId())) {
                final WrappedPacketInFlying wrapper = new WrappedPacketInFlying(event.getNMSPacket());

                if (Math.abs(wrapper.getX()) > 1.0E+7
                        || Math.abs(wrapper.getY()) > 1.0E+7
                        || Math.abs(wrapper.getZ()) > 1.0E+7
                        || Math.abs(wrapper.getPitch()) > 1.0E+7
                        || Math.abs(wrapper.getYaw()) > 1.0E+7) {
                    Bukkit.getScheduler().runTask(AntiHaxerman.INSTANCE.getPlugin(), () -> event.getPlayer().kickPlayer("You are gay."));
                    return;
                }
            }

            executorService.execute(() -> AntiHaxerman.INSTANCE.getReceivingPacketProcessor().handle(
                    data, new Packet(Packet.Direction.RECEIVE, event.getNMSPacket(), event.getPacketId(), event.getTimestamp())));
        }
    }

    @Override
    public void onPacketPlaySend(final PacketPlaySendEvent event) {
        final PlayerData data = PlayerDataManager.getInstance().getPlayerData(event.getPlayer());

        if (data != null) {
            executorService.execute(() -> AntiHaxerman.INSTANCE.getSendingPacketProcessor().handle(
                    data, new Packet(Packet.Direction.SEND, event.getNMSPacket(), event.getPacketId(), event.getTimestamp())));
        }
    }

    @Override
    public void onPostPlayerInject(final PostPlayerInjectEvent event) {
        final ClientVersion version = event.getClientVersion();

        final boolean unsupported = version.isHigherThan(ClientVersion.v_1_8) || version.isLowerThan(ClientVersion.v_1_7_10);

        if (unsupported) {
            final String message = String.format("Player '%s' joined with a client version that is not supported, this might cause false positives. Please take the appropriate action. (Version: %s)", event.getPlayer().getName(), version.name());

            Bukkit.getLogger().warning(message);
            AlertManager.sendMessage(message);
        }
    }
}
