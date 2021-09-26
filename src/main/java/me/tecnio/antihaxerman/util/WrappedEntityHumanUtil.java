package me.tecnio.antihaxerman.util;

import org.bukkit.block.Block;

public interface WrappedEntityHumanUtil {

    boolean canHarvestBlock(Block block);

    /**
     * Returns how strong the player is against the specified block at this moment
     */
    float getCurrentPlayerStrVsBlock(Block block, boolean flag);

    void releaseItem();

    boolean usingItem();
}