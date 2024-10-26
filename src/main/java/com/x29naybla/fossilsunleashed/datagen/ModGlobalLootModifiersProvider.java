package com.x29naybla.fossilsunleashed.datagen;

import com.x29naybla.fossilsunleashed.FossilsUnleashed;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;

import java.util.concurrent.CompletableFuture;

public class ModGlobalLootModifiersProvider extends GlobalLootModifierProvider {
    public ModGlobalLootModifiersProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, FossilsUnleashed.MOD_ID);
    }

    @Override
    protected void start() {
        //add("soybeans_from_short_grass", new AddItemModifier(new LootItemCondition[]{
                //LootTableIdCondition.builder(ResourceLocation.parse("blocks/short_grass")).build(),
                //LootItemRandomChanceCondition.randomChance(0.25f).build()},
                //ModItems.SOYBEANS.get()));

    }
}
