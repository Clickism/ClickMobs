/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;

public class VersionHelper {
    public static void playSound(PlayerEntity player, SoundEvent soundEvent, SoundCategory category, float volume, float pitch) {
        //? if >=1.21.1 {
        player.playSoundToPlayer(soundEvent, category, volume, pitch);
        //?} else
        /*player.playSound(soundEvent, category, volume, pitch);*/
    }

    public static ItemStack getSelectedStack(PlayerInventory inventory) {
        //? if >=1.21.5 {
        return inventory.getSelectedStack();
        //?} else
        /*return inventory.getMainHandStack();*/
    }

    public static int getSelectedSlot(PlayerInventory inventory) {
        //? if >=1.21.5 {
        return inventory.getSelectedSlot();
        //?} else
        /*return inventory.selectedSlot;*/
    }

    public static MinecraftServer getServer(Entity entity) {
        //? if >=1.21.9 {
        return entity.getEntityWorld().getServer();
        //?} else
        /*return entity.getServer();*/
    }

    public static World getWorld(Entity entity) {
        //? if >=1.21.9 {
        return entity.getEntityWorld();
        //?} else
        /*return entity.getWorld();*/
    }
}
