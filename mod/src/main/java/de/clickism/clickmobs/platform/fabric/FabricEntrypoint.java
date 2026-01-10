/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs.platform.fabric;
//? if fabric {

import de.clickism.clickmobs.ClickMobs;
import de.clickism.clickmobs.event.PickupMobListener;
import de.clickism.clickmobs.event.PlaceMobInVehicleListener;
import de.clickism.clickmobs.event.PlaceMobListener;
import de.clickism.clickmobs.event.UpdateNotifier;
import de.clickism.clickmobs.util.MessageType;
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
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import static de.clickism.clickmobs.ClickMobsConfig.CHECK_UPDATE;
import static de.clickism.clickmobs.ClickMobsConfig.CONFIG;

public class FabricEntrypoint implements ModInitializer {
    @Override
    public void onInitialize() {
        var pickupMobListener = new PickupMobListener();
        var placeMobInVehicleListener = new PlaceMobInVehicleListener();
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) ->
                pickupMobListener.event(player, level, hand, entity));
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) ->
                placeMobInVehicleListener.event(player, level, hand, entity));
        UseBlockCallback.EVENT.register(new PlaceMobListener()::event);

        ClickMobs.initialize();

        // Register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("clickmobs")
                    .requires(VersionHelper::isOpOrInSinglePlayer)
                    .then(FabricCommandAdapter.ofConfig(CONFIG)
                            .add(new SetCommand((sender, key, value) -> {
                                MessageType.CONFIG.send(sender, Component.literal("§aConfig option \"§l" + key + "§a\" set to §l" + value + "."));
                            }))
                            .add(new GetCommand((sender, key, value) -> {
                                MessageType.CONFIG.send(sender, Component.literal("§aConfig option \"§l" + key + "§a\" has value §l" + value + "."));
                            }))
                            .add(new ReloadCommand(sender -> {
                                MessageType.CONFIG.send(sender, Component.literal("§aReloaded the config file."));
                            }))
                            .add(new PathCommand((sender, path) -> {
                                MessageType.CONFIG.send(sender, Component.literal("§aThe config file is located at: §f" + path));
                            }))
                            .buildRoot()
                    )
            );
        });

        // Check for updates
        if (CHECK_UPDATE.get()) {
            String modVersion = FabricLoader.getInstance().getModContainer(ClickMobs.MOD_ID)
                    .map(container -> container.getMetadata().getVersion().getFriendlyString())
                    .orElse(null);
            ClickMobs.checkUpdates(modVersion);
            var notifier = new UpdateNotifier(ClickMobs::newerVersion);
            ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
                notifier.onJoin(handler.player);
            });
        }
    }
}
//?}
