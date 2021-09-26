

package me.tecnio.antihaxerman.check.impl.combat.autoclicker;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import io.github.retrooper.packetevents.utils.player.ClientVersion;

@CheckInfo(name = "AutoClicker", type = "F", description = "Checks for frequency of the clicks.")
public final class AutoClickerF extends Check {

    private int movements = 0, lastMovements = 0, total = 0, invalid = 0;

    public AutoClickerF(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());

            if (wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                final boolean proper = data.getClickProcessor().getCps() > 7.2 && movements < 4 && lastMovements < 4;

                if (proper) {
                    final boolean flag = movements == lastMovements;

                    if (flag) {
                        ++invalid;
                    }

                    if (++total == 40) {

                        if (invalid >= 40) {
                            if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_17)) {
                                if(increaseBuffer() > 10) {
                                    fail();
                                }
                                else {
                                    decreaseBufferBy(0.02);
                                }
                            } else {
                                fail();
                            }
                        }

                        total = 0;
                    }
                }

                lastMovements = movements;
                movements = 0;
            }
        } else if (packet.isFlying()) {
            movements++;
        }
    }
}
