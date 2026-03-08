package com.causmax22.tow_nuclear_dawn.registry.item.custom;

import com.causmax22.tow_nuclear_dawn.registry.event.radiation.DoseCapabilityProvider;
import com.causmax22.tow_nuclear_dawn.registry.event.radiation.PlayerDoseCapability;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class DosimeterItem extends Item {

    public DosimeterItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide) return;

        if (entity instanceof Player player) {
            boolean inMainHand = isSelected;
            boolean inOffHand  = slotId == 40;
            if (!inMainHand && !inOffHand) return;

            if (player.containerMenu != player.inventoryMenu) return;

            // Update every 20 ticks (once per second) to avoid spam
            if (level.getGameTime() % 20 != 0) return;

            PlayerDoseCapability dose = DoseCapabilityProvider.get(player);
            double accumulated = dose.getAccumulatedDose();

            // Pick color based on severity
            ChatFormatting color;
            String icon;
            if      (accumulated >= PlayerDoseCapability.THRESHOLD_LETHAL)   { color = ChatFormatting.DARK_RED;    icon = "☠"; }
            else if (accumulated >= PlayerDoseCapability.THRESHOLD_SEVERE)   { color = ChatFormatting.RED;         icon = "⚠"; }
            else if (accumulated >= PlayerDoseCapability.THRESHOLD_MODERATE) { color = ChatFormatting.GOLD;        icon = "⚠"; }
            else if (accumulated >= PlayerDoseCapability.THRESHOLD_MILD)     { color = ChatFormatting.YELLOW;      icon = "⚠"; }
            else                                                              { color = ChatFormatting.GREEN;       icon = "✔"; }

            Component message = Component.literal(
                    "☢ " + icon + " " + String.format("%.2f", accumulated) + " Srd"
            ).withStyle(color).withStyle(ChatFormatting.BOLD);

            player.displayClientMessage(message, true);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("tooltip.townuclear.dosimeter.shift_down.tooltip"));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.townuclear.dosimeter.tooltip"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}