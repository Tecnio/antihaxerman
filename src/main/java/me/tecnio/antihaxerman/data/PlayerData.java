package me.tecnio.antihaxerman.data;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.config.Config;
import me.tecnio.antihaxerman.exempt.ExemptProcessor;
import me.tecnio.antihaxerman.manager.AlertManager;
import me.tecnio.antihaxerman.manager.CheckManager;
import me.tecnio.antihaxerman.util.LogUtil;
import me.tecnio.antihaxerman.util.type.ConcurrentEvictingList;
import me.tecnio.antihaxerman.util.type.EntityHelper;
import me.tecnio.antihaxerman.util.type.Pair;
import lombok.Getter;
import lombok.Setter;
import me.tecnio.antihaxerman.data.processor.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public final class PlayerData {

    private final Player player;
    private String clientBrand;
    private int totalViolations, combatViolations, movementViolations, playerViolations, botViolations;
    private long flying, lastFlying, currentTicks, lastKP;
    private final long joinTime = System.currentTimeMillis();
    private long enderpearlTime, respawnTime;
    private boolean exempt, banning;
    private EntityHelper entityHelper;
    public int existedTicks;
    private LogUtil.TextFile logFile;

    private final List<Check> checks = CheckManager.loadChecks(this);
    private final Map<Check, Integer> mapchecks = CheckManager.loadChecksMap(this, checks);
    private final ConcurrentEvictingList<Pair<Location, Integer>> targetLocations = new ConcurrentEvictingList<>(40);

    private final ExemptProcessor exemptProcessor = new ExemptProcessor(this);
    private final CombatProcessor combatProcessor = new CombatProcessor(this);
    private final ActionProcessor actionProcessor = new ActionProcessor(this);
    private final ClickProcessor clickProcessor = new ClickProcessor(this);
    private final PositionProcessor positionProcessor = new PositionProcessor(this);
    private final RotationProcessor rotationProcessor = new RotationProcessor(this);
    private final VelocityProcessor velocityProcessor = new VelocityProcessor(this);
    private final ConnectionProcessor connectionProcessor = new ConnectionProcessor(this);
    private final GhostBlockProcessor ghostBlockProcessor = new GhostBlockProcessor(this);
    private final BotProcessor botProcessor = new BotProcessor();
    public PlayerData(final Player player) {
        this.player = player;
        if (Config.LOGGING_ENABLED) logFile = new LogUtil.TextFile("" + player.getUniqueId(), "\\logs");

        if (player.hasPermission("ahm.alerts")) {
            AlertManager.toggleAlerts(this);
        }

        entityHelper = new EntityHelper();
    }
}
