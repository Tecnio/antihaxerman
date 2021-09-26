package me.tecnio.antihaxerman.util.type;

import me.tecnio.antihaxerman.util.WrappedEntityUtil;
import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class WrappedEntity extends WrappedEntityUtil {

    protected net.minecraft.server.v1_8_R3.Entity nmsEntity;

    public WrappedEntity(Entity entity) {
        super();
        nmsEntity = ((CraftEntity) entity).getHandle();
        AxisAlignedBB bb = nmsEntity.getBoundingBox();
        collisionBox = new AABB(new Vector(bb.a, bb.b, bb.c), new Vector(bb.d, bb.e, bb.f));
        collisionBorderSize = nmsEntity.ao();
        location = entity.getLocation();
        length = nmsEntity.width;
        width = nmsEntity.width;
        height = nmsEntity.length;
    }

    public static WrappedEntity getWrappedEntity(Entity entity) {
        if(entity instanceof CraftHumanEntity)
            return new WrappedEntityHuman(entity);
        else
            return new WrappedEntity(entity);

    }
}