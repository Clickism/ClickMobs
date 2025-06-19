/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs.predicate;

import org.bukkit.entity.LivingEntity;

import java.util.List;

public abstract class MobPredicate {

    public static MobPredicate single(MobPredicateType<?> type, List<String> args) {
        return new SinglePredicate<>(type, args);
    }

    public static MobPredicate negate(MobPredicate predicate) {
        return new NegatedPredicate(predicate);
    }

    public static MobPredicate combine(List<MobPredicate> predicates) {
        return new CompoundPredicate(predicates);
    }

    public abstract boolean test(LivingEntity entity);

    public static class SinglePredicate<T> extends MobPredicate {
        private final MobPredicateType<T> type;
        private final List<T> args;

        public SinglePredicate(MobPredicateType<T> type, List<String> args) {
            this.type = type;
            this.args = type.parseArgs(args);
        }

        public boolean test(LivingEntity entity) {
            return type.test(entity, args);
        }
    }

    public static class NegatedPredicate extends MobPredicate {
        private final MobPredicate parent;

        public NegatedPredicate(MobPredicate predicate) {
            this.parent = predicate;
        }

        @Override
        public boolean test(LivingEntity entity) {
            return !parent.test(entity);
        }
    }

    public static class CompoundPredicate extends MobPredicate {
        private final List<MobPredicate> predicates;

        public CompoundPredicate(List<MobPredicate> predicates) {
            this.predicates = predicates;
        }

        @Override
        public boolean test(LivingEntity entity) {
            for (MobPredicate predicate : predicates) {
                if (!predicate.test(entity)) return false;
            }
            return true;
        }
    }
}
