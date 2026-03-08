package com.causmax22.tow_nuclear_dawn.registry.event.radiation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Stores the player's accumulated radiation dose in Srd.
 *
 * Units:
 *   - dose     : accumulated Srd (total absorbed radiation)
 *   - 1 Srd    = 0.1 mSv
 *   - 1 mc-h   = 50 seconds = 1000 ticks
 */
public class PlayerDoseCapability {

    // ── Codec (used by DoseCapabilityProvider for NBT save/load) ─────────────
    public static final Codec<PlayerDoseCapability> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.DOUBLE.fieldOf("accumulatedDose").forGetter(PlayerDoseCapability::getAccumulatedDose)
            ).apply(instance, PlayerDoseCapability::new)
    );

    // ── Thresholds (in Srd) ───────────────────────────────────────────────────
    public static final double THRESHOLD_MILD     =   1.0;  // 1 Srb - safe dose for a Steve
    public static final double THRESHOLD_MODERATE = 150.0;
    public static final double THRESHOLD_SEVERE   = 300.0;
    public static final double THRESHOLD_LETHAL   = 500.0;

    // ── Natural repair rate ───────────────────────────────────────────────────
    // 0.001 Srd/tick = slow background recovery
    public static final double NATURAL_REPAIR_PER_TICK = 0.001;

    // ── Internal state ────────────────────────────────────────────────────────
    private double accumulatedDose;

    // Default constructor (required by NeoForge attachment factory)
    public PlayerDoseCapability() {
        this.accumulatedDose = 0.0;
    }

    // Codec constructor
    private PlayerDoseCapability(double accumulatedDose) {
        this.accumulatedDose = accumulatedDose;
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public double getAccumulatedDose() {
        return accumulatedDose;
    }

    public void setAccumulatedDose(double dose) {
        this.accumulatedDose = Math.max(0.0, dose);
    }

    public void addDose(double delta) {
        this.accumulatedDose = Math.max(0.0, this.accumulatedDose + delta);
    }

    public void applyNaturalRepair() {
        if (this.accumulatedDose > 0) {
            this.accumulatedDose = Math.max(0.0, this.accumulatedDose - NATURAL_REPAIR_PER_TICK);
        }
    }

    public void reset() {
        this.accumulatedDose = 0.0;
    }
}