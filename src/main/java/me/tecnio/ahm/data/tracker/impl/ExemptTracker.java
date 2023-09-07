package me.tecnio.ahm.data.tracker.impl;

import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.exempt.ExemptType;

import java.util.function.Function;

public final class ExemptTracker {

    private final PlayerData data;

    public ExemptTracker(final PlayerData data) {
        this.data = data;
    }

    public boolean isExempt(final ExemptType exemptType) {
        return exemptType.getException().apply(data);
    }

    public boolean isExempt(final ExemptType... exemptTypes) {
        for (final ExemptType exemptType : exemptTypes) {
            if (this.isExempt(exemptType)) {
                return true;
            }
        }

        return false;
    }

    public boolean isExempt(final Function<PlayerData, Boolean> exception) {
        return exception.apply(data);
    }
}
