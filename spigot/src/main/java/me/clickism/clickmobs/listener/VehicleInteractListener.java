package me.clickism.clickmobs.listener;

import me.clickism.clickmobs.ClickMobs;
import me.clickism.clickmobs.message.Message;
import me.clickism.clickmobs.mob.PickupManager;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class VehicleInteractListener implements Listener {

    private final PickupManager pickupManager;

    @AutoRegistered
    public VehicleInteractListener(JavaPlugin plugin, PickupManager pickupManager) {
        this.pickupManager = pickupManager;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    private void onVehicleInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Entity entity = event.getRightClicked();
        if (!(entity instanceof Minecart) && !(entity instanceof Boat)) return;
        Player player = event.getPlayer();
        if (!player.isSneaking()) return;
        PlayerInventory inventory = player.getInventory();
        ItemStack item = inventory.getItemInMainHand();
        if (!pickupManager.isMob(item)) return;
        event.setCancelled(true);
        if (!hasSpace(entity)) return;
        try {
            LivingEntity villager = pickupManager.spawnFromItemStack(item, entity.getLocation());
            item.setAmount(item.getAmount() - 1);
            inventory.setItemInMainHand(item);
            World world = player.getWorld();
            if (entity instanceof Minecart) {
                world.playSound(player, Sound.BLOCK_METAL_BREAK, 1, .5f);
            } else {
                world.playSound(player, Sound.BLOCK_WOOD_BREAK, 1, .5f);
            }
            entity.addPassenger(villager);
        } catch (IllegalArgumentException exception) {
            Message.READ_ERROR.send(player);
            ClickMobs.LOGGER.severe("Failed to read mob data: " + exception.getMessage());
        }
    }

    private boolean hasSpace(Entity entity) {
        List<Entity> passengers = entity.getPassengers();
        if (entity instanceof RideableMinecart || entity instanceof ChestBoat) {
            return passengers.isEmpty();
        }
        if (entity instanceof Boat) {
            return passengers.size() < 2;
        }
        return false;
    }
}
