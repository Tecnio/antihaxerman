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

package me.tecnio.antihaxerman.data;

import lombok.Getter;
import lombok.Setter;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.config.Config;
import me.tecnio.antihaxerman.data.processor.*;
import me.tecnio.antihaxerman.exempt.ExemptProcessor;
import me.tecnio.antihaxerman.manager.AlertManager;
import me.tecnio.antihaxerman.manager.CheckManager;
import me.tecnio.antihaxerman.util.LogUtil;
import me.tecnio.antihaxerman.util.type.EvictingList;
import me.tecnio.antihaxerman.util.type.Pair;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
@Setter
public final class PlayerData {

    private final Player player;
    private String clientBrand;
    private int totalViolations, combatViolations, movementViolations, playerViolations;
    private long flying, lastFlying;
    private final long joinTime = System.currentTimeMillis();
    private boolean exempt;
    private LogUtil.TextFile logFile;

    private final List<Check> checks = CheckManager.loadChecks(this);

    private final EvictingList<Pair<Location, Integer>> targetLocations = new EvictingList<>(40);

    private final ExemptProcessor exemptProcessor = new ExemptProcessor(this);
    private final CombatProcessor combatProcessor = new CombatProcessor(this);
    private final ActionProcessor actionProcessor = new ActionProcessor(this);
    private final ClickProcessor clickProcessor = new ClickProcessor(this);
    private final PositionProcessor positionProcessor = new PositionProcessor(this);
    private final RotationProcessor rotationProcessor = new RotationProcessor(this);
    private final VelocityProcessor velocityProcessor = new VelocityProcessor(this);
    private final ConnectionProcessor connectionProcessor = new ConnectionProcessor(this);

    public PlayerData(final Player player) {
        this.player = player;
        if (Config.LOGGING_ENABLED) logFile = new LogUtil.TextFile("" + player.getUniqueId(), "\\\\logs");
        AlertManager.toggleAlerts(this);
    }
}
