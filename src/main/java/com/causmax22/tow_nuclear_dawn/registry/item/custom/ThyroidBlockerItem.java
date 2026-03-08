package com.causmax22.tow_nuclear_dawn.registry.item.custom;

import com.causmax22.tow_nuclear_dawn.registry.effect.ModEffects;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Thyroid Blocker — right-click to consume one use.
 * Blocks all radiation accumulation for 24 mc-h (24000 ticks).
 * Has 16 uses total, shown as a durability bar.
 *
 * Register in your item DeferredRegister:
 *   ITEMS.register("thyroid_blocker", () -> new ThyroidBlockerItem(
 *       new Item.Properties().durability(16).stacksTo(1)
 *   ));
 */
public class ThyroidBlockerItem extends Item {

    // 24 mc-h = 24 × 50 seconds = 24000 ticks
    public static final int BLOCK_DURATION_TICKS = 24000;

    public ThyroidBlockerItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true; // Always show durability bar
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        // Scale bar to remaining uses (16 = full, 0 = empty)
        return Math.round(13.0f * (stack.getMaxDamage() - stack.getDamageValue()) / stack.getMaxDamage());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            // If already blocked, don't waste a use
            if (player.hasEffect(ModEffects.THYROID_BLOCKED)) {
                player.displayClientMessage(
                        Component.literal("§eThyroid Blocker already active!"), true
                );
                return InteractionResultHolder.fail(stack);
            }

            // Apply the thyroid blocked effect
            player.addEffect(new MobEffectInstance(
                    ModEffects.THYROID_BLOCKED,
                    BLOCK_DURATION_TICKS,
                    0,
                    false,
                    true
            ));

            // Consume one use (damage the item by 1)
            net.minecraft.world.entity.EquipmentSlot slot = hand == InteractionHand.MAIN_HAND ? net.minecraft.world.entity.EquipmentSlot.MAINHAND : net.minecraft.world.entity.EquipmentSlot.OFFHAND;
            if (!player.getAbilities().instabuild) {
                stack.hurtAndBreak(1, player, slot);
            }

            // Play a swallow sound for feedback
            level.playSound(null, player.blockPosition(),
                    SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 1.0f, 1.0f);

            // Show remaining uses

            if (stack.isEmpty()) {
                player.displayClientMessage(
                        Component.literal("§cThyroid Blocker used up!"), true
                );
            } else {
                player.displayClientMessage(
                        Component.literal("§aThyroid Blocker active for 24 mc-h."), true
                );
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("tooltip.townuclear.thyroid_blocker.shift_down.tooltip"));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.townuclear.thyroid_blocker.tooltip"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}