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
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.*;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class VehicleUseEntityCallback implements UseEntityCallback {
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity vehicle, @Nullable EntityHitResult entityHitResult) {
        if (world.isClient()) return ActionResult.PASS;
        if (!hand.equals(Hand.MAIN_HAND)) return ActionResult.PASS;
        if (player.isSpectator()) return ActionResult.PASS;
        if (!player.isSneaking()) return ActionResult.PASS;
        //? if >=1.21.4 {
        if (!(vehicle instanceof VehicleEntity)) return ActionResult.PASS;
        //?} else
        /*if (!(vehicle instanceof MinecartEntity) && !(vehicle instanceof BoatEntity)) return ActionResult.PASS;*/
        if (!hasSpace(vehicle)) return ActionResult.PASS;
        ItemStack itemStack = player.getMainHandStack();
        Entity entity = PickupHandler.readEntityFromItemStack(world, itemStack);
        if (entity == null) return ActionResult.PASS;
        if (entity instanceof LivingEntity && entity instanceof VillagerDataContainer
                && ClickMobs.isClickVillagersPresent()) return ActionResult.PASS;
        world.spawnEntity(entity);
        BlockPos pos = vehicle.getBlockPos();
        entity.refreshPositionAndAngles(pos, 0, 0);
        itemStack.decrement(1);
        entity.startRiding(vehicle);
        //? if >=1.21.4 {
        if (vehicle instanceof AbstractBoatEntity) {
        //?} else
        /*if (vehicle instanceof BoatEntity) {*/
            VersionHelper.playSound(player, SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.MASTER, 1, .5f);
        } else {
            VersionHelper.playSound(player, SoundEvents.BLOCK_METAL_BREAK, SoundCategory.MASTER, 1, .5f);
        }
        return ActionResult.CONSUME;
    }
    
    private boolean hasSpace(Entity entity) {
        if (entity instanceof BoatEntity) {
            return entity.getPassengerList().size() < 2;
        }
        return !entity.hasPassengers();
    }
}
