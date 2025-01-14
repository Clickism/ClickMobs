package me.clickism.clickmobs.config;

import me.clickism.clickmobs.message.MessageType;
import org.bukkit.entity.Player;

public enum Permission {
    PICKUP,
    PLACE;

    private static final String PLUGIN_PREFIX = "clickmobs";
    private final String permission;

    Permission() {
        String name = name().replace('_', '-').toLowerCase();
        permission = PLUGIN_PREFIX + "." + name;
    }

    public boolean has(Player player) {
        return player.hasPermission(permission);
    }

    public boolean lacks(Player player) {
        return !has(player);
    }

    public boolean lacksAndNotify(Player player) {
        if (lacks(player)) {
            MessageType.FAIL.send(player, "You don't have permission to do this.");
            return true;
        }
        return false;
    }
}
