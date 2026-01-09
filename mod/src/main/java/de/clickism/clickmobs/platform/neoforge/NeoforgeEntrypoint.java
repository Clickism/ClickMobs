/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs.platform.neoforge;
//? if neoforge {
/*import de.clickism.clickmobs.ClickMobs;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@Mod(ClickMobs.MOD_ID)
public class NeoforgeEntrypoint {
    public NeoforgeEntrypoint(IEventBus eventBus) {
        NeoForge.EVENT_BUS.register(new NeoforgeEventListener());

        ClickMobs.initialize();
    }
}
*///?}
