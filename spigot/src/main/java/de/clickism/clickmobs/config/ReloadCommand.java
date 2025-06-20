/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs.config;

import de.clickism.clickmobs.ClickMobs;
import de.clickism.clickmobs.ClickMobsConfig;
import de.clickism.clickmobs.message.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class ReloadCommand implements CommandExecutor {

    private static final String USAGE = "/clickmobs <reload>";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!command.getLabel().equalsIgnoreCase("clickmobs")) return false;
        if (args.length == 0) {
            sendUsage(sender);
            return false;
        }
        if (!args[0].equalsIgnoreCase("reload")) {
            sendUsage(sender);
            return false;
        }
        if (Permission.RELOAD.lacksAndNotify(sender)) return false;
        try {
            ClickMobsConfig.CONFIG.load();
            Message.RELOAD_SUCCESS.send(sender);
        } catch (Exception exception) {
            ClickMobs.LOGGER.log(Level.SEVERE, "Failed to reload config/messages: ", exception);
            Message.RELOAD_FAIL.send(sender);
        }
        return true;
    }

    private void sendUsage(CommandSender sender) {
        Message.USAGE.send(sender, USAGE);
    }
}
