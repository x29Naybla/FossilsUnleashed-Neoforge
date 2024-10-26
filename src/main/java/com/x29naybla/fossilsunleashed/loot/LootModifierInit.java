package com.x29naybla.fossilsunleashed.loot;

import com.mojang.serialization.MapCodec;
import com.x29naybla.fossilsunleashed.FossilsUnleashed;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class LootModifierInit {
    public static DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS, FossilsUnleashed.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<AddItemModifier>> ADD_ITEM_MODIFIER = LOOT_MODIFIERS.register("add_item",AddItemModifier.CODEC_SUPPLIER);
}
