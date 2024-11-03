package com.x29naybla.fossilsunleashed.registry;

import com.x29naybla.fossilsunleashed.FossilsUnleashed;
import com.x29naybla.fossilsunleashed.entity.DodoEntity;
import com.x29naybla.fossilsunleashed.entity.VelociraptorEntity;
import com.x29naybla.fossilsunleashed.entity.projectile.DodoEgg;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@EventBusSubscriber(modid = FossilsUnleashed.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, FossilsUnleashed.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<DodoEntity>> DODO = register("dodo", DodoEntity::new, 0.4f, 0.7f);
    public static final DeferredHolder<EntityType<?>, EntityType<VelociraptorEntity>> VELOCIRAPTOR = register("velociraptor", VelociraptorEntity::new, 0.4f, 0.7f);

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        AttributeSupplier.Builder dodoAttributes = PathfinderMob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED,0.25)
                .add(Attributes.MAX_HEALTH, 6);

        AttributeSupplier.Builder velociraptorAttributes = PathfinderMob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 16)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE,4)
                .add(Attributes.MAX_HEALTH, 8);

        event.put(EntityRegistry.DODO.get(), dodoAttributes.build());
        event.put(EntityRegistry.VELOCIRAPTOR.get(), velociraptorAttributes.build());
    }

    public static final Supplier<EntityType<DodoEgg>> DODO_EGG = ENTITY_TYPES.register("dodo_egg", () -> (
            EntityType.Builder.<DodoEgg>of(DodoEgg::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(16)
                    .build("dodo_egg")));

    private static <T extends Mob> DeferredHolder<EntityType<?>, EntityType<T>> register(String name, EntityType.EntityFactory<T> entity, float width, float height) {
        return ENTITY_TYPES.register(name, () -> EntityType.Builder.of(entity, MobCategory.CREATURE).sized(width, height).build(name));
    }
}
