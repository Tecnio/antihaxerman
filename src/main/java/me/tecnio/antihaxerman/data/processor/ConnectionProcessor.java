

package me.tecnio.antihaxerman.data.processor;

import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.manager.AlertManager;
import me.tecnio.antihaxerman.util.type.EvictingMap;
import io.github.retrooper.packetevents.packetwrappers.play.in.keepalive.WrappedPacketInKeepAlive;
import io.github.retrooper.packetevents.packetwrappers.play.in.transaction.WrappedPacketInTransaction;
import io.github.retrooper.packetevents.packetwrappers.play.out.keepalive.WrappedPacketOutKeepAlive;
import io.github.retrooper.packetevents.packetwrappers.play.out.transaction.WrappedPacketOutTransaction;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;

@Getter
public final class ConnectionProcessor {

    private final PlayerData data;

    private final EvictingMap<Short, Long> transactionUpdates = new EvictingMap<>(20);
    private final EvictingMap<Long, Long> keepAliveUpdates = new EvictingMap<>(20);

    private short transactionId;
    private long keepAliveId;

    private long keepAlivePing;
    private long transactionPing;

    private long lastTransactionSent;
    private long lastTransactionReceived;
    private long lastTransactionReceivedCheck;

    private long lastKeepAliveSent;
    private long lastKeepAliveReceived;

    public ConnectionProcessor(final PlayerData data) {
        this.data = data;
    }

    public void handleIncomingTransaction(final WrappedPacketInTransaction wrapper) {
        final long now = System.currentTimeMillis();

        transactionUpdates.computeIfPresent(wrapper.getActionNumber(), (id, time) -> {
            transactionPing = now - time;
            lastTransactionReceived = now;
            return time;
        });
    }

    public void handleIncomingKeepAlive(final WrappedPacketInKeepAlive wrapper) {
        final long now = System.currentTimeMillis();
        keepAliveUpdates.computeIfPresent(wrapper.getId(), (id, time) -> {
            keepAlivePing = now - time;
            lastKeepAliveReceived = now;

            return time;
        });
    }

    public void handleOutgoingTransaction(final WrappedPacketOutTransaction wrapper) {
        final long now = System.currentTimeMillis();
        final short actionNumber = wrapper.getActionNumber();

        lastTransactionSent = now;
        lastTransactionReceivedCheck = now;
        transactionId = actionNumber;
        transactionUpdates.put(actionNumber, System.currentTimeMillis());
    }

    public void handleOutgoingKeepAlive(final WrappedPacketOutKeepAlive wrapper) {
        final long now = System.currentTimeMillis();
        final long id = wrapper.getId();


        lastKeepAliveSent = now;

        keepAliveId = id;
        keepAliveUpdates.put(id, System.currentTimeMillis());

        final long calc3 = now - lastKeepAliveReceived;
        final long calc4 = now - data.getJoinTime();
        final long calc = now - lastTransactionReceived;
        final long calc2 = now - data.getJoinTime();
        if(lastTransactionReceived == 0 && calc4 < 1000L) {
            return;
        }
        if(lastTransactionReceived == 0 && calc2 < 1000L) {
            return;
        }
//        if(calc3 > 200L) {
//            AlertManager.sendMessage("Warning! " + data.getPlayer().getName() + " Canceled Transaction Packets for over " + calc3 + "MS!");
//        }
//        if(calc3 > 2000L) {
//            Bukkit.getScheduler().runTask(Antihaxerman.INSTANCE.getPlugin(), new Runnable() {
//                public void run() {
//                    data.getPlayer().kickPlayer("KeepAlive Packet Disabler isnt halal mode. It doesnt bypass ahm, but ahm is better with this check.");
//                }
//            });
//        }

        if(calc > 2000L) {
            AlertManager.sendMessage("Warning! " + data.getPlayer().getName() + " Canceled Transaction Packets for over " + calc + "MS!");
        }
    }

    public Optional<Long> getTransactionTime(final short actionNumber) {
        final Map<Short, Long> entries = transactionUpdates;

        if (entries.containsKey(actionNumber)) return Optional.of(entries.get(actionNumber));

        return Optional.empty();
    }

    public Optional<Long> getKeepAliveTime(final long identification) {
        final Map<Long, Long> entries = keepAliveUpdates;

        if (entries.containsKey(identification)) return Optional.of(entries.get(identification));

        return Optional.empty();
    }
}
