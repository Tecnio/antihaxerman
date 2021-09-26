package me.tecnio.antihaxerman.util.type;

import me.tecnio.antihaxerman.util.WrappedEntityHumanUtil;
import net.minecraft.server.v1_8_R3.EntityHuman;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public class WrappedEntityHuman extends WrappedEntity implements WrappedEntityHumanUtil {

    public WrappedEntityHuman(Entity entity) {
        super(entity);
    }

    @Override
    public boolean canHarvestBlock(Block block) {
        Object obj = WrappedBlock.getWrappedBlock(block, 8).getNMS();
        net.minecraft.server.v1_8_R3.Block b = (net.minecraft.server.v1_8_R3.Block) obj;
        return ((EntityHuman) nmsEntity).b(b);
    }

    @Override
    public float getCurrentPlayerStrVsBlock(Block block, boolean flag) {
        Object obj = WrappedBlock.getWrappedBlock(block, 8).getNMS();
        net.minecraft.server.v1_8_R3.Block b = (net.minecraft.server.v1_8_R3.Block) obj;
        return ((EntityHuman) nmsEntity).a(b);
    }

    @Override
    public void releaseItem() {
        ((EntityHuman) nmsEntity).bU();
    }

    @Override
    public boolean usingItem() {
        return ((EntityHuman) nmsEntity).bS();
    }
}
