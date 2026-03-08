package com.causmax22.tow_nuclear_dawn.registry.event;

import com.causmax22.tow_nuclear_dawn.registry.event.radiation.Radiation;
import com.causmax22.tow_nuclear_dawn.registry.item.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = "townuclear", value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(ModItems.GEIGER_COUNTER.get(),
                    ResourceLocation.fromNamespaceAndPath("townuclear", "radiation_level"),
                    (stack, level, entity, seed) -> {
                        if (entity == null || level == null) return 0.0f;

                        double intensity = Radiation.getTotalExposure(level, entity instanceof net.minecraft.world.entity.player.Player p ? p : null);

                        if (intensity < 0.1)  return 0.0f; // Off
                        if (intensity < 10.0) return 1.0f; // Green
                        if (intensity < 50.0) return 2.0f; // Yellow
                        return 3.0f;                       // Red
                    });
        });
    }
}