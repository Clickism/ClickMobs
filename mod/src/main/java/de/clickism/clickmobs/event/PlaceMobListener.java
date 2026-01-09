/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs.event;

import de.clickism.clickmobs.ClickMobs;
import de.clickism.clickmobs.mob.PickupHandler;
import de.clickism.clickmobs.util.VersionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class PlaceMobListener {
    public InteractionResult event(
            Player player,
            Level world,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        if (!hand.equals(InteractionHand.MAIN_HAND)) return InteractionResult.PASS;
        if (hitResult == null) return InteractionResult.PASS;
        if (world.isClientSide()) return InteractionResult.PASS;
        if (player.isSpectator()) return InteractionResult.PASS;
        ItemStack itemStack = player.getMainHandItem();
        Entity entity = PickupHandler.readEntityFromItemStack(world, itemStack);
        if (entity == null) return InteractionResult.PASS;
        if (entity instanceof LivingEntity && VersionHelper.isVillagerDataHolder(entity)
            && ClickMobs.isClickVillagersPresent()) return InteractionResult.PASS;
        BlockPos clickedPos = hitResult.getBlockPos();
        //? if >=1.20.5 {
        InteractionResult actionResult = world.getBlockState(clickedPos).useWithoutItem(world, player, hitResult);
        //?} else
        //InteractionResult actionResult = world.getBlockState(clickedPos).use(world, player, hand, hitResult);
        if (actionResult.consumesAction()) return actionResult;
        BlockPos pos = clickedPos.relative(hitResult.getDirection());
        VersionHelper.moveEntity(entity, pos);
        world.addFreshEntity(entity);
        itemStack.shrink(1);
        if (itemStack.getCount() <= 0) {
            Inventory inventory = player.getInventory();
            int slot = VersionHelper.getSelectedSlot(inventory);
            inventory.setItem(slot, Items.AIR.getDefaultInstance());
        }
        BlockPos posBelow = pos.below();
        VersionHelper.playSound(player, SoundEvents.PLAYER_ATTACK_WEAK, SoundSource.NEUTRAL, 1, .5f);
        ((ServerLevel) world).sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, world.getBlockState(posBelow)),
                pos.getX() + .5, pos.getY(), pos.getZ() + .5,
                30, 0, 0, 0, 1
        );
        return InteractionResult.SUCCESS;
    }
}
