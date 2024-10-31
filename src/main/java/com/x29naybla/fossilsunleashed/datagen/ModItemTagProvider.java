package com.x29naybla.fossilsunleashed.datagen;

import com.x29naybla.fossilsunleashed.FossilsUnleashed;
import com.x29naybla.fossilsunleashed.block.ModBlocks;
import com.x29naybla.fossilsunleashed.item.ModItems;
import com.x29naybla.fossilsunleashed.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                              CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, FossilsUnleashed.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        //Fossils Unleashed Tags
        tag(ModTags.Items.BIOMASS)
                .addTag(Tags.Items.FOODS)
                .addTag(Tags.Items.ANIMAL_FOODS)
                .addTag(Tags.Items.SEEDS)
                .addTag(Tags.Items.BONES)
                .addTag(Tags.Items.LEATHERS)
                .addTag(Tags.Items.CROPS)
                .addTag(Tags.Items.EGGS)
                .addTag(Tags.Items.MUSHROOMS);

        tag(ModTags.Items.DODO_FOOD)
                .addTag(ItemTags.CHICKEN_FOOD);

        tag(ModTags.Items.VELOCIRAPTOR_FOOD)
                .addTag(ItemTags.WOLF_FOOD);

        //Neoforge Tags
        tag(Tags.Items.EGGS)
                .add(ModItems.DODO_EGG.get())
                .add(ModBlocks.VELOCIRAPTOR_EGG.asItem());

        tag(Tags.Items.FOODS_RAW_MEAT)
                .add(ModItems.VELOCIRAPTOR.get());

        tag(Tags.Items.FOODS_COOKED_MEAT)
                .add(ModItems.COOKED_VELOCIRAPTOR.get());

        tag(Tags.Items.FOODS_FOOD_POISONING)
                .add(ModItems.VELOCIRAPTOR.get());

        //Minecraft Tags
        tag(ItemTags.MEAT)
                .add(ModItems.VELOCIRAPTOR.get())
                .add(ModItems.COOKED_VELOCIRAPTOR.get());
    }
}
