package com.x29naybla.fossilsunleashed.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;


public class ModFoodProperties {
    public static final FoodProperties DODO = new FoodProperties.Builder().nutrition(2).saturationModifier(0.3F).effect(() -> new MobEffectInstance(MobEffects.HUNGER, 30, 0), 0.6F).build();
    public static final FoodProperties VELOCIRAPTOR = new FoodProperties.Builder().nutrition(2).saturationModifier(0.3F).effect(() -> new MobEffectInstance(MobEffects.HUNGER, 30, 0), 0.3F).build();
    public static final FoodProperties COOKED_VELOCIRAPTOR = new FoodProperties.Builder().nutrition(5).saturationModifier(0.6F).build();
}
