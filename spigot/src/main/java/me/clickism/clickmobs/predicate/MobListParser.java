/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickmobs.predicate;

import me.clickism.clickmobs.ClickMobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MobListParser {
    private static final Map<String, MobPredicateType> PREDICATES = Map.ofEntries(
            Map.entry("all", MobPredicateType.ALL),
            Map.entry("hostile", MobPredicateType.HOSTILE),
            Map.entry("baby", MobPredicateType.BABY),
            Map.entry("tamed", MobPredicateType.TAMED),
            Map.entry("nametagged", MobPredicateType.NAMETAGGED),
            Map.entry("silent", MobPredicateType.SILENT),
            Map.entry("mob", MobPredicateType.MOB)
    );

    private static final Pattern PREDICATE_PATTERN = Pattern.compile(
            "(?<not>not\\s+)?\\?(?<predicate>[a-z_]*)(?:\\((?<args>.+?)\\))?"
    );

    private static boolean isPredicate(String string) {
        return string.contains("?");
    }

    public MobList parseMobList(List<String> lines) {
        MobList mobList = new MobList();
        for (String line : lines) {
            try {
                if (isPredicate(line)) {
                    mobList.addPredicate(parsePredicate(line));
                    continue;
                }
                mobList.addMob(line);
            } catch (Exception e) {
                ClickMobs.LOGGER.severe("Error parsing mob predicate/line: " + line + ". Error: " + e.getMessage());
            }
        }
        return mobList;
    }

    public MobPredicate parsePredicate(String string) {
        Matcher matcher = PREDICATE_PATTERN.matcher(string);
        List<MobPredicate> predicates = new ArrayList<>();
        while (matcher.find()) {
            boolean negated = matcher.group("not") != null;
            String predicateName = matcher.group("predicate");
            if (predicateName == null) throw new IllegalArgumentException("Tag name cannot be null");
            MobPredicateType<?> predicateType = PREDICATES.get(predicateName);
            if (predicateType == null) throw new IllegalArgumentException("Invalid tag: " + predicateName);
            String argsString = matcher.group("args");
            List<String> args = new ArrayList<>();
            if (argsString != null) {
                args = Arrays.stream(argsString.trim().split("\\s*,\\s*"))
                        .filter(s -> !s.isEmpty())
                        .toList();
            }
            MobPredicate predicate = MobPredicate.single(predicateType, args);
            if (negated) {
                predicate = MobPredicate.negate(predicate);
            }
            predicates.add(predicate);
        }
        if (predicates.isEmpty()) {
            throw new IllegalArgumentException("Invalid predicate(s): " + string);
        }
        if (predicates.size() == 1) {
            return predicates.get(0);
        }
        return MobPredicate.combine(predicates);
    }
}
