/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickmobs.config;

import me.clickism.clickmobs.message.MessageType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum Permission {
    PICKUP,
    PLACE,
    RELOAD;

    private static final String PLUGIN_PREFIX = "clickmobs";
    private final String permission;

    Permission() {
        String name = name().replace('_', '-').toLowerCase();
        permission = PLUGIN_PREFIX + "." + name;
    }

    public boolean has(CommandSender player) {
        return player.hasPermission(permission);
    }

    public boolean lacks(CommandSender player) {
        return !has(player);
    }

    public boolean lacksAndNotify(CommandSender player) {
        if (lacks(player)) {
            MessageType.FAIL.send(player, "You don't have permission to do this.");
            return true;
        }
        return false;
    }

    public static boolean hasPickupPermissionFor(CommandSender sender, String mobName) {
        String mobKey = mobName.toLowerCase();
        return sender.hasPermission("clickmobs.pickup." + mobKey);
    }
}
