/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickmobs.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

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
}
