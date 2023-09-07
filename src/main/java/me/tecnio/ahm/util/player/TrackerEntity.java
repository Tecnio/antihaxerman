package me.tecnio.ahm.util.player;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class TrackerEntity {

    private final int entityId;

    public int serverPosX, serverPosY, serverPosZ;

    private double posX, posY, posZ;
    private double lastPosX, lastPosY, lastPosZ;

    private double otherPlayerMPX, otherPlayerMPY, otherPlayerMPZ;
    private int otherPlayerMPPosRotationIncrements;

    public TrackerEntity(final int id, final int x, final int y, final int z) {
        this.entityId = id;

        this.serverPosX = x;
        this.serverPosY = y;
        this.serverPosZ = z;
    }

    public void update() {
        if (this.otherPlayerMPPosRotationIncrements > 0) {
            final double d0 = this.posX + (this.otherPlayerMPX - this.posX) / this.otherPlayerMPPosRotationIncrements;
            final double d1 = this.posY + (this.otherPlayerMPY - this.posY) / this.otherPlayerMPPosRotationIncrements;
            final double d2 = this.posZ + (this.otherPlayerMPZ - this.posZ) / this.otherPlayerMPPosRotationIncrements;

            this.otherPlayerMPPosRotationIncrements--;
            this.setPosition(d0 ,d1, d2);
        }
    }

    public void setPosition(final double x, final double y, final double z) {
        this.lastPosX = this.posX;
        this.lastPosY = this.posY;
        this.lastPosZ = this.posZ;

        this.posX = x;
        this.posY = y;
        this.posZ = z;
    }

    public void setPosition2(final double x, final double y, final double z) {
        this.otherPlayerMPX = x;
        this.otherPlayerMPY = y;
        this.otherPlayerMPZ = z;

        this.otherPlayerMPPosRotationIncrements = 3;
    }
}
