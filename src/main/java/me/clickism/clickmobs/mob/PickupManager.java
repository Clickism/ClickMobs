package me.clickism.clickmobs.mob;

import me.clickism.clickmobs.ClickMobs;
import me.clickism.clickmobs.config.Permission;
import me.clickism.clickmobs.message.MessageType;
import me.clickism.clickmobs.nbt.NBTHelper;
import me.clickism.clickmobs.util.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class PickupManager implements Listener {
    public static final NamespacedKey ENTITY_KEY = new NamespacedKey(ClickMobs.INSTANCE, "entity");
    public static final NamespacedKey TYPE_KEY = new NamespacedKey(ClickMobs.INSTANCE, "type");
    public static final NamespacedKey NBT_KEY = new NamespacedKey(ClickMobs.INSTANCE, "nbt");

    private final NBTHelper nbtHelper;

    public PickupManager(JavaPlugin plugin, NBTHelper nbtHelper) {
        this.nbtHelper = nbtHelper;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler(ignoreCancelled = true)
    private void onInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof LivingEntity entity)) return;
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR) return;
        if (!player.isSneaking()) return;
        event.setCancelled(true);
        if (Permission.PICKUP.lacksAndNotify(player)) return;
        ItemStack item;
        try {
            item = toItemStack(entity);
        } catch (IllegalArgumentException exception) {
            MessageType.FAIL.send(player, "Failed to write mob data.");
            ClickMobs.LOGGER.severe("Failed to write mob data: " + exception.getMessage());
            return;
        }
        Utils.setHandOrGive(player, item);
        MessageType.PICK_UP.sendActionbarSilently(player, "You picked up a " + formatEntity(entity));
        sendPickupEffect(entity);
    }
    
    private static String formatEntity(Entity entity) {
        return entity.getType().name().toLowerCase().replace("_", " ");
    }

    public void sendPickupEffect(LivingEntity entity) {
        Location location = entity.getLocation().add(0, .25, 0);
        World world = entity.getWorld();
        world.spawnParticle(Particle.SWEEP_ATTACK, location, 1);
        world.playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, .5f);
    }
    
    @EventHandler(ignoreCancelled = true)
    private void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        ItemStack item = inventory.getItemInMainHand();
        if (item.getType() != Material.PLAYER_HEAD) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        if (!isEntity(item)) return;

        event.setCancelled(true);
        if (Permission.PLACE.lacksAndNotify(player)) return;
        Block block = event.getBlockPlaced();
        Location location = block.getLocation().add(.5, 0, .5);
        float yaw = player.getLocation().getYaw();
        location.setYaw((yaw + 360) % 360 - 180); // Face the entity towards the player
        try {
            spawnFromItemStack(item, location);
            item.setAmount(item.getAmount() - 1);
            inventory.setItemInMainHand(item);
            World world = player.getWorld();
            world.playSound(location, Sound.ENTITY_PLAYER_ATTACK_WEAK, 1, .5f);
            Block blockBelow = block.getRelative(BlockFace.DOWN);
            world.spawnParticle(Particle.BLOCK_CRACK, location, 30, blockBelow.getBlockData());
        } catch (IllegalArgumentException exception) {
            MessageType.FAIL.send(player, "Failed to read mob data.");
            ClickMobs.LOGGER.severe("Failed to read villager data: " + exception.getMessage());
        }
    }
    
    public boolean isEntity(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        PersistentDataContainer data = meta.getPersistentDataContainer();
        return data.has(ENTITY_KEY, PersistentDataType.BOOLEAN);
    }
    
    public ItemStack toItemStack(LivingEntity entity) {
        ItemStack item = createItem(entity);
        writeData(entity, item);
        entity.remove();
        return item;
    }

    private void writeData(LivingEntity entity, ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(ENTITY_KEY, PersistentDataType.BOOLEAN, true);
        data.set(TYPE_KEY, PersistentDataType.STRING, entity.getType().name());
        String nbt = nbtHelper.write(entity);
        data.set(NBT_KEY, PersistentDataType.STRING, nbt);
        item.setItemMeta(meta);
    }
    
    public LivingEntity spawnFromItemStack(ItemStack item, Location location) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) throw new IllegalArgumentException("ItemMeta is null");
        PersistentDataContainer data = meta.getPersistentDataContainer();
        if (!data.has(ENTITY_KEY, PersistentDataType.BOOLEAN))
            throw new IllegalArgumentException("Item is not a villager");
        if (!data.has(TYPE_KEY, PersistentDataType.STRING))
            throw new IllegalArgumentException("Entity type is missing");
        EntityType type = EntityType.valueOf(data.get(TYPE_KEY, PersistentDataType.STRING));
        World world = location.getWorld();
        if (world == null) throw new IllegalArgumentException("World is null");
        LivingEntity entity = (LivingEntity) world.spawnEntity(location, type);
        String nbt = data.get(NBT_KEY, PersistentDataType.STRING);
        nbtHelper.read(entity, nbt);
        entity.teleport(location);
        return entity;
    }

    private ItemStack createItem(LivingEntity entity) {
        String name = ChatColor.YELLOW + Utils.capitalize(formatEntity(entity));
        if (entity instanceof Ageable ageable && !ageable.isAdult()) {
            name = "Baby " + name;
        }
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) throw new IllegalArgumentException("ItemMeta is null");
        meta.setDisplayName(name);
        meta.setLore(List.of(
                ChatColor.DARK_GRAY + "Right click to place the mob back."
        ));
        item.setItemMeta(meta);
        MobTextures.setEntityTexture(item, entity);
        return item;
    }
}
