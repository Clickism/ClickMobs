/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs;

import de.clickism.clickmobs.config.ReloadCommand;
import de.clickism.clickmobs.entity.EntitySaver;
import de.clickism.clickmobs.entity.EntitySaverFactory;
import de.clickism.clickmobs.listener.DispenserListener;
import de.clickism.clickmobs.listener.JoinListener;
import de.clickism.clickmobs.listener.VehicleInteractListener;
import de.clickism.clickmobs.message.Message;
import de.clickism.clickmobs.mob.PickupManager;
import de.clickism.clickmobs.predicate.MobList;
import de.clickism.clickmobs.predicate.MobListParser;
import de.clickism.clickmobs.util.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static de.clickism.clickmobs.ClickMobsConfig.*;

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
        MobListParser parser = new MobListParser();
        MobList whitelistedMobs = new MobList();
        MobList blacklistedMobs = new MobList();
        WHITELISTED_MOBS.onLoad(lines -> {
            whitelistedMobs.clear();
            whitelistedMobs.addAll(parser.parseMobList(lines));
        });
        BLACKLISTED_MOBS.onLoad(lines -> {
            blacklistedMobs.clear();
            blacklistedMobs.addAll(parser.parseMobList(lines));
        });
        Set<String> blacklistedItemsInHand = new HashSet<>();
        BLACKLISTED_ITEMS_IN_HAND.onLoad(lines -> {
            blacklistedItemsInHand.clear();
            blacklistedItemsInHand.addAll(lines);
        });
        CONFIG.load();
        EntitySaver entitySaver = EntitySaverFactory.create();
        PickupManager pickupManager = new PickupManager(this, entitySaver,
                whitelistedMobs, blacklistedMobs, blacklistedItemsInHand);
        new DispenserListener(this, pickupManager);
        new VehicleInteractListener(this, pickupManager);
        // Register commands
        PluginCommand command = Bukkit.getPluginCommand("clickmobs");
        if (command != null) {
            command.setExecutor(new ReloadCommand());
        }
        // Check for updates
        if (CONFIG.get(CHECK_UPDATE)) {
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
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (!player.isOp()) return;
                Message.UPDATE.send(player, newerVersion);
            });
        });
    }
}
