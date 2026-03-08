package com.causmax22.tow_nuclear_dawn.registry.blocks.nuke;

import com.causmax22.tow_nuclear_dawn.ToWNuclearDawn;
import com.causmax22.tow_nuclear_dawn.registry.event.radiation.Radiation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.Map;

/**
 * Fades fallout radiation over time.
 * Runs every 1000 ticks (~50 seconds = 1 mc-h).
 * Each tick reduces all sources by a decay factor.
 * Sources below a minimum threshold are unregistered.
 *
 * Full decay from 500 Srd → safe over ~several mc-h.
 */
@EventBusSubscriber(modid = ToWNuclearDawn.MODID)
public class NukeFalloutTicker {

    // Decay factor per mc-h — 0.75 means 25% reduction per mc-h
    // 500 → 375 → 281 → 210 → 157 → 118 → 88 → 66 → 49 → 37 → 28 → 21 → 15 → 11 → 8 → 6 → ~safe
    // Full decay to below 1 Srd takes ~16 mc-h (~13 real minutes)
    private static final double DECAY_FACTOR = 0.75;

    // Sources below this are removed entirely
    private static final double MIN_THRESHOLD = 1.5;

    // Decay every 1000 ticks (1 mc-h = 50s = 1000 ticks)
    private static final int DECAY_INTERVAL = 1000;

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        Level level = event.getLevel();
        if (level.isClientSide()) return;
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (!level.dimension().equals(Level.OVERWORLD)) return;
        if (level.getGameTime() % DECAY_INTERVAL != 0) return;

        Map<BlockPos, Double> sources = Radiation.getActiveSources();
        boolean changed = false;

        for (Map.Entry<BlockPos, Double> entry : sources.entrySet()) {
            double newValue = entry.getValue() * DECAY_FACTOR;
            if (newValue < MIN_THRESHOLD) {
                Radiation.unregisterSource(entry.getKey());
                changed = true;
            } else {
                Radiation.registerSource(entry.getKey(), newValue);
                changed = true;
            }
        }

        // Persist changes to SavedData
        if (changed) {
            Radiation.RadiationSavedData.get(serverLevel).setSources(Radiation.getActiveSources());
            Radiation.RadiationSavedData.get(serverLevel).setDirty();
        }
    }
}