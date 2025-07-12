/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs.mob;

import de.clickism.clickmobs.ClickMobs;
import de.clickism.clickmobs.config.Permission;
import de.clickism.clickmobs.entity.EntitySaver;
import de.clickism.clickmobs.message.Message;
import de.clickism.clickmobs.predicate.MobList;
import de.clickism.clickmobs.util.Utils;
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static de.clickism.clickmobs.ClickMobsConfig.*;

public class PickupManager implements Listener {
    public static final NamespacedKey ENTITY_KEY = new NamespacedKey(ClickMobs.INSTANCE, "entity");
    public static final NamespacedKey TYPE_KEY = new NamespacedKey(ClickMobs.INSTANCE, "type");
    public static final NamespacedKey NBT_KEY = new NamespacedKey(ClickMobs.INSTANCE, "nbt");

    private final EntitySaver entitySaver;

    private final MobList whitelistedMobs;
    private final MobList blacklistedMobs;

    public PickupManager(JavaPlugin plugin, EntitySaver entitySaver,
                         MobList whitelistedMobs, MobList blacklistedMobs) {
        this.entitySaver = entitySaver;
        this.whitelistedMobs = whitelistedMobs;
        this.blacklistedMobs = blacklistedMobs;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private static String formatEntity(Entity entity) {
        String name = entity.getType().name().toLowerCase().replace("_", " ");
        if ("de_DE".equals(Message.LOCALIZATION.language())) {
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        return name;
    }

    private static String getName(LivingEntity entity) {
        if (entity.getCustomName() != null) {
            return "\"" + entity.getCustomName() + "\"";
        }
        String entityName = formatEntity(entity);
        String name = Utils.capitalize(entityName);
        if (entity instanceof Ageable ageable && !ageable.isAdult()) {
            return Message.BABY_MOB.localized(name);
        }
        return name;
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
        Message.PICK_UP.sendActionbarSilently(player, formatEntity(entity));
        sendPickupEffect(entity);
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
        ItemResult itemResult = getHeldMobItem(inventory);
        if (itemResult == null) return;
        event.setCancelled(true);
        if (Permission.PLACE.lacksAndNotify(player)) return;
        Block block = event.getBlockPlaced();
        Location location = block.getLocation().add(.5, 0, .5);
        float yaw = player.getLocation().getYaw();
        location.setYaw((yaw + 360) % 360 - 180); // Face the entity towards the player
        try {
            ItemStack item = itemResult.item();
            spawnFromItemStack(item, location);
            itemResult.decrementAmount(inventory);
            World world = player.getWorld();
            world.playSound(location, Sound.ENTITY_PLAYER_ATTACK_WEAK, 1, .5f);
            Block blockBelow = block.getRelative(BlockFace.DOWN);
            world.spawnParticle(Particle.BLOCK_CRACK, location, 30, blockBelow.getBlockData());
        } catch (IllegalArgumentException exception) {
            Message.READ_ERROR.send(player);
            ClickMobs.LOGGER.severe("Failed to read mob data: " + exception.getMessage());
        }
    }

    private ItemResult getHeldMobItem(PlayerInventory inventory) {
        ItemStack item = inventory.getItemInMainHand();
        if (isMob(item)) {
            return new ItemResult(item, EquipmentSlot.HAND);
        }
        item = inventory.getItemInOffHand();
        if (isMob(item)) {
            return new ItemResult(item, EquipmentSlot.OFF_HAND);
        }
        return null;
    }

    public boolean isMob(ItemStack item) {
        if (item.getType() != Material.PLAYER_HEAD) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        PersistentDataContainer data = meta.getPersistentDataContainer();
        return data.has(ENTITY_KEY, PersistentDataType.BOOLEAN);
    }

    public ItemStack toItemStack(LivingEntity entity) {
        entity = getRootEntity(entity);
        Set<LivingEntity> passengers = getAllPassengers(entity);
        ItemStack item = createItem(entity, passengers);
        writeData(entity, item);
        entity.remove();
        passengers.forEach(Entity::remove);
        return item;
    }

    private Set<LivingEntity> getAllPassengers(LivingEntity entity) {
        Set<LivingEntity> passengers = new HashSet<>();
        entity.getPassengers().forEach(passenger -> {
            if (!(passenger instanceof LivingEntity livingEntity)) return;
            passengers.add(livingEntity);
            passengers.addAll(getAllPassengers(livingEntity));
        });
        return passengers;
    }

    private LivingEntity getRootEntity(LivingEntity entity) {
        if (entity.getVehicle() instanceof LivingEntity vehicle) {
            return getRootEntity(vehicle);
        }
        return entity;
    }

    private void writeData(LivingEntity entity, ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(ENTITY_KEY, PersistentDataType.BOOLEAN, true);
        data.set(TYPE_KEY, PersistentDataType.STRING, entity.getType().name());
        String nbt = entitySaver.writeToString(entity);
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
        String nbt = data.get(NBT_KEY, PersistentDataType.STRING);
        LivingEntity entity = (LivingEntity) entitySaver.readAndSpawnAt(nbt, type, location);
        entity.teleport(location);
        return entity;
    }

    private ItemStack createItem(LivingEntity entity, Set<LivingEntity> passengers) {
        String entityName = formatEntity(entity);
        String name = getName(entity);
        if (!passengers.isEmpty()) {
            name = name + " §7(+ " + passengers.stream()
                    .map(PickupManager::getName)
                    .collect(Collectors.joining(" + ")) + ")";
        }
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) throw new IllegalArgumentException("ItemMeta is null");
        meta.setDisplayName(ChatColor.YELLOW + name);
        meta.setLore(Arrays.stream(Message.MOB$LORE.localized(entityName).split("\n"))
                .toList());
        int modelDataOverride = CONFIG.get(CUSTOM_MODEL_DATA).getOrDefault(Utils.getKeyOfEntity(entity), 0);
        if (modelDataOverride != 0) {
            meta.setCustomModelData(modelDataOverride);
        }
        item.setItemMeta(meta);
        MobTextures.setEntityTexture(item, entity);
        return item;
    }

    private boolean canPickUp(Player player, LivingEntity entity) {
        String name = Utils.getKeyOfEntity(entity);
        if (whitelistedMobs.contains(entity)) {
            return true;
        }
        boolean blacklisted = blacklistedMobs.contains(entity);
        if (CONFIG.get(PER_MOB_PERMISSIONS)) {
            return Permission.hasPickupPermissionFor(player, name) && !blacklisted;
        }
        return !blacklisted;
    }

    private record ItemResult(ItemStack item, EquipmentSlot slot) {
        void decrementAmount(PlayerInventory inventory) {
            ItemStack item = this.item;
            item.setAmount(item.getAmount() - 1);
            inventory.setItem(slot, item);
        }
    }
}
