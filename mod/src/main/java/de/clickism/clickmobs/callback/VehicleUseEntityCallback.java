/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs.callback;

import de.clickism.clickmobs.ClickMobs;
import de.clickism.clickmobs.mob.PickupHandler;
import de.clickism.clickmobs.util.VersionHelper;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.*;
//? if >=1.21.11
//import net.minecraft.world.entity.vehicle.boat.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class VehicleUseEntityCallback implements UseEntityCallback {
    @Override
    public InteractionResult interact(Player player, Level world, InteractionHand hand, Entity vehicle, @Nullable EntityHitResult entityHitResult) {
        if (world.isClientSide()) return InteractionResult.PASS;
        if (!hand.equals(InteractionHand.MAIN_HAND)) return InteractionResult.PASS;
        if (player.isSpectator()) return InteractionResult.PASS;
        if (!player.isShiftKeyDown()) return InteractionResult.PASS;
        //? if >=1.21.4 {
        /*if (!(vehicle instanceof VehicleEntity)) return InteractionResult.PASS;
        *///?} else
        if (!(vehicle instanceof Minecart) && !(vehicle instanceof Boat)) return InteractionResult.PASS;
        if (!hasSpace(vehicle)) return InteractionResult.PASS;
        ItemStack itemStack = player.getMainHandItem();
        Entity entity = PickupHandler.readEntityFromItemStack(world, itemStack);
        if (entity == null) return InteractionResult.PASS;
        if (entity instanceof LivingEntity && VersionHelper.isVillagerDataHolder(entity)
            && ClickMobs.isClickVillagersPresent()) return InteractionResult.PASS;
        world.addFreshEntity(entity);
        BlockPos pos = vehicle.blockPosition();
        VersionHelper.moveEntity(entity, pos);
        itemStack.shrink(1);
        entity.startRiding(vehicle);
        if (vehicle instanceof Boat) {
            VersionHelper.playSound(player, SoundEvents.WOOD_BREAK, SoundSource.MASTER, 1, .5f);
        } else {
            VersionHelper.playSound(player, SoundEvents.METAL_BREAK, SoundSource.MASTER, 1, .5f);
        }
        return InteractionResult.CONSUME;
    }
    
    private boolean hasSpace(Entity entity) {
        if (entity instanceof Boat) {
            return entity.getPassengers().size() < 2;
        }
        return !entity.isVehicle();
    }
}
