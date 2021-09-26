package me.tecnio.antihaxerman.check.impl.combat.velocity;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;

import java.math.BigDecimal;
import java.math.RoundingMode;

@CheckInfo(name = "Velocity", type = "C", description = "Checks for horizontal velocity modifications.")
public class VelocityC extends Check {

    public VelocityC(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if(packet.isPosition()) {
            double deltaXZ = data.getPositionProcessor().getDeltaXZ();

            double velocityH = data.getVelocityProcessor().getVelocityH();

            velocityH -= movingFlyingV3();

            double totalVelocity = deltaXZ / velocityH;

            if (data.getVelocityProcessor().getVelocityTicks() == 1) {

                if (totalVelocity <= 0.99 && totalVelocity >= 0.0
                        && !data.getPositionProcessor().isOnGround()
                        && data.getPositionProcessor().isLastOnGround()) {
                    if (increaseBuffer() > 1) {
                        fail(String.format("Velocity: %.2f%%", totalVelocity));
                    }
                } else {
                    setBuffer(getBuffer() - Math.min(getBuffer(), 0.09f));
                }
            }

            if (!data.getPositionProcessor().isOnGround() && !data.getPositionProcessor().isLastOnGround()
                    && data.getPositionProcessor().getDeltaY() > .2) {

                if (data.getVelocityProcessor().getVelocityTicks() <= 5) {
                    if (totalVelocity < 0.3) {

                        setBuffer(getBuffer() + (getBuffer() < 10 ? 0.95 : 0));

                        if (getBuffer() > 5) {
                            fail(String.format("Velocity: %.2f%%", totalVelocity) + " (tick)");
                        }
                    } else {
                        setBuffer(getBuffer() - Math.min(getBuffer(), 0.75));
                    }
                }
            }
        }
    }

    public double movingFlyingV3() {

        double preD = 0.01D;

        double mx = data.getPositionProcessor().getX() - data.getPositionProcessor().getLastX();
        double mz = data.getPositionProcessor().getZ() - data.getPositionProcessor().getLastZ();

        float motionYaw = (float) (Math.atan2(mz, mx) * 180.0D / Math.PI) - 90.0F;

        int direction;

        motionYaw -= data.getRotationProcessor().getYaw();

        while (motionYaw > 360.0F)
            motionYaw -= 360.0F;
        while (motionYaw < 0.0F)
            motionYaw += 360.0F;

        motionYaw /= 45.0F;

        float moveS = 0.0F;
        float moveF = 0.0F;

        if (Math.abs(Math.abs(mx) + Math.abs(mz)) > preD) {
            direction = (int) new BigDecimal(motionYaw).setScale(1, RoundingMode.HALF_UP).doubleValue();

            if (direction == 1) {
                moveF = 1F;
                moveS = -1F;


            } else if (direction == 2) {
                moveS = -1F;


            } else if (direction == 3) {
                moveF = -1F;
                moveS = -1F;


            } else if (direction == 4) {
                moveF = -1F;

            } else if (direction == 5) {
                moveF = -1F;
                moveS = 1F;

            } else if (direction == 6) {
                moveS = 1F;

            } else if (direction == 7) {
                moveF = 1F;
                moveS = 1F;

            } else if (direction == 8) {
                moveF = 1F;

            } else if (direction == 0) {
                moveF = 1F;
            }
        }

        moveS *= 0.98F;
        moveF *= 0.98F;

        float strafe = 0.98F, forward = 0.98F;
        float f = strafe * strafe + forward * forward;

        float friction;

        float var3 = (0.6F * 0.91F);
        float getAIMoveSpeed = 0.13000001F;

//        int speedlvl = PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.SPEED);
//        if (speedlvl > 0) {
//            switch (speedlvl) {
//                case 1: {
//                    getAIMoveSpeed = 0.156F;
//                    break;
//                }
//
//                case 2: {
//                    getAIMoveSpeed = 0.18200001F;
//                    break;
//                }
//
//                case 3: {
//                    getAIMoveSpeed = 0.208F;
//                    break;
//                }
//
//                case 4: {
//                    getAIMoveSpeed = 0.23400001F;
//                    break;
//                }
//
//            }
//        }

        float var4 = 0.16277136F / (var3 * var3 * var3);

        if (data.getPositionProcessor().isOnGround()) {
            friction = getAIMoveSpeed * var4;
        } else {
            friction = 0.026F;
        }

        if (f >= 1.0E-4F) {
            f = (float) Math.sqrt(f);
            if (f < 1.0F) {
                f = 1.0F;
            }
            f = friction / f;
            strafe = strafe * f;
            forward = forward * f;
            float f1 = (float) Math.sin(data.getRotationProcessor().getYaw() * (float) Math.PI / 180.0F);
            float f2 = (float) Math.cos(data.getRotationProcessor().getYaw() * (float) Math.PI / 180.0F);
            float motionXAdd = (strafe * f2 - forward * f1);
            float motionZAdd = (forward * f2 + strafe * f1);
            return Math.hypot(motionXAdd, motionZAdd);
        }

        return 0;
    }

}
