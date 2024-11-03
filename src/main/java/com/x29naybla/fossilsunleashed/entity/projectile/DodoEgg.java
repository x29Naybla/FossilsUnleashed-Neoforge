package com.x29naybla.fossilsunleashed.entity.projectile;

import com.x29naybla.fossilsunleashed.entity.DodoEntity;
import com.x29naybla.fossilsunleashed.item.ModItems;
import com.x29naybla.fossilsunleashed.registry.EntityRegistry;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class DodoEgg extends ThrowableItemProjectile {
    public DodoEgg(EntityType<? extends DodoEgg> entityType, Level level) {
        super(entityType, level);
    }

    public DodoEgg(Level level, LivingEntity shooter) {
        super(EntityRegistry.DODO_EGG.get(), shooter, level);
    }

    public DodoEgg(Level level, double d, double e, double f) {
        super(EntityRegistry.DODO_EGG.get(), d, e, f, level);
    }

    public void handleEntityEvent(byte id) {
        if (id == 3) {
            double d0 = 0.08;

            for(int i = 0; i < 8; ++i) {
                this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()), this.getX(), this.getY(), this.getZ(), ((double)this.random.nextFloat() - 0.5) * 0.08, ((double)this.random.nextFloat() - 0.5) * 0.08, ((double)this.random.nextFloat() - 0.5) * 0.08);
            }
        }

    }

    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        result.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), 0.0F);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            if (this.random.nextInt(8) == 0) {
                int i = 1;
                if (this.random.nextInt(32) == 0) {
                    i = 4;
                }

                for (int j = 0; j < i; ++j) {
                    DodoEntity dodo = EntityRegistry.DODO.get().create(this.level());
                    dodo.setAge(-24000);
                    dodo.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                    this.level().addFreshEntity(dodo);
                }
            }
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.DODO_EGG.get();
    }
}
