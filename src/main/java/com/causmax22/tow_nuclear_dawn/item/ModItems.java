package com.causmax22.tow_nuclear_dawn.item;

import com.causmax22.tow_nuclear_dawn.ToWNuclearDawn;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ToWNuclearDawn.MODID);

    // Copy paste this for a new item
    public static final DeferredItem<Item> RAW_URANIUM_ORE = ITEMS.register("raw_uranium_ore",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> CRUSHED_URANIUM_ORE = ITEMS.register("crushed_uranium_ore",
            () -> new Item(new Item.Properties()));



    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
