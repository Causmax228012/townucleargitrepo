package com.causmax22.tow_nuclear_dawn.item;

import com.causmax22.tow_nuclear_dawn.ToWNuclearDawn;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ToWNuclearDawn.MODID);

    // Copy paste this for a new item

    //URANIUM LINE
    public static final DeferredItem<Item> RAW_URANIUM_ORE = ITEMS.register("raw_uranium_ore",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> CRUSHED_URANIUM_ORE = ITEMS.register("crushed_uranium_ore",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> YELLOW_CAKE = ITEMS.register("yellow_cake",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> URANIUM_DIOXIDE = ITEMS.register("uranium_dioxide",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> URANIUM_PELLET = ITEMS.register("uranium_pellet",
            () -> new Item(new Item.Properties()));

    //SULFUR
    public static final DeferredItem<Item> SULFUR = ITEMS.register("sulfur",
            () -> new Item(new Item.Properties()));

    //SILICA/PROCESOR ITEMS
    public static final DeferredItem<Item> SILICA_POWDER = ITEMS.register("silica_powder",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> SILICA_SHEET = ITEMS.register("silica_sheet",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> POLISHED_SILICA_SHEET = ITEMS.register("polished_silica_sheet",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> PERFECTLY_POLISHED_SILICA_SHEET = ITEMS.register("perfectly_polished_silica_sheet",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> SILICA_WAFER = ITEMS.register("silica_wafer",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> PERFECT_SILICA_WAFER = ITEMS.register("perfect_silica_wafer",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> LOW_QUALITY_CPU = ITEMS.register("lq_cpu",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> HIGH_QUALITY_CPU = ITEMS.register("hq_cpu",
            () -> new Item(new Item.Properties()));


    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
