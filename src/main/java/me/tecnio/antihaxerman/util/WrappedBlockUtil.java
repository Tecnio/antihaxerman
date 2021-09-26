package me.tecnio.antihaxerman.util;

import me.tecnio.antihaxerman.util.type.AABB;
import me.tecnio.antihaxerman.util.type.WrappedBlock;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
public abstract class WrappedBlockUtil {

    public final Block obBlock;
    public float strength;
    public AABB hitbox;
    public AABB[] collisionBoxes;
    public boolean solid;
    public float slipperiness;
    public int clientVersion;

    public WrappedBlockUtil(Block obBlock, int clientVersion) {
        this.obBlock = obBlock;
        this.clientVersion = clientVersion;
    }


    public abstract Object getNMS();

    public abstract void sendPacketToPlayer(Player p);

    //Returns the amount of damage to apply to this block at this tick,
    //given that an entity is currently mining it.
    public abstract float getDamage(HumanEntity entity);

    public abstract boolean isMaterialAlwaysDestroyable();

    public float getStrength() {
        return strength;
    }

    public Block getBukkitBlock() {
        return obBlock;
    }

    public AABB getHitBox() {
        return hitbox;
    }

    public AABB[] getCollisionBoxes() {
        return collisionBoxes;
    }

    public boolean isColliding(AABB other) {
        for (AABB cBox : collisionBoxes) {
            if (cBox.isColliding(other))
                return true;
        }
        return false;
    }

    public static WrappedBlock getWrappedBlock(Block b, int clientVersion) {
        return new WrappedBlock(b, clientVersion);
    }

    public float getSlipperiness() {
        return slipperiness;
    }

    public boolean isSolid() {
        return solid;
    }

    public Vector getFlowDirection() {
        return new Vector();
    }
}
