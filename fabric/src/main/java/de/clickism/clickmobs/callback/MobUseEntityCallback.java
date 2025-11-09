/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs.callback;

import de.clickism.clickmobs.ClickMobs;
import de.clickism.clickmobs.predicate.MobList;
import de.clickism.clickmobs.mob.PickupHandler;
import de.clickism.clickmobs.util.MessageType;
import de.clickism.clickmobs.util.Utils;
import de.clickism.clickmobs.util.VersionHelper;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MobUseEntityCallback implements UseEntityCallback {

    private final MobList whitelistedMobs;
    private final MobList blacklistedMobs;

    public MobUseEntityCallback(MobList whitelistedMobs, MobList blacklistedMobs) {
        this.whitelistedMobs = whitelistedMobs;
        this.blacklistedMobs = blacklistedMobs;
    }

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if (world.isClient()) return ActionResult.PASS;
        if (entity instanceof LivingEntity && entity instanceof VillagerDataContainer
                && ClickMobs.isClickVillagersPresent()) return ActionResult.PASS;
        if (!hand.equals(Hand.MAIN_HAND)) return ActionResult.PASS;
        if (player.isSpectator()) return ActionResult.PASS;
        if (!player.isSneaking()) return ActionResult.PASS;
        if (!(entity instanceof LivingEntity livingEntity)) return ActionResult.PASS;
        if (livingEntity instanceof PlayerEntity) return ActionResult.PASS;
        if (hitResult == null) return ActionResult.CONSUME;
        return handlePickup(player, livingEntity);
    }

    private ActionResult handlePickup(PlayerEntity player, LivingEntity entity) {
        Item item = VersionHelper.getSelectedStack(player.getInventory()).getItem();
        if (PickupHandler.isBlacklistedItemInHand(item)) {
            return ActionResult.PASS;
        }
        if (!canBePickedUp(entity)) {
            MessageType.FAIL.sendActionbar(player, Text.literal("You can't pick up this mob"));
            return ActionResult.PASS;
        }
        PickupHandler.notifyPickup(player, entity);
        ItemStack itemStack = PickupHandler.toItemStack(entity);
        Utils.offerToHand(player, itemStack);
        return ActionResult.CONSUME;
    }

    public boolean canBePickedUp(LivingEntity entity) {
        if (whitelistedMobs.contains(entity)) {
            return true;
        }
        return !blacklistedMobs.contains(entity);
    }
}
