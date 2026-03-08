package com.causmax22.tow_nuclear_dawn;

import com.causmax22.tow_nuclear_dawn.registry.*;
import com.causmax22.tow_nuclear_dawn.registry.blocks.ModBlockEntities;
import com.causmax22.tow_nuclear_dawn.registry.blocks.ModBlocks;
import com.causmax22.tow_nuclear_dawn.registry.effect.ModEffects;
import com.causmax22.tow_nuclear_dawn.registry.event.radiation.DoseCapabilityProvider;
import com.causmax22.tow_nuclear_dawn.registry.item.ModItems;
import com.causmax22.tow_nuclear_dawn.registry.sound.ModSounds;
import com.mojang.serialization.Codec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import java.util.function.Supplier;

@Mod(ToWNuclearDawn.MODID)
public class ToWNuclearDawn {
    public static final String MODID = "townuclear";
    public static final Logger LOGGER = LogUtils.getLogger();

    // 1. DATA ATTACHMENT REGISTER
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MODID);

    public ToWNuclearDawn(IEventBus modEventBus, ModContainer modContainer) {
        // Register storage
        ATTACHMENT_TYPES.register(modEventBus);

        // Register registries
        ModCreativeModeTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModSounds.register(modEventBus);
        ModEffects.MOB_EFFECTS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);

        // Register Packet Handler to the Mod Bus
        modEventBus.addListener(this::registerPayloads);

        //the new line
        DoseCapabilityProvider.ATTACHMENT_TYPES.register(modEventBus);


        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    // 3. THE PACKET REGISTRY (No @SubscribeEvent needed)
    private void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(MODID);
    }
}