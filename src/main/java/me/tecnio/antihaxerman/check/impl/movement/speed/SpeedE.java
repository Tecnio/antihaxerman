

package me.tecnio.antihaxerman.check.impl.movement.speed;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.data.processor.PositionProcessor;
import me.tecnio.antihaxerman.data.processor.VelocityProcessor;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.PlayerUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Speed", type = "E", description = "Checks if player is going faster than possible on air.")
public final class SpeedE extends Check {
    public SpeedE(final PlayerData data) {
        super(data);
    }

    private int groundTicks;
    private int airTicks;
    private int lastJumpTick;
    private double lastLandY;

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition() && !this.isExempt(ExemptType.TELEPORT, ExemptType.FLYING)) {
            if(isExempt(ExemptType.NEARSTAIRS, ExemptType.NEARSLABS)) {
                return;
            }
            if(data.getPlayer().getWalkSpeed() > 0.2) {
                return;
            }
            final WrappedPacketInFlying flying = new WrappedPacketInFlying(packet.getRawPacket());
            double speed = 0.0;
            this.groundTicks = (flying.isOnGround() ? (this.groundTicks + 1) : 0);
            this.airTicks = (flying.isOnGround() ? 0 : (this.airTicks + 1));
            final PositionProcessor position = this.data.getPositionProcessor();
            final VelocityProcessor velocity = this.data.getVelocityProcessor();
            final double deltaXZ = Math.abs(position.getDeltaXZ());
            final double deltaY = position.getDeltaY();
            final double posY = this.data.getPositionProcessor().getY();
            if (deltaXZ == 0.0 || this.isExempt(ExemptType.PISTON)) {
                return;
            }
            double maxGroundSpeed = this.getSpeed(0.287);
            double maxAirSpeed = this.getSpeed(0.362);
            final double maxAfterJumpAirSpeed = this.getAfterJumpSpeed();
            final int sinceIceTicks = position.getSinceIceTicks();
            final int sinceSlimeTicks = position.getSinceSlimeTicks();
            final int sinceUnderBlockTicks = position.getSinceBlockNearHeadTicks();
            final boolean velocityExempt = this.isExempt(ExemptType.VELOCITY);
            if (velocityExempt) {
                final double velocityXz = Math.hypot(velocity.getVelocityX(), velocity.getVelocityZ()) + 0.5;
                maxAirSpeed += velocityXz;
                maxGroundSpeed += velocityXz;
            }
            if (deltaY > 0.4199 && this.airTicks == 1) {
                speed = deltaXZ / maxAfterJumpAirSpeed;
                this.lastJumpTick = this.data.existedTicks;
            }
            if (this.airTicks > 1 || (this.airTicks > 0 && deltaY < 0.4199)) {
                if (sinceUnderBlockTicks <= 15) {
                    maxAirSpeed += 0.3;
                }
                if (sinceIceTicks <= 15 || sinceSlimeTicks <= 10) {
                    maxAirSpeed += 0.25;
                }
                speed = deltaXZ / maxAirSpeed;
            }
            if (this.groundTicks > 0) {
                if (this.groundTicks < 11) {
                    if ((this.data.existedTicks - this.lastJumpTick > 8 && posY <= this.lastLandY + 0.25) || this.data.getPositionProcessor().getSinceBlockNearHeadTicks() < 5) {
                        maxGroundSpeed += 0.17;
                    }
                    this.lastLandY = posY;
                }
                if (sinceUnderBlockTicks <= 15) {
                    maxGroundSpeed += 0.15;
                }
                if (sinceIceTicks <= 15 || sinceSlimeTicks <= 10) {
                    maxGroundSpeed += 0.2;
                }
                speed = deltaXZ / maxGroundSpeed;
            }
            final double shiftedSpeed = Math.round(speed * 100.0);
            if (shiftedSpeed > 110) {
                final double buffer = getBuffer() + ((shiftedSpeed > 150.0) ? 60.0 : 20.0);
                setBuffer(buffer);
                if(Math.round(speed * 100.0) > 116) {
                    return;
                }
                debug(Math.round(speed * 100.0));
                if (buffer > 400.0 || shiftedSpeed > 1000.0) {
                    this.fail(String.format("speed=%o%%", Math.round(speed * 100.0)));
                    setBuffer(Math.min(350.0, getBuffer()));
                }
            }
            else {
                setBuffer(Math.max(getBuffer() - 5.0, 0.0));
            }
        }
    }

    private double getSpeed(double movement) {
        if (PlayerUtil.getPotionLevel(this.data.getPlayer(), PotionEffectType.SPEED) > 0) {
            movement *= 1.0 + 0.2 * PlayerUtil.getPotionLevel(this.data.getPlayer(), PotionEffectType.SPEED);
        }
        return movement;
    }

    private double getAfterJumpSpeed() {
        return 0.62 + 0.033 * PlayerUtil.getPotionLevel(this.data.getPlayer(), PotionEffectType.SPEED);
    }
}
