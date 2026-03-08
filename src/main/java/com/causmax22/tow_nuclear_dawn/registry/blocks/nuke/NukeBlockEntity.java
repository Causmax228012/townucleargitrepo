package com.causmax22.tow_nuclear_dawn.registry.blocks.nuke;

import com.causmax22.tow_nuclear_dawn.registry.blocks.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class NukeBlockEntity extends BlockEntity {

    private static final int COUNTDOWN_TICKS  = 1200; // 60 seconds
    private static final int BLAST_RADIUS     = 160;  // 10 chunks
    private static final int CARVE_SPEED      = 2;    // blocks per tick
    private static final int CLOUD_TICKS      = 600;  // 30 seconds of cloud

    private boolean armed         = false;
    private int ticksRemaining    = COUNTDOWN_TICKS;
    private boolean detonated     = false;
    private int carveRadius       = 0;
    private int cloudTicksLeft    = 0;

    public NukeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.NUKE_BE.get(), pos, state);
    }

    public void arm() {
        this.armed = true;
        this.ticksRemaining = COUNTDOWN_TICKS;
        setChanged();
    }

    public boolean isArmed()         { return armed; }
    public int getSecondsRemaining() { return ticksRemaining / 20; }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        // ── Phase 1: Countdown ────────────────────────────────────────────────
        if (armed && !detonated) {
            ticksRemaining--;

            if (ticksRemaining % 200 == 0 || (ticksRemaining <= 200 && ticksRemaining % 20 == 0)) {
                int seconds = getSecondsRemaining();
                String color = seconds <= 10 ? "§4" : "§c";
                serverLevel.players().forEach(p -> {
                    if (p.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 500 * 500) {
                        p.displayClientMessage(
                                Component.literal(color + "☢ NUCLEAR DETONATION IN " + seconds + " SECONDS ☢"), true
                        );
                    }
                });
            }

            if (ticksRemaining <= 0) {
                detonated     = true;
                carveRadius   = 0;
                cloudTicksLeft = CLOUD_TICKS;
                NukeExplosion.flashAndFallout(serverLevel, pos);
            }

            setChanged();
            return;
        }

        // ── Phase 2: Gradual crater carving ───────────────────────────────────
        if (detonated && carveRadius <= BLAST_RADIUS) {
            int rSq    = carveRadius * carveRadius;
            int prevSq = Math.max(0, carveRadius - CARVE_SPEED) * Math.max(0, carveRadius - CARVE_SPEED);

            for (int x = -carveRadius; x <= carveRadius; x++) {
                for (int y = -carveRadius; y <= carveRadius; y++) {
                    for (int z = -carveRadius; z <= carveRadius; z++) {
                        int distSq = x*x + y*y + z*z;
                        if (distSq <= rSq && distSq > prevSq) {
                            BlockPos target = pos.offset(x, y, z);
                            if (target.getY() < level.getMinBuildHeight() + 1) continue;
                            if (!level.getBlockState(target).isAir()) {
                                level.setBlock(target, Blocks.AIR.defaultBlockState(), 3);
                            }
                        }
                    }
                }
            }

            carveRadius += CARVE_SPEED;

            if (carveRadius > BLAST_RADIUS && cloudTicksLeft <= 0) {
                serverLevel.removeBlock(pos, false);
            }

            setChanged();
        }

        // ── Phase 3: Sustained mushroom cloud for 30 seconds ─────────────────
        if (detonated && cloudTicksLeft > 0) {
            spawnCloudFrame(serverLevel, pos);
            cloudTicksLeft--;

            // Remove block entity once both carving and cloud are done
            if (carveRadius > BLAST_RADIUS && cloudTicksLeft <= 0) {
                serverLevel.removeBlock(pos, false);
            }

            setChanged();
        }
    }

    private void spawnCloudFrame(ServerLevel level, BlockPos center) {
        double baseX = center.getX();
        double baseY = center.getY();
        double baseZ = center.getZ();

        // Stem — spawn a slice every tick
        for (int y = 0; y < 100; y += 5) {
            double radius = y < 50 ? 5.0 : (100 - y) * 0.3;
            for (int i = 0; i < 6; i++) {
                double angle = (i / 6.0) * Math.PI * 2;
                double px = baseX + Math.cos(angle) * radius;
                double pz = baseZ + Math.sin(angle) * radius;
                sendLong(level, ParticleTypes.LARGE_SMOKE, px, baseY + y, pz, 1, 0.5, 0.5, 0.5, 0.02);
            }
        }

        // Cap
        double capY = baseY + 100.0;
        for (int r = 0; r <= 60; r += 10) {
            for (int i = 0; i < 8; i++) {
                double angle = (i / 8.0) * Math.PI * 2;
                double px = baseX + Math.cos(angle) * r;
                double pz = baseZ + Math.sin(angle) * r;
                sendLong(level, ParticleTypes.LARGE_SMOKE, px, capY, pz, 1, 1.0, 0.5, 1.0, 0.02);
            }
        }
    }

    private <T extends ParticleOptions> void sendLong(ServerLevel level, T particle,
                                                      double x, double y, double z, int count,
                                                      double dx, double dy, double dz, double speed) {
        for (ServerPlayer player : level.players()) {
            level.sendParticles(player, particle, true, x, y, z, count, dx, dy, dz, speed);
        }
    }

    // ── NBT ───────────────────────────────────────────────────────────────────

    @Override
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean("armed",         armed);
        tag.putBoolean("detonated",     detonated);
        tag.putInt("ticksRemaining",    ticksRemaining);
        tag.putInt("carveRadius",       carveRadius);
        tag.putInt("cloudTicksLeft",    cloudTicksLeft);
    }

    @Override
    public void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        armed          = tag.getBoolean("armed");
        detonated      = tag.getBoolean("detonated");
        ticksRemaining = tag.getInt("ticksRemaining");
        carveRadius    = tag.getInt("carveRadius");
        cloudTicksLeft = tag.getInt("cloudTicksLeft");
    }
}