package com.causmax22.tow_nuclear_dawn.registry.item.custom;

import com.causmax22.tow_nuclear_dawn.registry.event.radiation.Radiation;
import com.causmax22.tow_nuclear_dawn.registry.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class GeigerItem extends Item {
    public GeigerItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide) return;

        if (entity instanceof Player player) {
            // Check main hand (isSelected) OR offhand (slot 40)
            boolean inMainHand = isSelected;
            boolean inOffHand  = slotId == 40;
            if (!inMainHand && !inOffHand) return;

            if (player.containerMenu != player.inventoryMenu) return;

            double dose = Radiation.getTotalExposure(level, player);

            if (dose > 0.1) {
                ChatFormatting color;
                if      (dose >= 50.0) color = ChatFormatting.RED;
                else if (dose >= 10.0) color = ChatFormatting.YELLOW;
                else                   color = ChatFormatting.GREEN;

                if (level.getGameTime() % 2 == 0) {
                    Component message = Component.literal("RADIATION: " + String.format("%.1f", dose) + " Srd")
                            .withStyle(color).withStyle(ChatFormatting.BOLD);
                    player.displayClientMessage(message, true);
                }

                double intensity = Math.log10(dose);
                int clickDelay = (int) Math.max(1, 10 - (intensity * 4));

                if (level.getGameTime() % clickDelay == 0) {
                    level.playSound(null, player.blockPosition(),
                            ModSounds.CRACKLE.get(), SoundSource.PLAYERS, 0.5f, 1.0f);

                    if (dose > 0.5 && player.getRandom().nextFloat() < 0.4f) {
                        float randomPitch = 0.8f + player.getRandom().nextFloat() * 0.4f;
                        level.playSound(null, player.blockPosition(),
                                ModSounds.CRACKLE.get(), SoundSource.PLAYERS, 0.4f, randomPitch);
                    }
                }
            } else if (level.getGameTime() % 20 == 0) {
                player.displayClientMessage(Component.empty(), true);
            }
        }
    }

    private ChatFormatting getDigitalColor(int light) {
        if (light >= 13) return ChatFormatting.RED;
        if (light >= 8)  return ChatFormatting.YELLOW;
        return ChatFormatting.GREEN;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("tooltip.townuclear.geiger_counter.shift_down.tooltip"));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.townuclear.geiger_counter.tooltip"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}