package com.causmax22.tow_nuclear_dawn.registry.event.radiation;

import com.causmax22.tow_nuclear_dawn.ToWNuclearDawn;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = ToWNuclearDawn.MODID)
public class Radiation {

    // 1 Srd = 0.1 mSv
    // ── In-memory map (fast, wiped on restart) ────────────────────────────────
    private static final Map<BlockPos, Double> ACTIVE_SOURCES = new ConcurrentHashMap<>();

    // ── Public API ────────────────────────────────────────────────────────────

    public static void registerSource(BlockPos pos, double srd) {
        ACTIVE_SOURCES.put(pos, srd);
    }

    public static Map<BlockPos, Double> getActiveSources() {
        return ACTIVE_SOURCES;
    }

    public static void unregisterSource(BlockPos pos) {
        ACTIVE_SOURCES.remove(pos);
    }

    // ── World Save: flush memory → SavedData ──────────────────────────────────

    @SubscribeEvent
    public static void onWorldSave(LevelEvent.Save event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!level.dimension().equals(Level.OVERWORLD)) return;

        RadiationSavedData data = RadiationSavedData.get(level);
        data.setSources(ACTIVE_SOURCES);
        data.setDirty(); // marks it for saving
    }

    // ── World Load: restore SavedData → memory ────────────────────────────────

    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!level.dimension().equals(Level.OVERWORLD)) return;

        ACTIVE_SOURCES.clear();
        ACTIVE_SOURCES.putAll(RadiationSavedData.get(level).getSources());
    }

    // ── Exposure Calculation ──────────────────────────────────────────────────

    public static double getTotalExposure(Level level, Player player) {
        double totalDose = 0;

        Vec3 headPos = player.getEyePosition();
        Vec3 footPos = player.position();

        for (Map.Entry<BlockPos, Double> entry : ACTIVE_SOURCES.entrySet()) {
            BlockPos sourcePos = entry.getKey();
            double sourceStrength = entry.getValue();
            Vec3 start = Vec3.atCenterOf(sourcePos);

            double doseAtHead = calculatePointExposure(level, start, headPos, sourceStrength, sourcePos);
            double doseAtFeet = calculatePointExposure(level, start, footPos, sourceStrength, sourcePos);

            totalDose += Math.max(doseAtHead, doseAtFeet);
        }
        return totalDose;
    }

    private static double calculatePointExposure(Level level, Vec3 source, Vec3 target, double strength, BlockPos sourcePos) {
        double distSq = target.distanceToSqr(source);
        if (distSq < 0.1) distSq = 0.1;

        double totalShielding = 1.0;
        double distance = Math.sqrt(distSq);
        Vec3 direction = target.subtract(source).normalize();

        for (double d = 1.0; d < distance; d += 1.0) {
            BlockPos checkPos = BlockPos.containing(source.add(direction.scale(d)));
            if (!checkPos.equals(sourcePos)) {
                BlockState state = level.getBlockState(checkPos);
                if (!state.isAir()) {
                    totalShielding *= getBlockShielding(state);
                }
            }
            if (totalShielding < 0.0001) break;
        }

        return (strength / distSq) * totalShielding;
    }

    public static double getBlockShielding(BlockState state) {
        Block block = state.getBlock();

        if (block == Blocks.COAL_BLOCK)     return 0.01;
        if (block == Blocks.IRON_BLOCK)     return 0.2;
        if (block == Blocks.OBSIDIAN)       return 0.4;
        if (block == Blocks.WHITE_CONCRETE) return 0.5;

        return 0.95;
    }

    // ── SavedData inner class ─────────────────────────────────────────────────

    public static class RadiationSavedData extends SavedData {

        private static final String DATA_NAME = ToWNuclearDawn.MODID + "_radiation_sources";
        private final Map<BlockPos, Double> sources = new ConcurrentHashMap<>();

        public static RadiationSavedData get(ServerLevel level) {
            return level.getDataStorage().computeIfAbsent(
                    new SavedData.Factory<>(
                            RadiationSavedData::new,
                            ( tag, provider) -> RadiationSavedData.load(tag, provider)
                    ),
                    DATA_NAME
            );
        }

        // Load from NBT (called on world load)
        public static RadiationSavedData load(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
            RadiationSavedData data = new RadiationSavedData();
            ListTag list = tag.getList("sources", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag entry = list.getCompound(i);
                BlockPos pos = new BlockPos(entry.getInt("x"), entry.getInt("y"), entry.getInt("z"));
                double srd = entry.getDouble("srd");
                data.sources.put(pos, srd);
            }
            return data;
        }

        // Save to NBT (called on world save)
        @Override
        public CompoundTag save(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
            ListTag list = new ListTag();
            for (Map.Entry<BlockPos, Double> entry : sources.entrySet()) {
                CompoundTag e = new CompoundTag();
                e.putInt("x", entry.getKey().getX());
                e.putInt("y", entry.getKey().getY());
                e.putInt("z", entry.getKey().getZ());
                e.putDouble("srd", entry.getValue());
                list.add(e);
            }
            tag.put("sources", list);
            return tag;
        }

        public Map<BlockPos, Double> getSources() { return sources; }

        public void setSources(Map<BlockPos, Double> incoming) {
            sources.clear();
            sources.putAll(incoming);
        }
    }
}