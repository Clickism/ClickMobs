/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs.predicate;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MobList {
    private final List<MobPredicate> predicates = new ArrayList<>();
    private final Set<String> mobs = new HashSet<>();

    public static Identifier parseIdentifier(String string) {
        String[] parts = string.split(":");
        if (parts.length != 2) {
            return Identifier.tryParse("minecraft:" + string);
        }
        return Identifier.tryParse(string);
    }

    public static String getIdentifierOfEntity(LivingEntity entity) {
        return BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(entity.getType()).unwrapKey()
                .map((key) -> key
                        //? if >=1.21.11 {
                        .identifier()
                        //?} else
                        //.location()
                        .toString())
                .orElse("[unregistered]");
    }

    public boolean contains(LivingEntity entity) {
        String id = getIdentifierOfEntity(entity);
        if (mobs.contains(id)) {
            return true;
        }
        return predicates.stream()
                .anyMatch(predicate -> predicate.test(entity));
    }

    public void addMob(Identifier identifier) {
        mobs.add(identifier.toString());
    }

    public void addPredicate(MobPredicate predicate) {
        predicates.add(predicate);
    }
}
