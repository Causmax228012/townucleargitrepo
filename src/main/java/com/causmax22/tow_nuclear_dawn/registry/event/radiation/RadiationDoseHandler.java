package com.causmax22.tow_nuclear_dawn.registry.event.radiation;

import com.causmax22.tow_nuclear_dawn.ToWNuclearDawn;
import com.causmax22.tow_nuclear_dawn.registry.effect.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ToWNuclearDawn.MODID)
public class RadiationDoseHandler {

    // ── Passive DNA repair rates (Srd per tick) ───────────────────────────────
    // REPAIR_SAFE must be > 0.01 to suppress 1 Srd/mc-h (0.01 Srd/tick dose gain)
    // Setting to 0.015 gives a comfortable safety margin at background levels
    private static final double REPAIR_SAFE     = 0.015;          // suppresses up to 1.5 Srd/mc-h
    private static final double REPAIR_MILD     = 1.0 / 6000.0;   // ~0.00017 Srd/tick
    private static final double REPAIR_MODERATE = 1.0 / 24000.0;  // ~0.000042 Srd/tick
    private static final double REPAIR_SEVERE   = 1.0 / 96000.0;  // ~0.00001 Srd/tick
    private static final double REPAIR_LETHAL   = 1.0 / 384000.0; // barely any

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

        PlayerDoseCapability dose = DoseCapabilityProvider.get(player);

        // ── Thyroid Blocker check ─────────────────────────────────────────────
        // If active, skip ALL dose accumulation — radiation can't get in
        boolean blocked = player.hasEffect(ModEffects.THYROID_BLOCKED);

        if (!blocked) {
            double intensity = Radiation.getTotalExposure(player.level(), player);
            // Only excess above 1.5 Srd/mc-h accumulates (body suppresses background)
            double netIntensity = Math.max(0, intensity - 1.5);
            double doseDelta = (netIntensity / 100.0) - getPassiveRepair(dose.getAccumulatedDose());
            dose.addDose(doseDelta);
        } else {
            // Blocked — only passive repair runs
            dose.addDose(-getPassiveRepair(dose.getAccumulatedDose()));
        }

        applyRadiationEffects(player, dose.getAccumulatedDose());
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!player.level().isClientSide()) {
                DoseCapabilityProvider.get(player).reset();
            }
        }
    }

    // ── Scaled passive repair ─────────────────────────────────────────────────

    /**
     * Returns passive repair per tick scaled to current dose severity.
     * Higher dose = slower natural recovery (DNA damage is harder to fix).
     */
    private static double getPassiveRepair(double dose) {
        if      (dose >= PlayerDoseCapability.THRESHOLD_LETHAL)   return REPAIR_LETHAL;
        else if (dose >= PlayerDoseCapability.THRESHOLD_SEVERE)   return REPAIR_SEVERE;
        else if (dose >= PlayerDoseCapability.THRESHOLD_MODERATE) return REPAIR_MODERATE;
        else if (dose >= PlayerDoseCapability.THRESHOLD_MILD)     return REPAIR_MILD;
        else                                                       return REPAIR_SAFE;
    }

    /**
     * Returns the repair stat used in the dose formula when exposed to radiation.
     * Same scaling as passive repair but expressed as repair units for the formula.
     */
    private static double getRepairStat(double dose) {
        return getPassiveRepair(dose) * 100.0;
    }

    // ── Effect Application ────────────────────────────────────────────────────

    private static void applyRadiationEffects(Player player, double dose) {
        // Clear all radiation effects first — prevents tier sticking
        player.removeEffect(MobEffects.CONFUSION);
        player.removeEffect(MobEffects.WEAKNESS);
        player.removeEffect(MobEffects.HUNGER);
        player.removeEffect(MobEffects.WITHER);

        int duration = 60;

        if (dose >= PlayerDoseCapability.THRESHOLD_LETHAL) {
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, duration, 1, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,  duration, 2, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.HUNGER,    duration, 2, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.WITHER,    duration, 1, false, true));

        } else if (dose >= PlayerDoseCapability.THRESHOLD_SEVERE) {
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, duration, 1, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,  duration, 1, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.HUNGER,    duration, 1, false, true));

        } else if (dose >= PlayerDoseCapability.THRESHOLD_MODERATE) {
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, duration, 0, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,  duration, 0, false, true));

        } else if (dose >= PlayerDoseCapability.THRESHOLD_MILD) {
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, duration, 0, false, true));
        }
        // SAFE: everything already cleared
    }
}