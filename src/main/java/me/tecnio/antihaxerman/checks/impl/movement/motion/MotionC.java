package me.tecnio.antihaxerman.checks.impl.movement.motion;

import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import me.tecnio.antihaxerman.utils.PlayerUtils;
import org.bukkit.block.BlockFace;

@CheckInfo(name = "Motion", type = "C")
public final class MotionC extends Check {
    public MotionC(PlayerData data) {
        super(data);
    }

    private double startMotion;

    @Override
    public void onMove() {
        if (data.getAirTicks() == 1) startMotion = data.getDeltaY();
        if (!data.isUnderBlock()
                && !PlayerUtils.isOnWeirdBlock(data)
                && !PlayerUtils.inLiquid(data)
                && !data.isNearWall()
                && !data.isOnClimbableBlock()
                && !data.isTakingVelocity()
                && data.teleportTicks() > 20
                && !data.getPlayer().getLocation().clone().getBlock().getRelative(BlockFace.DOWN).getType().toString().toLowerCase().contains("slime")) {
            if (data.getDeltaY() < 0.0 && startMotion > 0 && data.getAirTicks() > 0 && data.getAirTicks() < 6 && data.getLastOnGroundLocation().getY() % 1 <= .01) {
                flag(data, "dist = " + data.getDeltaY() + ", ticks = " + data.getAirTicks());
            }
        }
    }
}
