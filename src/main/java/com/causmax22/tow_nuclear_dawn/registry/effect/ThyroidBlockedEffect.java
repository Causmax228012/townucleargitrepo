package com.causmax22.tow_nuclear_dawn.registry.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * The "Thyroid Blocked" mob effect.
 * Checked in RadiationDoseHandler — if present, dose accumulation is skipped.
 *
 * Register in a ModEffects DeferredRegister:
 *   public static final DeferredHolder<MobEffect, ThyroidBlockedEffect> THYROID_BLOCKED =
 *       MOB_EFFECTS.register("thyroid_blocked", ThyroidBlockedEffect::new);
 */
public class ThyroidBlockedEffect extends MobEffect {

    public ThyroidBlockedEffect() {
        super(
                MobEffectCategory.BENEFICIAL,
                0x00FF88  // Teal-green color for the effect icon
        );
    }
}