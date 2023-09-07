package me.tecnio.ahm.check;

import lombok.Getter;
import lombok.Setter;
import me.tecnio.ahm.AHM;
import me.tecnio.ahm.alert.AlertManager;
import me.tecnio.ahm.check.api.Buffer;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.api.enums.CheckState;
import me.tecnio.ahm.config.ConfigManager;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.exempt.ExemptType;
import me.tecnio.ahm.util.string.ChatUtil;
import org.atteo.classindex.IndexSubclasses;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
@IndexSubclasses
public abstract class Check {

    protected final PlayerData data;
    protected final Player player;

    private final CheckManifest info;

    private final String name, type, description;
    private final CheckState state;
    private final int decay;

    protected final Buffer buffer;

    private final boolean enabled, punishing;
    private final List<String> punishments;
    private final int maxVl;

    @Setter
    private int violations;

    public Check(final PlayerData data) {
        this.data = data;
        this.player = data.getPlayer();

        if (this.getClass().isAnnotationPresent(CheckManifest.class)) {
            this.info = this.getClass().getDeclaredAnnotation(CheckManifest.class);

            this.name = this.info.name();
            this.type = this.info.type();
            this.description = this.info.description();

            this.state = this.info.state();

            this.decay = this.info.decay();

            this.buffer = new Buffer(this.info.maxBuffer());
        } else {
            this.info = null;
            throw new IllegalStateException("The CheckManifest annotation has not been added on " + this.getClass().getName());
        }

        final String name = this.name + "." + this.type;
        final ConfigManager config = AHM.get(ConfigManager.class);

        this.enabled = config.getEnabledMap().get(name);
        this.punishing = config.getPunishMap().get(name);
        this.punishments = config.getCommandsMap().get(name);
        this.maxVl = config.getMaxViolationsMap().get(name);
    }

    protected final void fail() {
        this.fail("No information.");
    }

    protected final void fail(final String debug) {
        ++this.violations;

        AHM.get(AlertManager.class).handleAlert(this, debug);

        if (this.violations >= this.maxVl) {
            AHM.get(AlertManager.class).handlePunishment(this);
        }
    }

    protected final void fail(final String debug, final Object... params) {
        this.fail(String.format(debug, params));
    }

    protected final boolean isExempt(final ExemptType... exemptTypes) {
        return data.getExemptTracker().isExempt(exemptTypes);
    }

    protected final void debug(final Object object, final Object... objects) {
        data.getPlayer().sendMessage(ChatUtil.translate(String.format("&6AntiHaxerman Debug &8> " + ChatColor.WHITE + object.toString(), objects)));
    }

    protected boolean canClick() {
        return !data.getActionTracker().isPlacing();
    }
}
