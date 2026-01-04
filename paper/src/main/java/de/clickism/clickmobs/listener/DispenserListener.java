/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs.listener;

import de.clickism.clickmobs.ClickMobs;
import de.clickism.clickmobs.mob.PickupManager;
import io.papermc.paper.event.block.BlockPreDispenseEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import static de.clickism.clickmobs.ClickMobsConfig.ENABLE_DISPENSERS;

public class DispenserListener implements Listener {

    private final PickupManager pickupManager;

    @AutoRegistered
    public DispenserListener(JavaPlugin plugin, PickupManager pickupManager) {
        this.pickupManager = pickupManager;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void onPreDispense(BlockPreDispenseEvent event) {
        if (!ENABLE_DISPENSERS.get()) return;
        ItemStack item = event.getItemStack();
        if (!pickupManager.isMob(item)) return;
        Block block = event.getBlock();
        BlockState state = block.getState();
        if (!(state instanceof Dispenser dispenser)) return;
        // Dispensing villager
        event.setCancelled(true);
        Directional directional = (Directional) state.getBlockData();
        Location location = block.getRelative(directional.getFacing()).getLocation().add(.5, 0, .5);
        try {
            pickupManager.spawnFromItemStack(item, location);
            item.setAmount(item.getAmount() - 1);
            if (item.getAmount() <= 0) {
                item = new ItemStack(Material.AIR);
            }
            dispenser.getSnapshotInventory().setItem(event.getSlot(), item);
            dispenser.update(true, false);
        } catch (IllegalArgumentException exception) {
            ClickMobs.LOGGER.severe("Failed to spawn mob from NBT data: " + exception.getMessage());
        }
    }
}
