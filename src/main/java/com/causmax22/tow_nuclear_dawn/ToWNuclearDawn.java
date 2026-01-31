package com.causmax22.tow_nuclear_dawn;

import com.causmax22.tow_nuclear_dawn.block.ModBlocks;
import com.causmax22.tow_nuclear_dawn.item.ModItems;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ToWNuclearDawn.MODID)
public class ToWNuclearDawn {
    public static final String MODID = "townuclear";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ToWNuclearDawn(IEventBus modEventBus, ModContainer modContainer) {

        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);

        //RIGHT HERE:

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);



        modEventBus.addListener(this::addCreative);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            //Uranium
            event.accept(ModItems.RAW_URANIUM_ORE);
            event.accept(ModItems.CRUSHED_URANIUM_ORE);
            event.accept(ModItems.YELLOW_CAKE);
            event.accept(ModItems.URANIUM_DIOXIDE);
            event.accept(ModItems.URANIUM_PELLET);
            //Sulfur
            event.accept(ModItems.SULFUR);
            //Silica/cpu processing
            event.accept(ModItems.SILICA_POWDER);
            event.accept(ModItems.SILICA_SHEET);
            event.accept(ModItems.POLISHED_SILICA_SHEET);
            event.accept(ModItems.PERFECTLY_POLISHED_SILICA_SHEET);
            event.accept(ModItems.SILICA_WAFER);
            event.accept(ModItems.PERFECT_SILICA_WAFER);
            event.accept(ModItems.HIGH_QUALITY_CPU);
            event.accept(ModItems.LOW_QUALITY_CPU);
        }
        if(event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS){
            event.accept(ModBlocks.URANIUM_ORE);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }
}
