package com.x29naybla.fossilsunleashed.item.custom;

import com.x29naybla.fossilsunleashed.entity.projectile.DodoEgg;
import com.x29naybla.fossilsunleashed.item.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DodoEggItem extends EggItem {
    public DodoEggItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EGG_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!level.isClientSide) {
            DodoEgg dodoegg = new DodoEgg(level, player);
            dodoegg.setItem(new ItemStack(ModItems.DODO_EGG.get()));
            dodoegg.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(dodoegg);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            itemStack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }
}
