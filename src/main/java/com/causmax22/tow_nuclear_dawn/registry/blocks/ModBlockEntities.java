package com.causmax22.tow_nuclear_dawn.registry.blocks;

import com.causmax22.tow_nuclear_dawn.ToWNuclearDawn;
import com.causmax22.tow_nuclear_dawn.registry.blocks.nuke.NukeBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ToWNuclearDawn.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NukeBlockEntity>> NUKE_BE =
            BLOCK_ENTITIES.register("nuke_be", () ->
                    BlockEntityType.Builder.of(NukeBlockEntity::new, ModBlocks.NUKE.get()).build(null)
            );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}