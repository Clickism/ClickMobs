/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickmobs.mob;

import me.clickism.clickmobs.ClickMobs;
import me.clickism.clickmobs.config.Permission;
import me.clickism.clickmobs.config.Setting;
import me.clickism.clickmobs.message.Message;
import me.clickism.clickmobs.nbt.NBTHelper;
import me.clickism.clickmobs.util.Parameterizer;
import me.clickism.clickmobs.util.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    private void onInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!(event.getRightClicked() instanceof LivingEntity entity)) return;
        if (entity instanceof HumanEntity) return;
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR) return;
        if (!player.isSneaking()) return;
        event.setCancelled(true);
        if (Permission.PICKUP.lacksAndNotify(player)) return;
        if (!canPickUp(player, entity)) {
            Message.BLACKLISTED_MOB.sendActionbar(player);
            return;
        }
        ItemStack item;
        try {
            item = toItemStack(entity);
        } catch (IllegalArgumentException exception) {
            Message.WRITE_ERROR.send(player);
            ClickMobs.LOGGER.severe("Failed to write mob data: " + exception.getMessage());
            return;
        }
        Utils.setHandOrGive(player, item);
        Message.PICK_UP.parameterizer()
                .put("mob", formatEntity(entity))
                .sendActionbarSilently(player);
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    private void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        ItemStack item = inventory.getItemInMainHand();
        if (item.getType() != Material.PLAYER_HEAD) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        if (!isMob(item)) return;
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
            Message.READ_ERROR.send(player);
            ClickMobs.LOGGER.severe("Failed to read mob data: " + exception.getMessage());
        }
    }

    public boolean isMob(ItemStack item) {
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
            throw new IllegalArgumentException("Item is not a mob");
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
        String entityName = formatEntity(entity);
        String name = getName(entity);
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) throw new IllegalArgumentException("ItemMeta is null");
        meta.setDisplayName(ChatColor.YELLOW + name);
        meta.setLore(Message.MOB.getParameterizedLore(Parameterizer.empty().put("mob", entityName)));
        int modelDataOverride = Setting.CUSTOM_MODEL_DATA.getInt();
        if (modelDataOverride != 0) {
            meta.setCustomModelData(modelDataOverride);
        }
        item.setItemMeta(meta);
        MobTextures.setEntityTexture(item, entity);
        return item;
    }

    private static String getName(LivingEntity entity) {
        if (entity.getCustomName() != null) {
            return "\"" + entity.getCustomName() + "\"";
        }
        String entityName = formatEntity(entity);
        String name = Utils.capitalize(entityName);
        if (entity instanceof Ageable ageable && !ageable.isAdult()) {
            return Message.BABY_MOB.parameterizer()
                    .put("mob", name)
                    .toString();
        }
        return name;
    }

    private static boolean canPickUp(Player player, Entity entity) {
        String name = entity.getType().name();
        boolean isWhitelisted = Setting.WHITELISTED_MOBS.getList().contains(name);
        if (isWhitelisted) {
            return true;
        }
        if (Setting.PER_MOB_PERMISSIONS.isEnabled()) {
            return Permission.hasPickupPermissionFor(player, name) && !Setting.BLACKLISTED_MOBS.getList().contains(name);
        }
        if (Setting.ONLY_ALLOW_WHITELISTED.isEnabled()) {
            return false;
        }
        if (Setting.ALLOW_HOSTILE.isDisabled() && entity instanceof Monster) {
            return false;
        }
        return !Setting.BLACKLISTED_MOBS.getList().contains(name);
    }
}
