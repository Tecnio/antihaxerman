package me.tecnio.antihaxerman.util;

import me.tecnio.antihaxerman.util.type.WrappedItemStack;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public abstract class WrappedItemStackUtil {

    public static WrappedItemStack getWrappedItemStack(ItemStack obiItemStack) {
        return new WrappedItemStack(obiItemStack);
    }

    public abstract float getDestroySpeed(Block obbBlock);

    public abstract boolean canDestroySpecialBlock(Block obbBlock);
}
