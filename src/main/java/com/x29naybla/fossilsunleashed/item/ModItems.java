package com.x29naybla.fossilsunleashed.item;

import com.x29naybla.fossilsunleashed.FossilsUnleashed;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FossilsUnleashed.MOD_ID);

    public static final DeferredItem<Item> FOSSIL = ITEMS.register("fossil",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DNA = ITEMS.register("dna",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
