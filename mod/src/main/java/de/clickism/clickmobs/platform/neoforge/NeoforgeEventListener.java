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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

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
}
*///?}