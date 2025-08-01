/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Method;

public class NMSSaver implements EntitySaver {
    private static final Class<?> CRAFT_ENTITY_CLASS;
    private static final Method GET_HANDLE_METHOD;

    static {
        try {
            CRAFT_ENTITY_CLASS = Class.forName(NMSUtils.cbClass("entity.CraftEntity"));
            GET_HANDLE_METHOD = CRAFT_ENTITY_CLASS.getMethod("getHandle");
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException("Failed to initialize NMSSaver", e);
        }
    }

    private static net.minecraft.world.entity.Entity getNmsEntity(Entity entity) throws Exception {
        Object nmsEntity = CRAFT_ENTITY_CLASS.cast(entity);
        return (net.minecraft.world.entity.Entity) GET_HANDLE_METHOD.invoke(nmsEntity);
    }

    @Override
    public String writeToString(Entity entity) throws IllegalArgumentException {
        try {
            net.minecraft.world.entity.Entity nmsEntity = getNmsEntity(entity);
            CompoundTag nbt = new CompoundTag();
            nmsEntity.save(nbt);
            return nbt.toString();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to write NBT: " + entity);
        }
    }

    @Override
    public Entity readAndSpawnAt(String string, EntityType type, Location location) throws IllegalArgumentException {
        try {
            World world = location.getWorld();
            if (world == null) {
                throw new IllegalArgumentException("World cannot be null");
            }
            Entity entity = world.spawnEntity(location, type);
            CompoundTag nbt = TagParser.parseTag(string);
            nbt.putUUID("UUID", entity.getUniqueId());
            net.minecraft.world.entity.Entity nmsEntity = getNmsEntity(entity);
            nmsEntity.load(nbt);
            entity.teleport(location);
            return entity;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to read NBT: " + string);
        }
    }
}
