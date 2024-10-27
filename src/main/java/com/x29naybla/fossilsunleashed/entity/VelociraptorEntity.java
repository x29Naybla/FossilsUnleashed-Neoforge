package com.x29naybla.fossilsunleashed.entity;

import com.x29naybla.fossilsunleashed.registry.EntityRegistry;
import com.x29naybla.fossilsunleashed.util.ModTags;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;
import java.util.function.Predicate;


public class VelociraptorEntity extends TamableAnimal implements NeutralMob, GeoEntity {
    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.velociraptor.idle");
    protected static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.velociraptor.walk");
    protected static final RawAnimation RUN = RawAnimation.begin().thenLoop("animation.velociraptor.run");
    protected static final RawAnimation SLEEP = RawAnimation.begin().thenLoop("animation.velociraptor.sleep");
    protected static final RawAnimation SIT = RawAnimation.begin().thenLoop("animation.velociraptor.sit");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private static final Ingredient FOOD_ITEMS = Ingredient.of(ModTags.Items.VELOCIRAPTOR_FOOD);
    public static final Predicate<LivingEntity> PREY_SELECTOR;
    private static final UniformInt PERSISTENT_ANGER_TIME;
    private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME;

    @javax.annotation.Nullable
    private UUID persistentAngerTarget;

    public VelociraptorEntity(EntityType<? extends VelociraptorEntity> entityType, Level level) {
        super(entityType, level);
        this.setTame(false,false);

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::animController));
    }

    protected <E extends VelociraptorEntity> PlayState animController(final AnimationState<E> event) {
        if (isInSittingPose()){
            event.setAnimation(SIT);
        }else if (event.isMoving()) {
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
        this.goalSelector.addGoal(0, new FollowParentGoal(this,1f));
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5F));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F));
        this.goalSelector.addGoal(6, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this, new Class[0])).setAlertOthers(new Class[0]));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, Player.class, 10, true, false, target -> isAngryAt((LivingEntity) target)));
        this.targetSelector.addGoal(5, new NonTameRandomTargetGoal(this, Animal.class, false, PREY_SELECTOR));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, AbstractSkeleton.class, false));
        this.targetSelector.addGoal(8, new ResetUniversalAngerTargetGoal(this, true));
    }



    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_REMAINING_ANGER_TIME, 0);
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    protected void applyTamingSideEffects() {
        if (this.isTame()) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(40.0);
            this.setHealth(40.0F);
        } else {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(8.0);
        }

    }

    public InteractionResult mobInteract(Player player, InteractionHand interactionHand) {

        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (this.level().isClientSide) {
            if (this.isTame() && this.isOwnedBy(player)) {
                return InteractionResult.SUCCESS;
            } else {
                return !this.isFood(itemStack) || !(this.getHealth() < this.getMaxHealth()) && this.isTame() ? InteractionResult.PASS : InteractionResult.SUCCESS;
            }
        } else {
            InteractionResult interactionResult;
            if (this.isTame()) {
                if (this.isOwnedBy(player) && this.isFood(itemStack) && (this.getHealth() < this.getMaxHealth())) {
                    this.usePlayerItem(player, interactionHand, itemStack);
                    this.heal(2.0F);
                    return InteractionResult.CONSUME;
                } else if (this.isOwnedBy(player)) {
                    interactionResult = super.mobInteract(player, interactionHand);
                    if (!interactionResult.consumesAction() || this.isBaby()) {
                        this.setOrderedToSit(!this.isOrderedToSit());
                    }
                    return interactionResult;
                }
            } else if (itemStack.is(Items.BONE)) {
                this.usePlayerItem(player, interactionHand, itemStack);
                if (this.random.nextInt(3) == 0) {
                    this.tame(player);
                    this.setOrderedToSit(true);
                    this.level().broadcastEntityEvent(this, (byte)7);
                } else {
                    this.level().broadcastEntityEvent(this, (byte)6);
                }

                this.setPersistenceRequired();
                return InteractionResult.CONSUME;
            }
        }
        return super.mobInteract(player, interactionHand);
    }

    @Override
    public void customServerAiStep() {
        if (this.getMoveControl().hasWanted()) {
            this.setSprinting(this.getMoveControl().getSpeedModifier() >= 1.5D);
        } else {
            this.setSprinting(false);
        }
        super.customServerAiStep();
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
        VelociraptorEntity velociraptor = (VelociraptorEntity) EntityRegistry.VELOCIRAPTOR.get().create(level());
        if (this.isTame()) {
            velociraptor.setOwnerUUID(this.getOwnerUUID());
            velociraptor.setTame(true, true);
        }

        return velociraptor;
    }

    public boolean canMate(Animal otherAnimal) {
        if (otherAnimal == this) {
            return false;
        } else if (!this.isTame()) {
            return false;
        } else if (otherAnimal instanceof VelociraptorEntity) {
            VelociraptorEntity velociraptor = (VelociraptorEntity)otherAnimal;
            if (!velociraptor.isTame()) {
                return false;
            } else {
                return velociraptor.isInSittingPose() ? false : this.isInLove() && velociraptor.isInLove();
            }
        } else {
            return false;
        }
    }

    static {
        DATA_REMAINING_ANGER_TIME = SynchedEntityData.defineId(VelociraptorEntity.class, EntityDataSerializers.INT);
        PREY_SELECTOR = (p_348295_) -> {
            EntityType<?> entitytype = p_348295_.getType();
            return entitytype == EntityType.CHICKEN || entitytype == EntityType.RABBIT || entitytype == EntityType.ARMADILLO;
        };
        PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return (Integer)this.entityData.get(DATA_REMAINING_ANGER_TIME);
    }

    @Override
    public void setRemainingPersistentAngerTime(int time) {
        this.entityData.set(DATA_REMAINING_ANGER_TIME, time);
    }

    @Override
    public @Nullable UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID target) {
        this.persistentAngerTarget = target;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }
}
