package com.causmax22.tow_nuclear_dawn.registry.effect;

import com.causmax22.tow_nuclear_dawn.ToWNuclearDawn;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registers all custom mob effects for ToW: Nuclear Dawn.
 *
 * In your main mod constructor add:
 *   ModEffects.MOB_EFFECTS.register(modEventBus);
 */
public class ModEffects {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, ToWNuclearDawn.MODID);

    public static final DeferredHolder<MobEffect, ThyroidBlockedEffect> THYROID_BLOCKED =
            MOB_EFFECTS.register("thyroid_blocked", ThyroidBlockedEffect::new);
}