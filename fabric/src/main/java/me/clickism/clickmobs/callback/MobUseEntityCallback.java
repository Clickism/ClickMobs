/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickmobs.callback;

import me.clickism.clickmobs.ClickMobs;
import me.clickism.clickmobs.mob.PickupHandler;
import me.clickism.clickmobs.util.MessageType;
import me.clickism.clickmobs.util.Utils;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MobUseEntityCallback implements UseEntityCallback {
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if (world.isClient()) return ActionResult.PASS;
        if (entity instanceof LivingEntity && entity instanceof VillagerDataContainer
                && ClickMobs.isClickVillagersPresent()) return ActionResult.PASS;
        if (!hand.equals(Hand.MAIN_HAND)) return ActionResult.PASS;
        if (player.isSpectator()) return ActionResult.PASS;
        if (!player.isSneaking()) return ActionResult.PASS;
        if (!(entity instanceof LivingEntity)) return ActionResult.PASS;
        if (hitResult == null) return ActionResult.CONSUME;
        handlePickup(player, entity);
        return ActionResult.CONSUME;
    }

    private void handlePickup(PlayerEntity player, Entity entity) {
        if (!PickupHandler.canBePickedUp(entity)) {
            MessageType.FAIL.sendActionbar(player, Text.literal("You can't pick up this mob"));
        }
        PickupHandler.notifyPickup(player, entity);
        ItemStack itemStack = PickupHandler.toItemStack(entity);
        Utils.offerToHand(player, itemStack);
    }
}
