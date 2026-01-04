/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.clickism.clickmobs.config.Permission;
import de.clickism.clickmobs.entity.EntitySaver;
import de.clickism.clickmobs.entity.SnapshotSaver;
import de.clickism.clickmobs.listener.DispenserListener;
import de.clickism.clickmobs.listener.JoinListener;
import de.clickism.clickmobs.listener.VehicleInteractListener;
import de.clickism.clickmobs.message.Message;
import de.clickism.clickmobs.mob.PickupManager;
import de.clickism.clickmobs.predicate.MobList;
import de.clickism.clickmobs.predicate.MobListParser;
import de.clickism.clickmobs.util.UpdateChecker;
import de.clickism.configured.papercommandadapter.PaperCommandAdapter;
import de.clickism.configured.papercommandadapter.command.GetCommand;
import de.clickism.configured.papercommandadapter.command.PathCommand;
import de.clickism.configured.papercommandadapter.command.ReloadCommand;
import de.clickism.configured.papercommandadapter.command.SetCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static de.clickism.clickmobs.ClickMobsConfig.*;

@SuppressWarnings("UnstableApiUsage")
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
        EntitySaver entitySaver = new SnapshotSaver();
        PickupManager pickupManager = new PickupManager(this, entitySaver,
                whitelistedMobs, blacklistedMobs, blacklistedItemsInHand);
        new DispenserListener(this, pickupManager);
        new VehicleInteractListener(this, pickupManager);

        // Register Commands
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("clickmobs");
            root.then(PaperCommandAdapter.ofConfig(CONFIG)
                    .requires(sender -> Permission.CONFIG.has(sender.getSender()))
                    .add(new SetCommand(Message.CONFIG_SET::send))
                    .add(new GetCommand(Message.CONFIG_GET::send))
                    .add(new ReloadCommand(Message.CONFIG_RELOAD::send))
                    .add(new PathCommand(Message.CONFIG_PATH::send))
                    .buildRoot());
            commands.registrar().register(root.build());
        });
        // Check for updates
        if (CHECK_UPDATE.get()) {
            checkUpdates();
            new JoinListener(this, () -> newerVersion);
        }
        // Setup Metrics
        setupBStats();
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

    private void setupBStats() {
        int pluginId = 27923;
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new SimplePie("language", () ->
                String.valueOf(LANGUAGE.get())));
        metrics.addCustomChart(new SingleLineChart("blacklist_tag_entries", () ->
                (int) BLACKLISTED_MOBS.get()
                        .stream()
                        .filter(entry -> entry.contains("?"))
                        .count()
        ));
        metrics.addCustomChart(new SingleLineChart("blacklist_all_entries", () ->
                BLACKLISTED_MOBS.get().size()));
        metrics.addCustomChart(new SingleLineChart("whitelist_tag_entries", () ->
                (int) WHITELISTED_MOBS.get()
                        .stream()
                        .filter(entry -> entry.contains("?"))
                        .count()
        ));
        metrics.addCustomChart(new SingleLineChart("whitelist_all_entries", () ->
                WHITELISTED_MOBS.get().size()));
    }
}
