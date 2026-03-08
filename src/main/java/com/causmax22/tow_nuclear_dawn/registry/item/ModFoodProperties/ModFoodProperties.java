package com.causmax22.tow_nuclear_dawn.registry.item.ModFoodProperties;


import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoodProperties {
    public static final FoodProperties POTASIUMIODINE = new FoodProperties.Builder().nutrition(3).saturationModifier(0.25f)
            .alwaysEdible().effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 200, 0,false, false, false),0.3f).build();
}
