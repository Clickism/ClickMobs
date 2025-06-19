/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs.predicate;

import de.clickism.clickmobs.util.Utils;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Tameable;

import java.util.List;

public interface MobPredicateType<T> {

    MobPredicateType<?> ALL = (entity, args) -> true;

    MobPredicateType<?> HOSTILE = (entity, args) -> entity instanceof Monster;

    MobPredicateType<?> BABY = (entity, args) -> entity instanceof Ageable ageable && !ageable.isAdult();

    MobPredicateType<?> TAMED = (entity, args) ->
            entity instanceof Tameable tameable && tameable.isTamed();

    MobPredicateType<String> NAMETAGGED = (entity, args) -> {
        String customName = entity.getCustomName();
        if (args.isEmpty()) {
            return customName != null;
        }
        return customName != null && customName.equals(args.get(0));
    };

    MobPredicateType<?> SILENT = (entity, args) -> entity.isSilent();

    /**
     * Checks if the entity is in the list of mobs.
     * Takes multiple arguments, will return true if any of
     * the arguments match the entity's identifier.
     */
    MobPredicateType<String> MOB = (entity, args) -> args.stream()
            .anyMatch(string -> string.equalsIgnoreCase(Utils.getKeyOfEntity(entity)));

    MobPredicateType<?> LEASHED = (entity, args) -> entity.isLeashed();

    boolean test(LivingEntity entity, List<T> args);

    default List<T> parseArgs(List<String> args) {
        return args.stream()
                .map(this::parseArg)
                .toList();
    }

    @SuppressWarnings("unchecked")
    default T parseArg(String arg) {
        return (T) arg;
    }
}
