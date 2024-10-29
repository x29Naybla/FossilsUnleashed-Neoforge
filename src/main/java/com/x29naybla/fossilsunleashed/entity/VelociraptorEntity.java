package com.x29naybla.fossilsunleashed.entity;

import com.x29naybla.fossilsunleashed.block.ModBlocks;
import com.x29naybla.fossilsunleashed.block.custom.VelociraptorEggBlock;
import com.x29naybla.fossilsunleashed.registry.EntityRegistry;
import com.x29naybla.fossilsunleashed.util.ModTags;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SyncedDataHolder;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TurtleEggBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
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
    private static final EntityDataAccessor<Boolean> HAS_EGG = SynchedEntityData.defineId(VelociraptorEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LAYING_EGG = SynchedEntityData.defineId(VelociraptorEntity.class, EntityDataSerializers.BOOLEAN);
    int layEggCounter;

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
        if (this.isSleeping()){
            event.setAnimation(SLEEP);
        }else if (isInSittingPose()){
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
        this.goalSelector.addGoal(0, new VelociraptorLayEggGoal(this, 1.0));
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.3F));
        this.goalSelector.addGoal(1, new VelociraptorBreedGoal(this, 1.0));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new SleepGoal());
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.1, true));
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this, new Class[0])).setAlertOthers(new Class[0]));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, Player.class, 10, true, false, target -> isAngryAt((LivingEntity) target)));
        this.targetSelector.addGoal(5, new NonTameRandomTargetGoal(this, Animal.class, false, PREY_SELECTOR));
        this.targetSelector.addGoal(6, new ResetUniversalAngerTargetGoal(this, true));
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
    public void aiStep() {
        super.aiStep();
        if (this.isAlive() && this.isLayingEgg() && this.layEggCounter >= 1 && this.layEggCounter % 5 == 0) {
            BlockPos blockpos = this.blockPosition();
            if (VelociraptorEggBlock.onSand(this.level(), blockpos)) {
                this.level().levelEvent(2001, blockpos, Block.getId(this.level().getBlockState(blockpos.below())));
                this.gameEvent(GameEvent.ENTITY_ACTION);
            }
        }
    }

    @Override
    public void customServerAiStep() {
        if (this.getMoveControl().hasWanted()) {
            this.setSprinting(this.getMoveControl().getSpeedModifier() >= 1.1);
        }else {
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
        this.setSleeping(false);
    }

    public boolean hasEgg() {
        return this.entityData.get(HAS_EGG);
    }

    void setHasEgg(boolean hasEgg) {
        this.entityData.set(HAS_EGG, hasEgg);
    }

    public boolean isLayingEgg() {
        return this.entityData.get(LAYING_EGG);
    }

    void setLayingEgg(boolean isLayingEgg) {
        this.layEggCounter = isLayingEgg ? 1 : 0;
        this.entityData.set(LAYING_EGG, isLayingEgg);
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_REMAINING_ANGER_TIME, 0);
        builder.define(HAS_EGG, false);
        builder.define(LAYING_EGG, false);
    }

    public boolean isSleeping() {
        return this.getFlag(32);
    }

    public void setSleeping(boolean bl) {
        this.setFlag(32, bl);
    }

    private void setFlag(int i, boolean bl) {
        if (bl) {
            this.entityData.set(DATA_FLAGS_ID, (byte)(this.entityData.get(DATA_FLAGS_ID) | i));
        } else {
            this.entityData.set(DATA_FLAGS_ID, (byte)(this.entityData.get(DATA_FLAGS_ID) & ~i));
        }

    }

    private boolean getFlag(int i) {
        return (this.entityData.get(DATA_FLAGS_ID) & i) != 0;
    }

    void wakeUp() {
        this.setSleeping(false);
    }

    public void tick() {
        super.tick();
        if (this.isEffectiveAi()) {
            if (this.isInWater() || this.getTarget() != null || this.level().isThundering()) {
                this.wakeUp();
            }

            if (this.isInWater() || this.isSleeping()) {
                this.setInSittingPose(false);
            }
        }

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

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        VelociraptorEntity velociraptor = (VelociraptorEntity) EntityRegistry.VELOCIRAPTOR.get().create(level());
        if (this.isTame()) {
            velociraptor.setOwnerUUID(this.getOwnerUUID());
            velociraptor.setTame(true, true);
        }

        return velociraptor;
    }

    public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        if (!(target instanceof Creeper) && !(target instanceof Ghast) && !(target instanceof ArmorStand)) {
            if (!(target instanceof VelociraptorEntity)) {
                if (target instanceof Player) {
                    Player player = (Player)target;
                    if (owner instanceof Player) {
                        Player player1 = (Player)owner;
                        if (!player1.canHarmPlayer(player)) {
                            return false;
                        }
                    }
                }

                if (target instanceof AbstractHorse) {
                    AbstractHorse abstracthorse = (AbstractHorse)target;
                    if (abstracthorse.isTamed()) {
                        return false;
                    }
                }

                if (target instanceof TamableAnimal) {
                    TamableAnimal tamableanimal = (TamableAnimal)target;
                    if (tamableanimal.isTame()) {
                        return false;
                    }
                }

                return true;
            } else {
                VelociraptorEntity velociraptor = (VelociraptorEntity)target;
                return !velociraptor.isTame() || velociraptor.getOwner() != owner;
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

    private class SleepGoal extends Goal {
        private final int WAIT_TIME_BEFORE_SLEEP = random.nextInt(100) + 100;
        private int countdown;

        public SleepGoal() {
            super();
            this.countdown = VelociraptorEntity.this.random.nextInt(WAIT_TIME_BEFORE_SLEEP);
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        public boolean canUse() {
            if (VelociraptorEntity.this.xxa == 0.0F && VelociraptorEntity.this.yya == 0.0F && VelociraptorEntity.this.zza == 0.0F) {
                return this.canSleep() || VelociraptorEntity.this.isSleeping();
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return this.canSleep();
        }

        private boolean canSleep() {
            if (this.countdown > 0) {
                --this.countdown;
                return false;
            } else {
                return ((VelociraptorEntity.this.level().getDayTime() >= 1500 && VelociraptorEntity.this.level().getDayTime() <= 10500)) && !VelociraptorEntity.this.isInPowderSnow && !VelociraptorEntity.this.isPassenger();
            }
        }

        public void stop() {
            this.countdown = VelociraptorEntity.this.random.nextInt(WAIT_TIME_BEFORE_SLEEP);
            clearStates();
        }

        public void start() {
            VelociraptorEntity.this.setInSittingPose(false);
            VelociraptorEntity.this.setJumping(false);
            VelociraptorEntity.this.setSleeping(true);
            VelociraptorEntity.this.getNavigation().stop();
            VelociraptorEntity.this.getMoveControl().setWantedPosition(VelociraptorEntity.this.getX(), VelociraptorEntity.this.getY(), VelociraptorEntity.this.getZ(), 0.0);
        }
    }

    static class VelociraptorBreedGoal extends BreedGoal {
        private final VelociraptorEntity velociraptor;

        VelociraptorBreedGoal(VelociraptorEntity velociraptor, double speedModifier) {
            super(velociraptor, speedModifier);
            this.velociraptor = velociraptor;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !this.velociraptor.hasEgg();
        }

        @Override
        protected void breed() {
            ServerPlayer serverplayer = this.animal.getLoveCause();
            if (serverplayer == null && this.partner.getLoveCause() != null) {
                serverplayer = this.partner.getLoveCause();
            }

            if (serverplayer != null) {
                serverplayer.awardStat(Stats.ANIMALS_BRED);
                CriteriaTriggers.BRED_ANIMALS.trigger(serverplayer, this.animal, this.partner, null);
            }

            this.velociraptor.setHasEgg(true);
            this.animal.setAge(6000);
            this.partner.setAge(6000);
            this.animal.resetLove();
            this.partner.resetLove();
            RandomSource randomsource = this.animal.getRandom();
            if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                this.level
                        .addFreshEntity(new ExperienceOrb(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), randomsource.nextInt(7) + 1));
            }
        }
    }

    static class VelociraptorLayEggGoal extends MoveToBlockGoal {
        private final VelociraptorEntity velociraptor;

        VelociraptorLayEggGoal(VelociraptorEntity velociraptor, double speedModifier) {
            super(velociraptor, speedModifier, 16);
            this.velociraptor = velociraptor;
        }

        @Override
        public boolean canUse() {
            return this.velociraptor.hasEgg() ? super.canUse() : false;
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && this.velociraptor.hasEgg();
        }

        @Override
        public void tick() {
            super.tick();
            BlockPos blockpos = this.velociraptor.blockPosition();
            if (!this.velociraptor.isInWater() && this.isReachedTarget()) {
                if (this.velociraptor.layEggCounter < 1) {
                    this.velociraptor.setLayingEgg(true);
                } else if (this.velociraptor.layEggCounter > this.adjustedTickDelay(200)) {
                    Level level = this.velociraptor.level();
                    level.playSound(null, blockpos, SoundEvents.TURTLE_LAY_EGG, SoundSource.BLOCKS, 0.3F, 0.9F + level.random.nextFloat() * 0.2F);
                    BlockPos blockpos1 = this.blockPos.above();
                    BlockState blockstate = ModBlocks.VELOCIRAPTOR_EGG.get().defaultBlockState().setValue(TurtleEggBlock.EGGS, Integer.valueOf(this.velociraptor.random.nextInt(3) + 1));
                    level.setBlock(blockpos1, blockstate, 3);
                    level.gameEvent(GameEvent.BLOCK_PLACE, blockpos1, GameEvent.Context.of(this.velociraptor, blockstate));
                    this.velociraptor.setHasEgg(false);
                    this.velociraptor.setLayingEgg(false);
                    this.velociraptor.setInLoveTime(600);
                }

                if (this.velociraptor.isLayingEgg()) {
                    this.velociraptor.layEggCounter++;
                }
            }
        }

        /**
         * Return {@code true} to set given position as destination
         */
        @Override
        protected boolean isValidTarget(LevelReader level, BlockPos pos) {
            return !level.isEmptyBlock(pos.above()) ? false : VelociraptorEggBlock.isSand(level, pos);
        }
    }
}
