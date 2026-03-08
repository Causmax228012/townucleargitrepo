package com.causmax22.tow_nuclear_dawn.registry.blocks.nuke;

import com.causmax22.tow_nuclear_dawn.registry.blocks.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class NukeBlock extends BaseEntityBlock {

    public static final MapCodec<NukeBlock> CODEC = simpleCodec(NukeBlock::new);

    public NukeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NukeBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, ModBlockEntities.NUKE_BE.get(),
                (lvl, pos, st, be) -> be.tick(lvl, pos, st));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        if (level.getBlockEntity(pos) instanceof NukeBlockEntity nuke) {
            if (!nuke.isArmed()) {
                nuke.arm();
                player.displayClientMessage(
                        Component.literal("§4☢ NUCLEAR DEVICE ARMED — DETONATION IN 60 SECONDS ☢"), true
                );
            } else {
                player.displayClientMessage(
                        Component.literal("§c☢ ALREADY ARMED — " + nuke.getSecondsRemaining() + "s REMAINING"), true
                );
            }
        }
        return InteractionResult.SUCCESS;
    }
}