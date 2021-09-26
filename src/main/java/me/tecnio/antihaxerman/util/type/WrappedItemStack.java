package me.tecnio.antihaxerman.util.type;

import me.tecnio.antihaxerman.util.WrappedItemStackUtil;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class WrappedItemStack extends WrappedItemStackUtil {
    private net.minecraft.server.v1_8_R3.ItemStack itemStack;

    public WrappedItemStack(ItemStack obiItemStack) {
        itemStack = CraftItemStack.asNMSCopy(obiItemStack);
    }

    @Override
    public float getDestroySpeed(Block obbBlock) {
        net.minecraft.server.v1_8_R3.Block block =
                (net.minecraft.server.v1_8_R3.Block) WrappedBlock.getWrappedBlock(obbBlock, 8).getNMS();
        if(itemStack == null)
            return 1F;
        return itemStack.a(block);
    }

    @Override
    public boolean canDestroySpecialBlock(Block obbBlock) {
        net.minecraft.server.v1_8_R3.Block block =
                (net.minecraft.server.v1_8_R3.Block) WrappedBlock.getWrappedBlock(obbBlock, 8).getNMS();
        if(itemStack == null)
            return false;
        return itemStack.b(block);
    }
}
