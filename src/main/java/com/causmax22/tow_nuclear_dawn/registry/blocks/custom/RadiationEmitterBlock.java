package com.causmax22.tow_nuclear_dawn.registry.blocks.custom;

import com.causmax22.tow_nuclear_dawn.registry.event.radiation.Radiation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class RadiationEmitterBlock extends Block {
    private final double srdOutput;

    public RadiationEmitterBlock(Properties properties, double srdOutput) {
        super(properties);
        this.srdOutput = srdOutput;
    }

    // This runs when the block is placed in the world
    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!level.isClientSide) {
            // Tell our API that this coordinate is now "Hot"
            Radiation.registerSource(pos, this.srdOutput);
        }
        super.onPlace(state, level, pos, oldState, isMoving);
    }

    // This runs when the block is broken or exploded
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) { // Ensure it's actually gone, not just state-changed
            if (!level.isClientSide) {
                // Remove it from the "Hot List" so the lag stops
                Radiation.unregisterSource(pos);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}
