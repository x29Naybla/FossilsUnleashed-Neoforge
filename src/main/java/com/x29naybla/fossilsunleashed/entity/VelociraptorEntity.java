package com.x29naybla.fossilsunleashed.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.x29naybla.fossilsunleashed.registry.EntityRegistry;
import com.x29naybla.fossilsunleashed.util.ModTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;


public class VelociraptorEntity extends TamableAnimal implements GeoEntity {
    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.velociraptor.idle");
    protected static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.velociraptor.walk");
    protected static final RawAnimation RUN = RawAnimation.begin().thenLoop("animation.velociraptor.run");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);


    private static final Ingredient FOOD_ITEMS = Ingredient.of(ModTags.Items.VELOCIRAPTOR_FOOD);

    public VelociraptorEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        this.setTame(false,false);

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::animController));
    }

    protected <E extends VelociraptorEntity> PlayState animController(final AnimationState<E> event) {
        if (event.isMoving()) {
            if (this.isSprinting()) {
                event.setAnimation(RUN);
            } else {
                event.setAnimation(WALK);
            }
        } else {
            event.setAnimation(IDLE);
        }

        return PlayState.CONTINUE;
    }

    protected void registerGoals(){
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1f));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(6, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    protected float getStandingEyeHeight(PoseStack.Pose pose, EntityDimensions entityDimensions) {
        return this.isBaby() ? 0.2F : 0.3F;
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return FOOD_ITEMS.test(itemStack);
    }

    void clearStates() {
        this.setInSittingPose(false);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return (AgeableMob) EntityRegistry.VELOCIRAPTOR.get().create(level());
    }
}
