/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs.predicate;

//? if =1.20.1 {
/*import net.minecraft.entity.mob.MobEntity;
*///?} else {
import net.minecraft.entity.Leashable;
//?}
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.text.Text;

import java.util.List;

public interface MobPredicateType<T> {

    MobPredicateType<?> ALL = (entity, args) -> true;

    MobPredicateType<?> HOSTILE = (entity, args) -> entity instanceof Monster;

    MobPredicateType<?> BABY = (entity, args) -> entity.isBaby();

    MobPredicateType<?> TAMED = (entity, args) ->
            entity instanceof TameableEntity tameable && tameable.isTamed();

    MobPredicateType<String> NAMETAGGED = (entity, args) -> {
        if (args.isEmpty()) {
            return entity.hasCustomName();
        }
        Text customName = entity.getCustomName();
        return customName != null && customName.getString().equals(args.get(0));
    };

    MobPredicateType<?> SILENT = (entity, args) -> entity.isSilent();

    /**
     * Checks if the entity is in the list of mobs.
     * Takes multiple arguments, will return true if any of
     * the arguments match the entity's identifier.
     */
    MobPredicateType<String> MOB = new MobPredicateType<>() {
        @Override
        public boolean test(LivingEntity entity, List<String> args) {
            return args.stream()
                    .anyMatch(string -> string.equals(MobList.getIdentifierOfEntity(entity)));
        }

        @Override
        public String parseArg(String arg) {
            return MobList.parseIdentifier(arg).toString();
        }
    };

    MobPredicateType<?> LEASHED = (entity, args) ->
            //? if =1.20.1 {
            /*entity instanceof MobEntity mob && mob.isLeashed();
            *///?} else
            entity instanceof Leashable leashable && leashable.isLeashed();

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
