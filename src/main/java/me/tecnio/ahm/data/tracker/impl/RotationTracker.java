package me.tecnio.ahm.data.tracker.impl;

import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import ac.artemis.packet.wrapper.client.PacketPlayClientLook;
import lombok.Getter;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.data.tracker.Tracker;
import me.tecnio.ahm.update.RotationUpdate;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@Getter
public final class RotationTracker extends Tracker {

    private final Set<Integer> candidates = new HashSet<>();
    private static final double EXPANDER = Math.pow(2, 24);

    private float yaw, pitch,
            lastYaw, lastPitch;

    private float deltaYaw, deltaPitch,
            lastDeltaYaw, lastDeltaPitch, yawAccel, pitchAccel;

    private float divisorX, divisorY;

    private int sensitivity, calcSensitivity;

    private double gcdYaw, gcdPitch, absGcdPitch, absGcdYaw;

    public RotationTracker(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientFlying) {
            this.lastYaw = this.yaw;
            this.lastPitch = this.pitch;

            if (packet instanceof PacketPlayClientLook) {
                final PacketPlayClientLook look = ((PacketPlayClientLook) packet);

                this.yaw = look.getYaw();
                this.pitch = look.getPitch();
            }

            this.lastDeltaYaw = deltaYaw;
            this.lastDeltaPitch = deltaPitch;

            this.deltaYaw = Math.abs(this.yaw - lastYaw);
            this.deltaPitch = Math.abs(this.pitch - lastPitch);

            this.yawAccel = Math.abs(this.deltaYaw - lastDeltaYaw);
            this.pitchAccel = Math.abs(this.deltaPitch - lastDeltaPitch);

            this.absGcdYaw = getGcd((long) Math.abs(deltaYaw), (long) Math.abs(lastDeltaYaw));
            this.absGcdPitch = getGcd((long) Math.abs(deltaPitch), (long) Math.abs(lastDeltaPitch));

            processSensitivity();

            if (packet instanceof PacketPlayClientLook) {
                divisorX = this.getRotationGcd(this.deltaPitch, this.lastDeltaPitch);
                divisorY = this.getRotationGcd(this.deltaYaw, this.lastDeltaYaw);
            }

            data.setRotationUpdate(new RotationUpdate(this.yaw, this.pitch, this.lastYaw, this.lastPitch, this.deltaYaw,
                    this.deltaPitch, this.lastDeltaYaw, this.lastDeltaPitch, this.yawAccel, this.pitchAccel, this.absGcdPitch, this.absGcdYaw));
        }
    }

    private float getRotationGcd(final float current, final float last) {
        final long currentExpanded = (long) (current * EXPANDER);
        final long lastExpanded = (long) (last * EXPANDER);

        return (float) (this.getGcd(currentExpanded, lastExpanded) / EXPANDER);
    }

    private long getGcd(final long current, final long previous) {
        return (previous <= 16384L) ? current : getGcd(previous, current % previous);
    }

    // not my monstrosity, someone else added this
    public void processSensitivity() {
        final float pitch = getPitch();
        final float lastPitch = getLastPitch();

        final float yaw = getYaw();
        final float lastYaw = getLastYaw();

        if (Math.abs(pitch) != 90.0f) {
            final float distanceY = pitch - lastPitch;

            final double error = Math.max(Math.abs(pitch), Math.abs(lastPitch)) * 3.814697265625E-6;

            computeSensitivity(distanceY, error);
        }

        final float distanceX = circularDistance(yaw, lastYaw);

        final double error = Math.max(Math.abs(yaw), Math.abs(lastYaw)) * 3.814697265625E-6;

        computeSensitivity(distanceX, error);

        if (candidates.size() == 1) {
            this.calcSensitivity = this.candidates.iterator().next();
            this.sensitivity = 200 * this.calcSensitivity / 143;
        } else {
            this.sensitivity = -1;

            forEach(candidates::add);
        }
    }

    public void computeSensitivity(final double delta, final double error) {
        final double start = delta - error;
        final double end = delta + error;

        forEach(s -> {
            final double f0 = ((double) s / 142.0) * 0.6 + 0.2;
            final double f = (f0 * f0 * f0 * 8.0) * 0.15;

            final int pStart = (int) Math.ceil(start / f);
            final int pEnd = (int) Math.floor(end / f);

            if (pStart <= pEnd) {
                for (int p = pStart; p <= pEnd; p++) {
                    final double d = p * f;

                    if (!(d >= start && d <= end)) {
                        this.candidates.remove(s);
                    }
                }
            } else {
                this.candidates.remove(s);
            }
        });
    }

    public float circularDistance(final float a, final float b) {
        final float d = Math.abs(a % 360.0f - b % 360.0f);
        return d < 180.0f ? d : 360.0f - d;
    }

    public void forEach(final Consumer<Integer> consumer) {
        for (int s = 0; s <= 143; s++) {
            consumer.accept(s);
        }
    }
}
