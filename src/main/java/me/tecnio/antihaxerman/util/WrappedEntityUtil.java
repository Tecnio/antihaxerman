package me.tecnio.antihaxerman.util;

import me.tecnio.antihaxerman.util.type.AABB;
import me.tecnio.antihaxerman.util.type.WrappedEntity;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public abstract class WrappedEntityUtil {

    public AABB collisionBox;
    public float collisionBorderSize;
    public float length;
    public float width;
    public float height;
    public Location location;

    public static WrappedEntity getWrappedEntity(Entity entity) {
        return WrappedEntity.getWrappedEntity(entity);
    }

    public AABB getCollisionBox(Vector entityPos) {
        Vector move = entityPos.clone().subtract(location.toVector());
        AABB box = getCollisionBox().clone();
        box.translate(move);
        return box;
    }

    public AABB getHitbox(Vector entityPos) {
        AABB box = getCollisionBox(entityPos);
        box.expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
        return box;
    }

    public AABB getCollisionBox() {
        return collisionBox;
    }

    public AABB getHitbox() {
        AABB hitbox = collisionBox.clone();
        hitbox.expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
        return hitbox;
    }

    public float getLength() {
        return length;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}