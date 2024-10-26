package com.x29naybla.fossilsunleashed.item;

import com.x29naybla.fossilsunleashed.FossilsUnleashed;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FossilsUnleashed.MOD_ID);

    public static final Supplier<CreativeModeTab> FOSSILSUNLEASHED_ITEMS_TAB = CREATIVE_MODE_TAB.register("fossilsunleashed_items_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.FOSSIL.get()))
                    .title(Component.translatable("creativetab.fossilsunleashed.items"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.FOSSIL);
                        output.accept(ModItems.DNA);
                        output.accept(ModItems.VELOCIRAPTOR_SPAWN_EGG);

                    }).build());

    public static final Supplier<CreativeModeTab> FOSSILSUNLEASHED_BLOCKS_TAB = CREATIVE_MODE_TAB.register("fossilsunleashed_blocks_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.FOSSIL.get()))
                    .title(Component.translatable("creativetab.fossilsunleashed.blocks"))
                    .displayItems((itemDisplayParameters, output) -> {

                    }).build());



                        public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
