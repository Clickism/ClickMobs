/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs.platform.neoforge;
//? if neoforge {

/*import de.clickism.clickmobs.ClickMobs;
import de.clickism.clickmobs.event.UpdateNotifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

import static de.clickism.clickmobs.ClickMobsConfig.CHECK_UPDATE;

@Mod(ClickMobs.MOD_ID)
public class NeoforgeEntrypoint {
    public NeoforgeEntrypoint(IEventBus eventBus) {
        NeoForge.EVENT_BUS.register(new NeoforgeEventListener());

        ClickMobs.initialize();

        // Register commands
        NeoForge.EVENT_BUS.register(new NeoforgeEventListener.ConfigCommandRegisterListener());

        // Check for updates
        if (CHECK_UPDATE.get()) {
            String modVersion = ModList.get()
                    .getModContainerById(ClickMobs.MOD_ID)
                    .map(container -> container.getModInfo().getVersion().toString())
                    .orElse(null);
            ClickMobs.checkUpdates(modVersion, "neoforge");
            var notifier = new UpdateNotifier(ClickMobs::newerVersion);
            NeoForge.EVENT_BUS.register(new NeoforgeEventListener.JoinListener(notifier));
        }
    }
}
*///?}
