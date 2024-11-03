package com.x29naybla.fossilsunleashed.entity;

import com.x29naybla.fossilsunleashed.item.ModItems;
import com.x29naybla.fossilsunleashed.registry.EntityRegistry;
import com.x29naybla.fossilsunleashed.util.ModTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;


public class DodoEntity extends Animal implements GeoEntity {
    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.dodo.idle");
    protected static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.dodo.walk");
    protected static final RawAnimation RUN = RawAnimation.begin().thenLoop("animation.dodo.run");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private static final Ingredient FOOD_ITEMS = Ingredient.of(ModTags.Items.DODO_FOOD);
    public int eggTime;

    public DodoEntity(EntityType<? extends DodoEntity> entityType, Level level) {
        super(entityType, level);
        this.eggTime = this.random.nextInt(6000) + 6000;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::animController));
    }

    protected <E extends DodoEntity> PlayState animController(final AnimationState<E> event) {
        if (event.isMoving()) {
            if(isSprinting()){
                event.setAnimation(RUN);
            }else
                event.setAnimation(WALK);
        } else {
            event.setAnimation(IDLE);
        }

        return PlayState.CONTINUE;
    }

    protected void registerGoals(){
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1, (p_335679_) -> {
            return p_335679_.is(ModTags.Items.DODO_FAVORITES);
        }, false));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.3, (p_335679_) -> {
            return p_335679_.is(ModTags.Items.DODO_FOOD);
        }, false));
        this.goalSelector.addGoal(3, new FollowParentGoal(this, 1));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.isAlive() && !this.isBaby() && --this.eggTime <= 0) {
            this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.spawnAtLocation(ModItems.DODO_EGG);
            this.gameEvent(GameEvent.ENTITY_PLACE);
            this.eggTime = this.random.nextInt(6000) + 6000;
        }

    }

    @Override
    public void customServerAiStep() {
        if (this.getMoveControl().hasWanted()) {
            this.setSprinting(this.getMoveControl().getSpeedModifier() >= 1.3);
        }else {
            this.setSprinting(false);
        }
        super.customServerAiStep();
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return (DodoEntity) EntityRegistry.DODO.get().create(level());
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return FOOD_ITEMS.test(itemStack);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("EggLayTime")) {
            this.eggTime = compound.getInt("EggLayTime");
        }

    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("EggLayTime", this.eggTime);
    }
}
