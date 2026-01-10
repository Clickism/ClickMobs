/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs.util;

//? if >=1.21.11
import net.minecraft.server.permissions.Permissions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class VersionHelper {
    public static void playSound(Player player, SoundEvent soundEvent, SoundSource category, float volume, float pitch) {
        //? if >=1.21.11 {
        player.level().playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                soundEvent,
                category,
                volume,
                pitch
        );
        //?} elif >=1.20.5 {
        /*player.playNotifySound(soundEvent, category, volume, pitch);
         *///?} else
        //player.playNotifySound(soundEvent, category, volume, pitch);
    }

    public static ItemStack getSelectedStack(Inventory inventory) {
        //? if >=1.21.5 {
        return inventory.getSelectedItem();
        //?} else
        //return inventory.getSelected();
    }

    public static int getSelectedSlot(Inventory inventory) {
        //? if >=1.21.5 {
        return inventory.getSelectedSlot();
        //?} else
        //return inventory.selected;
    }

    public static MinecraftServer getServer(Entity entity) {
        return entity.level().getServer();
    }

    public static Level getWorld(Entity entity) {
        return entity.level();
    }

    public static boolean isOp(Player player) {
        //? if >=1.21.11 {
        var perms = player.permissions();
        return perms.hasPermission(Permissions.COMMANDS_ADMIN)
               || perms.hasPermission(Permissions.COMMANDS_OWNER);
        //?} else
        //return player.hasPermissions(3);
    }

    public static boolean isOp(CommandSourceStack source) {
        //? if >=1.21.11 {
        var perms = source.permissions();
        return perms.hasPermission(Permissions.COMMANDS_ADMIN)
               || perms.hasPermission(Permissions.COMMANDS_OWNER);
        //?} else
        //return source.hasPermission(3);
    }

    public static boolean isOpOrInSinglePlayer(CommandSourceStack source) {
        var player = source.getPlayer();
        if (player != null && player.level().getServer().isSingleplayer()) {
            return true;
        }
        return isOp(source);
    }

    public static boolean isVillagerDataHolder(Entity entity) {
        //? if >=1.21.11 {
        return entity instanceof net.minecraft.world.entity.npc.villager.VillagerDataHolder;
        //?} else
        //return entity instanceof net.minecraft.world.entity.npc.VillagerDataHolder;
    }

    public static void moveEntity(Entity entity, BlockPos blockPos) {
        //? if >=1.21.5 {
        entity.snapTo(blockPos, 0, 0);
        //?} else
        //entity.moveTo(blockPos, 0, 0);
    }
}
