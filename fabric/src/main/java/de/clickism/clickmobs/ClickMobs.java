/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs;

import de.clickism.clickmobs.callback.MobUseBlockCallback;
import de.clickism.clickmobs.callback.MobUseEntityCallback;
import de.clickism.clickmobs.callback.UpdateNotifier;
import de.clickism.clickmobs.callback.VehicleUseEntityCallback;
import de.clickism.clickmobs.predicate.MobList;
import de.clickism.clickmobs.predicate.MobListParser;
import de.clickism.clickmobs.util.MessageType;
import de.clickism.clickmobs.util.UpdateChecker;
import de.clickism.clickmobs.util.VersionHelper;
import de.clickism.configured.fabriccommandadapter.FabricCommandAdapter;
import de.clickism.configured.fabriccommandadapter.command.GetCommand;
import de.clickism.configured.fabriccommandadapter.command.PathCommand;
import de.clickism.configured.fabriccommandadapter.command.ReloadCommand;
import de.clickism.configured.fabriccommandadapter.command.SetCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.MinecraftVersion;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.clickism.clickmobs.ClickMobsConfig.*;

public class ClickMobs implements ModInitializer {
    public static final String MOD_ID = "clickmobs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static String newerVersion = null;

    @Override
    public void onInitialize() {
        UseEntityCallback.EVENT.register(new MobUseEntityCallback());
        UseEntityCallback.EVENT.register(new VehicleUseEntityCallback());
        UseBlockCallback.EVENT.register(new MobUseBlockCallback());
        // Load config after events to ensure listeners are registered
        CONFIG.load();
        // Register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("clickmobs")
                    .requires(VersionHelper::isOp)
                    .then(FabricCommandAdapter.ofConfig(CONFIG)
                            .add(new SetCommand((sender, key, value) -> {
                                MessageType.CONFIG.send(sender, Text.literal("§aConfig option \"§l" + key + "§a\" set to §l" + value + "."));
                            }))
                            .add(new GetCommand((sender, key, value) -> {
                                MessageType.CONFIG.send(sender, Text.literal("§aConfig option \"§l" + key + "§a\" has value §l" + value + "."));
                            }))
                            .add(new ReloadCommand(sender -> {
                                MessageType.CONFIG.send(sender, Text.literal("§aReloaded the config file."));
                            }))
                            .add(new PathCommand((sender, path) -> {
                                MessageType.CONFIG.send(sender, Text.literal("§aThe config file is located at: §f" + path));
                            }))
                            .buildRoot()
                    )
            );
        });
        // Check for updates
        if (CHECK_UPDATE.get()) {
            checkUpdates();
            ServerPlayConnectionEvents.JOIN.register(new UpdateNotifier(() -> newerVersion));
        }
    }

    private void checkUpdates() {
        String modVersion = FabricLoader.getInstance().getModContainer(MOD_ID)
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse(null);
        //? if >=1.21.9 {
        String minecraftVersion = MinecraftVersion.create().name();
        //?} elif >= 1.21.6 {
        /*String minecraftVersion = MinecraftVersion.CURRENT.name();
         *///?} else
        /*String minecraftVersion = MinecraftVersion.CURRENT.getName();*/
        new UpdateChecker(MOD_ID, "fabric", minecraftVersion).checkVersion(version -> {
            if (modVersion == null || UpdateChecker.getRawVersion(modVersion).equals(version)) {
                return;
            }
            newerVersion = version;
            LOGGER.info("Newer version available: {}", version);
        });
    }

    public static boolean isClickVillagersPresent() {
        return FabricLoader.getInstance().isModLoaded("clickvillagers");
    }
}