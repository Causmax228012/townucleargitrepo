package com.causmax22.tow_nuclear_dawn.registry.blocks;

import com.causmax22.tow_nuclear_dawn.ToWNuclearDawn;
import com.causmax22.tow_nuclear_dawn.registry.blocks.custom.RadiationEmitterBlock;
import com.causmax22.tow_nuclear_dawn.registry.blocks.nuke.NukeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.causmax22.tow_nuclear_dawn.registry.item.ModItems.ITEMS;


public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS=
            DeferredRegister.createBlocks(ToWNuclearDawn.MODID);
    //copy-paste here


    public static final DeferredBlock<Block> URANIUM_ORE = registerBlock("uranium_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4f).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE)));

    // Use 'registerBlock' (your helper) instead of 'BLOCKS.register'
    public static final DeferredBlock<RadiationEmitterBlock> RADIATIONEMMITER = registerBlock("radiation_emmiter",
            () -> new RadiationEmitterBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .strength(5.0f)
                    .requiresCorrectToolForDrops(),
                    1000000.0));



    public static final DeferredBlock<NukeBlock> NUKE = registerBlock("nuke",
            () -> new NukeBlock(BlockBehaviour.Properties.of()
                    .strength(10.0f)
                    .requiresCorrectToolForDrops()));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block){
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBLockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBLockItem(String name, DeferredBlock<T> block) {
        ITEMS.register(name, () -> new BlockItem(block.get(),new Item.Properties()));
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}