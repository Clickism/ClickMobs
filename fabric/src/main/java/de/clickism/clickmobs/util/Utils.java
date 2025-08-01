/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

public class Utils {
    /**
     * Title cases a string. (i.E.: "HELLO world" -> "Hello World")
     *
     * @param string the string to title case
     * @return the title cased string
     */
    public static String titleCase(String string) {
        return capitalize(string.toLowerCase());
    }

    /**
     * Capitalizes the first letter of each word in a string.
     *
     * @param string the string to capitalize
     * @return the capitalized string
     */
    public static String capitalize(String string) {
        String[] words = string.split(" ");
        StringBuilder capitalizedString = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) continue;
            capitalizedString.append(capitalizeWord(word)).append(" ");
        }
        return capitalizedString.toString().trim();
    }

    private static String capitalizeWord(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
    
    public static void offerToHand(PlayerEntity player, ItemStack itemStack) {
        PlayerInventory inventory = player.getInventory();
        int selectedSlot = VersionHelper.getSelectedSlot(inventory);
        if (inventory.getStack(selectedSlot).isEmpty()) {
            inventory.insertStack(selectedSlot, itemStack);
            return;
        }
        inventory.offerOrDrop(itemStack);
    }
}
