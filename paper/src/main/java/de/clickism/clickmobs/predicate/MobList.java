/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs.predicate;

import de.clickism.clickmobs.util.Utils;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MobList {
    private final List<MobPredicate> predicates = new ArrayList<>();
    private final Set<String> mobs = new HashSet<>();

    public boolean contains(LivingEntity entity) {
        String id = Utils.getKeyOfEntity(entity);
        if (mobs.contains(id)) {
            return true;
        }
        return predicates.stream()
                .anyMatch(predicate -> predicate.test(entity));
    }

    public void addMob(String identifier) {
        mobs.add(identifier);
    }

    public void addPredicate(MobPredicate predicate) {
        predicates.add(predicate);
    }

    public void addAll(MobList other) {
        mobs.addAll(other.mobs);
        predicates.addAll(other.predicates);
    }

    public void clear() {
        mobs.clear();
        predicates.clear();
    }
}
