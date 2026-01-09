/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs.mob;

import de.clickism.clickmobs.util.MessageType;
import de.clickism.clickmobs.util.VersionHelper;

import net.minecraft.server.commands.TagCommand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.ItemTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

//? if >=1.21.6 {
/*import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.util.ProblemReporter;
*///?}
//? if >=1.21.1 {
/*import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.CustomData;
*///?} else {
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
//?}
//? if >=1.21.4
//import net.minecraft.world.entity.EntitySpawnReason;


public class PickupHandler {

    private static final String TYPE_KEY = "EntityType";
    //? if <1.21.1
    private static final String DATA_KEY = "ClickMobsData";

    private static final String ALL_TAG = "?all";
    public static final Set<String> BLACKLISTED_MATERIALS_IN_HAND = new HashSet<>();

    public static boolean isBlacklistedItemInHand(Item item) {
        if (BLACKLISTED_MATERIALS_IN_HAND.contains(ALL_TAG) && item != Items.AIR) {
            return true;
        }

        String itemName = BuiltInRegistries.ITEM.getKey(item).toString();
        if (itemName.toLowerCase().matches(".*_harness$")) {
            // Harnesses cause problems with Happy Ghasts
            return true;
        }
        return BLACKLISTED_MATERIALS_IN_HAND.contains(item.toString().toLowerCase());
    }

    //? if >=1.21.6 {
    /*public static <T extends Entity> ItemStack toItemStack(T entity) {
        TagValueOutput view = TagValueOutput.createWithoutContext(new ProblemReporter.Collector());
        entity.saveWithoutId(view);
        String id = EntityType.getKey(entity.getType()).toString();
        view.putString(TYPE_KEY, id);
        ItemStack itemStack = getItemStack(getDisplayName(entity), getEntityName(entity), view.buildResult());
        MobTextures.setEntityTexture(itemStack, entity);
        entity.remove(Entity.RemovalReason.DISCARDED);
        return itemStack;
    }
    *///?} else {
    public static <T extends Entity> ItemStack toItemStack(T entity) {
        CompoundTag nbt = new CompoundTag();
        entity.save(nbt);
        String id = EntityType.getKey(entity.getType()).toString();
        nbt.putString(TYPE_KEY, id);
        ItemStack itemStack = getItemStack(getDisplayName(entity), getEntityName(entity), nbt);
        MobTextures.setEntityTexture(itemStack, entity);
        entity.remove(Entity.RemovalReason.DISCARDED);
        return itemStack;
    }
    //?}

    private static ItemStack getItemStack(Component name, String entityName, CompoundTag nbt) {
        ItemStack itemStack = Items.PLAYER_HEAD.getDefaultInstance();
        writeCustomData(itemStack, nbt);
        formatItem(itemStack, name.copy().withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW)),
                List.of(Component.literal("Right click to place the ")
                        .append(Component.literal(entityName))
                        .append(" back.")
                        .withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.DARK_GRAY)))
        );
        return itemStack;
    }

    //? if >=1.21.1 {
    /*private static void writeCustomData(ItemStack itemStack, CompoundTag nbt) {
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
    }
    
    @Nullable
    private static CompoundTag readCustomData(ItemStack itemStack) {
        CustomData nbt = itemStack.get(DataComponents.CUSTOM_DATA);
        if (nbt == null) return null;
        return nbt.copyTag();
    }
    
    private static void formatItem(ItemStack itemStack, Component name, List<Component> lore) {
        itemStack.set(DataComponents.ITEM_NAME, name);
        itemStack.set(DataComponents.CUSTOM_NAME, name);
        itemStack.set(DataComponents.LORE, new ItemLore(lore));
    }
    *///?} else {
    private static void writeCustomData(ItemStack itemStack, CompoundTag nbt) {
        itemStack.getOrCreateTag().put(DATA_KEY, nbt);
    }

    @Nullable
    private static CompoundTag readCustomData(ItemStack itemStack) {
        var nbt = itemStack.getTag(); // Don't create
        if (nbt == null) return null;
        return nbt.getCompound(DATA_KEY);
    }

    private static void formatItem(ItemStack itemStack, Component name, List<Component> lore) {
        var list = new ListTag();
        lore.forEach(text -> list.add(StringTag.valueOf(Component.Serializer.toJson(text))));
        var display = itemStack.getOrCreateTagElement("display");
        display.put("Lore", list);
        display.put("Name", StringTag.valueOf(Component.Serializer.toJson(name)));
    }
    //?}

    public static boolean isMob(ItemStack itemStack) {
        return readCustomData(itemStack) != null;
    }

    @Nullable
    public static Entity readEntityFromItemStack(Level world, ItemStack itemStack) {
        try {
            CompoundTag nbt = readCustomData(itemStack);
            if (nbt == null) return null;
            //? if >=1.21.5 {
            /*String id = nbt.getString(TYPE_KEY).orElse(null);
            *///?} else
            String id = nbt.getString(TYPE_KEY);
            if (id == null) return null;
            EntityType<?> type = EntityType.byString(id).orElse(null);
            if (type == null) return null;
            //? if >=1.21.4 {
            /*Entity entity = type.create(world, EntitySpawnReason.SPAWN_ITEM_USE);
             *///?} else
            Entity entity = type.create(world);
            if (entity == null) return null;
            //? if >=1.21.6 {
            /*ValueInput view = TagValueInput.create(new ProblemReporter.Collector(), world.registryAccess(), nbt);
            entity.load(view);
            *///?} else
            entity.load(nbt);
            return entity;
        } catch (Exception e) {
            return null;
        }
    }

    private static MutableComponent getDisplayName(Entity entity) {
        if (entity.hasCustomName()) {
            return Component.literal("\"").append(entity.getCustomName()).append("\"");
        }
        Component name = entity.getType().getDescription();
        if (entity instanceof Mob mob && mob.isBaby()) {
            return Component.literal("Baby ").append(name);
        }
        return name.copy();
    }

    private static String getEntityName(Entity entity) {
        return entity.getType().getDescription().getString().toLowerCase();
    }

    public static void notifyPickup(Player player, Entity entity) {
        String name = entity.getType().getDescription().getString().toLowerCase();
        MessageType.PICKUP_MESSAGE.sendActionbarSilently(player, Component.literal("You picked up a ")
                .append(Component.literal(name)));
        ServerLevel world = (ServerLevel) VersionHelper.getWorld(player);
        double x = entity.getX();
        double y = entity.getY() + .25f;
        double z = entity.getZ();
        world.sendParticles(ParticleTypes.SWEEP_ATTACK, x, y, z, 1, 0, 0, 0, 1);
        VersionHelper.playSound(player, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.NEUTRAL, 1, .5f);
    }
}
