package me.clickism.clickmobs.nbt;

import org.bukkit.entity.LivingEntity;

public interface NBTHelper {
    String write(LivingEntity entity) throws IllegalArgumentException;

    void read(LivingEntity entity, String nbtString) throws IllegalArgumentException;
}
