/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs.platform.neoforge;
//? if neoforge {

/*import de.clickism.clickmobs.event.PickupMobListener;
import de.clickism.clickmobs.event.PlaceMobInVehicleListener;
import de.clickism.clickmobs.event.PlaceMobListener;
import de.clickism.clickmobs.event.UpdateNotifier;
import de.clickism.clickmobs.util.MessageType;
import de.clickism.clickmobs.util.VersionHelper;
import de.clickism.configured.neoforgecommandadapter.NeoforgeCommandAdapter;
import de.clickism.configured.neoforgecommandadapter.command.GetCommand;
import de.clickism.configured.neoforgecommandadapter.command.PathCommand;
import de.clickism.configured.neoforgecommandadapter.command.ReloadCommand;
import de.clickism.configured.neoforgecommandadapter.command.SetCommand;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import static de.clickism.clickmobs.ClickMobsConfig.CONFIG;

public class NeoforgeEventListener {
    private final PickupMobListener pickupMobListener = new PickupMobListener();
    private final PlaceMobListener placeMobListener = new PlaceMobListener();
    private final PlaceMobInVehicleListener placeMobInVehicleListener = new PlaceMobInVehicleListener();

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        placeMobListener.event(event.getEntity(), event.getLevel(), event.getHand(), event.getHitVec());
    }

    @SubscribeEvent
    public void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        pickupMobListener.event(event.getEntity(), event.getLevel(), event.getHand(), event.getTarget());
        placeMobInVehicleListener.event(event.getEntity(), event.getLevel(), event.getHand(), event.getTarget());
    }

    public record JoinListener(UpdateNotifier notifier) {
        @SubscribeEvent
        public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player)) return;
            notifier.onJoin(player);
        }
    }

    public record ConfigCommandRegisterListener() {
        @SubscribeEvent
        public void onRegisterCommands(RegisterCommandsEvent event) {
            event.getDispatcher().register(Commands.literal("clickmobs")
                    .requires(VersionHelper::isOpOrInSinglePlayer)
                    .then(NeoforgeCommandAdapter.ofConfig(CONFIG)
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
                            .buildRoot())
            );
        }
    }
}
*///?}