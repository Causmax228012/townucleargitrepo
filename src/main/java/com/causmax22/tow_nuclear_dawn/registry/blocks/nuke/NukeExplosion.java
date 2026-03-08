package com.causmax22.tow_nuclear_dawn.registry.blocks.nuke;

import com.causmax22.tow_nuclear_dawn.registry.event.radiation.Radiation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class NukeExplosion {

    private static final double FALLOUT_INITIAL_SRD = 50000.0;
    private static final int    FALLOUT_GRID_SPACING = 48;
    private static final int    FALLOUT_RADIUS       = 200;

    public static void flashAndFallout(ServerLevel level, BlockPos center) {
        // ── Blast sound ───────────────────────────────────────────────────────
        level.playSound(null, (double) center.getX(), (double) center.getY(), (double) center.getZ(),
                SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 10.0f, 0.5f);

        // ── Mushroom cloud particles ──────────────────────────────────────────
        spawnMushroomCloud(level, center);

        // ── Fallout ───────────────────────────────────────────────────────────
        registerFallout(level, center);

        // ── Warn all players ──────────────────────────────────────────────────
        level.players().forEach(p ->
                p.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("§4☢ A NUCLEAR DEVICE HAS DETONATED ☢"), true
                )
        );
    }

    // ── Mushroom Cloud ────────────────────────────────────────────────────────

    private static void spawnMushroomCloud(ServerLevel level, BlockPos center) {
        double baseY = (double) center.getY();
        double baseX = (double) center.getX();
        double baseZ = (double) center.getZ();

        // Stem
        for (int y = 0; y < 100; y++) {
            double radius = y < 50 ? 5.0 : (100 - y) * 0.3;
            for (int i = 0; i < 8; i++) {
                double angle = (i / 8.0) * Math.PI * 2;
                double px = baseX + Math.cos(angle) * radius;
                double pz = baseZ + Math.sin(angle) * radius;
                double py = baseY + y;
                sendLongDistanceParticles(level, ParticleTypes.EXPLOSION,   px, py, pz, 3, 1.0, 0.5, 1.0, 0.1);
                sendLongDistanceParticles(level, ParticleTypes.LARGE_SMOKE, px, py, pz, 2, 1.0, 0.5, 1.0, 0.05);
            }
        }

        // Cap
        double capY = baseY + 100.0;
        for (int r = 0; r <= 60; r += 5) {
            for (int i = 0; i < 16; i++) {
                double angle = (i / 16.0) * Math.PI * 2;
                double px = baseX + Math.cos(angle) * r;
                double pz = baseZ + Math.sin(angle) * r;
                sendLongDistanceParticles(level, ParticleTypes.EXPLOSION,   px, capY, pz, 5, 2.0, 1.0, 2.0, 0.1);
                sendLongDistanceParticles(level, ParticleTypes.LARGE_SMOKE, px, capY, pz, 3, 2.0, 1.0, 2.0, 0.05);
            }
        }
    }

    /**
     * Sends particles to every player on the server with longDistance=true
     * so they render regardless of distance.
     */
    private static <T extends ParticleOptions> void sendLongDistanceParticles(
            ServerLevel level, T particle,
            double x, double y, double z,
            int count, double dx, double dy, double dz, double speed) {
        for (ServerPlayer player : level.players()) {
            level.sendParticles(player, particle, true, x, y, z, count, dx, dy, dz, speed);
        }
    }

    // ── Fallout ───────────────────────────────────────────────────────────────

    private static void registerFallout(ServerLevel level, BlockPos center) {
        for (int x = -FALLOUT_RADIUS; x <= FALLOUT_RADIUS; x += FALLOUT_GRID_SPACING) {
            for (int z = -FALLOUT_RADIUS; z <= FALLOUT_RADIUS; z += FALLOUT_GRID_SPACING) {
                if (x*x + z*z <= FALLOUT_RADIUS * FALLOUT_RADIUS) {
                    BlockPos falloutPos = level.getHeightmapPos(
                            net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE,
                            center.offset(x, 0, z)
                    );
                    Radiation.registerSource(falloutPos, FALLOUT_INITIAL_SRD);
                }
            }
        }
        Radiation.RadiationSavedData.get(level).setSources(Radiation.getActiveSources());
        Radiation.RadiationSavedData.get(level).setDirty();
    }
}