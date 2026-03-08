package com.causmax22.tow_nuclear_dawn.registry.event.radiation;

import com.causmax22.tow_nuclear_dawn.ToWNuclearDawn;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

/**
 * Registers and attaches PlayerDoseCapability to all players.
 *
 * Usage anywhere in code:
 *   PlayerDoseCapability dose = DoseCapabilityProvider.get(player);
 *
 * In your main mod constructor add:
 *   DoseCapabilityProvider.ATTACHMENT_TYPES.register(modEventBus);
 */
public class DoseCapabilityProvider {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ToWNuclearDawn.MODID);

    /**
     * The attachment key. Dose resets on death — no respawn copy.
     * Codec handles all NBT save/load automatically.
     */
    public static final Supplier<AttachmentType<PlayerDoseCapability>> DOSE =
            ATTACHMENT_TYPES.register("player_dose", () ->
                    AttachmentType.builder(PlayerDoseCapability::new)
                            .serialize(PlayerDoseCapability.CODEC)
                            .build()
            );

    public static PlayerDoseCapability get(Player player) {
        return player.getData(DOSE.get());
    }
}