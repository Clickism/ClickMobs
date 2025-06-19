/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs.config;

import de.clickism.clickmobs.message.Message;
import de.clickism.clickmobs.message.MessageType;
import org.bukkit.command.CommandSender;

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
            Message.NO_PERMISSION.send(player);
            return true;
        }
        return false;
    }

    public static boolean hasPickupPermissionFor(CommandSender sender, String mobName) {
        String mobKey = mobName.toLowerCase();
        return sender.hasPermission("clickmobs.pickup." + mobKey);
    }
}
