/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs.event;

import de.clickism.clickmobs.ClickMobs;
import de.clickism.clickmobs.mob.PickupHandler;
import de.clickism.clickmobs.predicate.MobList;
import de.clickism.clickmobs.predicate.MobListParser;
import de.clickism.clickmobs.util.MessageType;
import de.clickism.clickmobs.util.Utils;
import de.clickism.clickmobs.util.VersionHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

import static de.clickism.clickmobs.ClickMobsConfig.BLACKLISTED_MOBS;
import static de.clickism.clickmobs.ClickMobsConfig.WHITELISTED_MOBS;

public class PickupMobListener {

    private final MobListParser parser = new MobListParser();
    private MobList whitelistedMobs;
    private MobList blacklistedMobs;

    public PickupMobListener() {
        WHITELISTED_MOBS.onChange(list -> this.whitelistedMobs = parser.parseMobList(list));
        BLACKLISTED_MOBS.onChange(list -> this.blacklistedMobs = parser.parseMobList(list));
    }

    public InteractionResult event(
            Player player,
            Level world,
            InteractionHand hand,
            Entity entity
    ) {
        if (world.isClientSide()) return InteractionResult.PASS;
        if (entity instanceof LivingEntity && VersionHelper.isVillagerDataHolder(entity)
            && ClickMobs.isClickVillagersPresent()) return InteractionResult.PASS;
        if (!hand.equals(InteractionHand.MAIN_HAND)) return InteractionResult.PASS;
        if (player.isSpectator()) return InteractionResult.PASS;
        if (!player.isShiftKeyDown()) return InteractionResult.PASS;
        if (!(entity instanceof LivingEntity livingEntity)) return InteractionResult.PASS;
        if (livingEntity instanceof Player) return InteractionResult.PASS;
        return handlePickup(player, livingEntity);
    }

    private InteractionResult handlePickup(Player player, LivingEntity entity) {
        Item item = VersionHelper.getSelectedStack(player.getInventory()).getItem();
        if (PickupHandler.isBlacklistedItemInHand(item)) {
            return InteractionResult.PASS;
        }
        if (!canBePickedUp(entity)) {
            MessageType.FAIL.sendActionbar(player, Component.literal("You can't pick up this mob"));
            return InteractionResult.PASS;
        }
        PickupHandler.notifyPickup(player, entity);
        ItemStack itemStack = PickupHandler.toItemStack(entity);
        Utils.offerToHand(player, itemStack);
        return InteractionResult.CONSUME;
    }

    public boolean canBePickedUp(LivingEntity entity) {
        if (whitelistedMobs.contains(entity)) {
            return true;
        }
        return !blacklistedMobs.contains(entity);
    }

}
