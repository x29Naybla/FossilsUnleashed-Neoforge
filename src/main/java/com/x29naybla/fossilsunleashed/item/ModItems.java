package com.x29naybla.fossilsunleashed.item;

import com.x29naybla.fossilsunleashed.FossilsUnleashed;
import com.x29naybla.fossilsunleashed.item.custom.DodoEggItem;
import com.x29naybla.fossilsunleashed.registry.EntityRegistry;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FossilsUnleashed.MOD_ID);

    public static final DeferredItem<Item> FOSSIL = ITEMS.register("fossil",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DNA = ITEMS.register("dna",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> CAMBRIAN_FOSSIL = ITEMS.register("cambrian_fossil",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> TRIASSIC_ANIMAL_FOSSIL = ITEMS.register("triassic_animal_fossil",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> JURASSIC_ANIMAL_FOSSIL = ITEMS.register("jurassic_animal_fossil",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CRETACEOUS_ANIMAL_FOSSIL = ITEMS.register("cretaceous_animal_fossil",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> DODO_DNA = ITEMS.register("dodo_dna",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> VELOCIRAPTOR_DNA = ITEMS.register("velociraptor_dna",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> DODO_EGG = ITEMS.register("dodo_egg",
            () -> new DodoEggItem(new Item.Properties()));
    public static final DeferredItem<Item> VELOCIRAPTOR = ITEMS.register("velociraptor",
            () -> new Item(new Item.Properties().food(ModFoodProperties.VELOCIRAPTOR)));
    public static final DeferredItem<Item> COOKED_VELOCIRAPTOR = ITEMS.register("cooked_velociraptor",
            () -> new Item(new Item.Properties().food(ModFoodProperties.COOKED_VELOCIRAPTOR)));

    public static final DeferredItem<Item> DODO_SPAWN_EGG = ITEMS.register("dodo_spawn_egg",
            () -> new SpawnEggItem(EntityRegistry.DODO.get(), 0xa0968e, 0x563b2f, new Item.Properties()));
    public static final DeferredItem<Item> VELOCIRAPTOR_SPAWN_EGG = ITEMS.register("velociraptor_spawn_egg",
            () -> new SpawnEggItem(EntityRegistry.VELOCIRAPTOR.get(), 0xebe4b5, 0xdb822d, new Item.Properties()));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
