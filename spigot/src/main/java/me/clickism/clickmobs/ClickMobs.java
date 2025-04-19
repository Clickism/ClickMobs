/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickmobs;

import me.clickism.clickmobs.config.ReloadCommand;
import me.clickism.clickmobs.config.Setting;
import me.clickism.clickmobs.entity.EntitySaver;
import me.clickism.clickmobs.entity.EntitySaverFactory;
import me.clickism.clickmobs.listener.DispenserListener;
import me.clickism.clickmobs.listener.JoinListener;
import me.clickism.clickmobs.listener.VehicleInteractListener;
import me.clickism.clickmobs.message.Message;
import me.clickism.clickmobs.mob.PickupManager;
import me.clickism.clickmobs.util.MessageParameterizer;
import me.clickism.clickmobs.util.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ClickMobs extends JavaPlugin {

    public static final String PROJECT_ID = "clickmobs";

    public static ClickMobs INSTANCE;
    public static Logger LOGGER;

    private @Nullable String newerVersion;

    @Override
    public void onLoad() {
        INSTANCE = this;
        LOGGER = getLogger();
    }

    @Override
    public void onEnable() {
        // Load config/messages
        try {
            Setting.initialize();
            Message.initialize();
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Failed to load config/messages: ", exception);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        EntitySaver entitySaver = EntitySaverFactory.create();
        PickupManager pickupManager = new PickupManager(this, entitySaver);
        new DispenserListener(this, pickupManager);
        new VehicleInteractListener(this, pickupManager);
        // Register commands
        PluginCommand command = Bukkit.getPluginCommand("clickmobs");
        if (command != null) {
            command.setExecutor(new ReloadCommand());
        }
        // Check for updates
        if (Setting.CHECK_UPDATE.isEnabled()) {
            checkUpdates();
            new JoinListener(this, () -> newerVersion);
        }
    }

    private void checkUpdates() {
        LOGGER.info("Checking for updates...");
        new UpdateChecker(PROJECT_ID, "spigot", null).checkVersion(version -> {
            if (getDescription().getVersion().equals(version)) return;
            newerVersion = version;
            LOGGER.info("New version available: " + version);
            MessageParameterizer parameterizer = Message.UPDATE.parameterizer()
                    .put("version", version);
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (!player.isOp()) return;
                parameterizer.send(player);
            });
        });
    }
}
