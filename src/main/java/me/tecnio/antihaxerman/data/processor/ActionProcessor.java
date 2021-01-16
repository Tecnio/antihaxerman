/*
 *  Copyright (C) 2020 Tecnio
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>
 */

package me.tecnio.antihaxerman.data.processor;

import io.github.retrooper.packetevents.packetwrappers.play.in.blockdig.WrappedPacketInBlockDig;
import io.github.retrooper.packetevents.packetwrappers.play.in.clientcommand.WrappedPacketInClientCommand;
import io.github.retrooper.packetevents.packetwrappers.play.in.entityaction.WrappedPacketInEntityAction;
import lombok.Getter;
import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.data.PlayerData;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@Getter
public final class ActionProcessor {

    private final PlayerData data;

    private boolean sprinting, sneaking, sendingAction, placing, digging, blocking,
            inventory, respawning, sendingDig;

    private int lastDiggingTick, lastPlaceTick, lastBreakTick;

    private int sprintingTicks, sneakingTicks;

    public ActionProcessor(final PlayerData data) {
        this.data = data;
    }

    public void handleEntityAction(final WrappedPacketInEntityAction wrapper) {
        sendingAction = true;
        switch (wrapper.getAction()) {
            case START_SPRINTING:
                sprinting = true;
                break;
            case STOP_SPRINTING:
                sprinting = false;
                break;
            case START_SNEAKING:
                sneaking = true;
                break;
            case STOP_SNEAKING:
                sneaking = false;
                break;
        }
    }

    public void handleBlockDig(final WrappedPacketInBlockDig wrapper) {
        sendingDig = true;
        switch (wrapper.getDigType()) {
            case START_DESTROY_BLOCK:
                digging = true;
                lastDiggingTick = AntiHaxerman.INSTANCE.getTickManager().getTicks();
                break;
            case STOP_DESTROY_BLOCK:
            case ABORT_DESTROY_BLOCK:
                digging = false;
                break;
            case RELEASE_USE_ITEM:
                blocking = false;
                break;
        }
    }

    public void handleClientCommand(final WrappedPacketInClientCommand wrapper) {
        switch (wrapper.getClientCommand()) {
            case OPEN_INVENTORY_ACHIEVEMENT:
                inventory = true;
                break;
            case PERFORM_RESPAWN:
                respawning = true;
                break;
        }
    }

    public void handleBlockPlace() {
        placing = true;
        if (data.getPlayer().getItemInHand().toString().contains("SWORD")) blocking = true;
    }

    public void handleCloseWindow() {
        inventory = false;
    }

    public void handleInteract(final PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            digging = true;
        }
    }

    public void handleBukkitPlace() {

    }

    public void handleBukkitBlockBreak() {
        ;
    }

    public void handleArmAnimation() {
        if (digging) lastDiggingTick = AntiHaxerman.INSTANCE.getTickManager().getTicks();
    }

    public void handleFlying() {
        if (digging) lastDiggingTick = AntiHaxerman.INSTANCE.getTickManager().getTicks();
        if (placing) lastPlaceTick = AntiHaxerman.INSTANCE.getTickManager().getTicks();
        if (digging) lastBreakTick = AntiHaxerman.INSTANCE.getTickManager().getTicks();

        sendingAction = false;
        placing = false;
        respawning = false;

        if (sprinting) ++sprintingTicks;
        else sprintingTicks = 0;

        if (sneaking) ++sneakingTicks;
        else sneakingTicks = 0;
    }
}