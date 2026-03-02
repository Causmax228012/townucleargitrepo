package com.causmax22.tow_nuclear_dawn.registry.item.custom;

import com.causmax22.tow_nuclear_dawn.registry.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;


import java.util.Map;

public class GeigerItem extends Item {
    public GeigerItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        // 1. Only run on the server to sync sounds, and only if the player is holding it
        if (!level.isClientSide && entity instanceof Player player && isSelected) {

            // Get light level (your radiation placeholder)
            int light = level.getBrightness(LightLayer.BLOCK, player.blockPosition());

            if (light > 0) {
                // --- SECTION 1: FRANTIC SOUNDS ---
                // Level 13-15 attempts 40 sounds/tick. Level 7-12 attempts 10.
                int maxAttempts = (light >= 13) ? 40 : (light >= 7) ? 10 : 1;

                for (int i = 0; i < maxAttempts; i++) {
                    if (player.getRandom().nextFloat() < 0.25f) {
                        // Slight pitch jitter (0.95 to 1.05) for that digital swarm feel
                        float pitch = 0.95f + player.getRandom().nextFloat() * 0.1f;

                        level.playSound(null, player.blockPosition(),
                                ModSounds.CRACKLE.get(),
                                SoundSource.PLAYERS, 0.4f, pitch);
                    }
                }

                // --- SECTION 2: SHAKING SUBTITLES ---
                if (level.getGameTime() % 2 == 0) { // Update every 2 ticks to keep it readable
                    ChatFormatting color = getDigitalColor(light);

                    // Shake Logic: Add random leading spaces to make the text "vibrate"
                    String shake = "";
                    if (light >= 13) {
                        shake = " ".repeat(player.getRandom().nextInt(3));
                    }

                    // Glitch Logic: Add the 'matrix' scrambled effect at max danger
                    String decoration = (light == 15) ? "§k!§r" : "";

                    // Construct the final message
                    Component readout = Component.literal(shake + decoration + " RADIATION: " + light + " mSv/h " + decoration)
                            .withStyle(style -> style.withColor(color)
                                    .withBold(light >= 13));

                    // Send to Action Bar (Subtitle)
                    player.displayClientMessage(readout, true);
                }
            }
        }
    }

    // Keep this below the inventoryTick method
    private ChatFormatting getDigitalColor(int light) {
        if (light >= 13) return ChatFormatting.RED;
        if (light >= 7) return ChatFormatting.YELLOW;
        return ChatFormatting.GREEN;
    }
}