package com.causmax22.tow_nuclear_dawn.item;

import com.causmax22.tow_nuclear_dawn.ToWNuclearDawn;
import com.causmax22.tow_nuclear_dawn.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ToWNuclearDawn.MODID);

    public static final Supplier<CreativeModeTab> TOW_NUCLEAR_DAWN_TAB = CREATIVE_MODE_TAB.register("tow_nuclear_dawn_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.CRUSHED_URANIUM_ORE.get()))
                    .title(Component.translatable("creativetab.tow_nuclear_dawn.tow_nuclear_dawn"))
                    .displayItems((itemDisplayParameters, output) -> {
                        //uranium processing
                        output.accept(ModBlocks.URANIUM_ORE);
                        output.accept(ModItems.RAW_URANIUM_ORE);
                        output.accept(ModItems.CRUSHED_URANIUM_ORE);
                        output.accept(ModItems.YELLOW_CAKE);
                        output.accept(ModItems.URANIUM_DIOXIDE);
                        output.accept(ModItems.URANIUM_PELLET);
                        //Sulfur
                        output.accept(ModItems.SULFUR);
                        //Silica/cpu processing
                        output.accept(ModItems.SILICA_POWDER);
                        output.accept(ModItems.PURE_SILICA_POWDER);
                        output.accept(ModItems.SILICA_SHEET);
                        output.accept(ModItems.PURE_SILICA_SHEET);
                        output.accept(ModItems.POLISHED_SILICA_SHEET);
                        output.accept(ModItems.PURE_POLISHED_SILICA_SHEET);
                        output.accept(ModItems.SILICA_WAFER);
                        output.accept(ModItems.PERFECT_SILICA_WAFER);
                        output.accept(ModItems.HIGH_QUALITY_CPU);
                        output.accept(ModItems.LOW_QUALITY_CPU);
                        //Steel Manufacturing
                        output.accept(ModItems.GALVANIZED_ANDESITE_ALLOY);
                        output.accept(ModItems.GALVANIZED_ANDESITE_SHEET);


                    }).build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TAB.register(eventBus);
    }

}
