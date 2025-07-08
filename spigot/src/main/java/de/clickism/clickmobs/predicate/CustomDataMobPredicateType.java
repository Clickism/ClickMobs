/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs.predicate;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class CustomDataMobPredicateType implements MobPredicateType<CustomDataMobPredicateType.MatchingContext> {
    @Override
    public boolean test(LivingEntity entity, List<CustomDataMobPredicateType.MatchingContext> args) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        return args.stream().anyMatch(context -> {
            NamespacedKey key = context.key;
            if (context.parsingContext.isEmpty() || context.value.isEmpty()) {
                return pdc.getKeys().contains(key);
            }
            ParsingContext<?, ?> parsingContext = context.parsingContext.orElseThrow();
            Object value = context.value.orElseThrow();
            Object storedValue = pdc.get(key, parsingContext.dataType);
            return Objects.equals(value, storedValue);
        });
    }

    @Override
    public CustomDataMobPredicateType.MatchingContext parseArg(String arg) {
        String[] parts = arg.split("=", 2);
        NamespacedKey key = NamespacedKey.fromString(parts[0]);
        if (key == null) throw new IllegalArgumentException("Invalid NamespacedKey: " + parts[0]);
        if (parts.length == 1) {
            return new MatchingContext(key, Optional.empty(), Optional.empty());
        }
        String[] valueParts = parts[1].split("\\[", 2);
        if (valueParts.length != 2 || !valueParts[1].endsWith("]")) {
            throw new IllegalArgumentException("Invalid PersistentDataType and/or value: " + arg);
        }
        String dataTypeString = valueParts[0];
        String valueString = valueParts[1].substring(0, valueParts[1].length() - 1); // Remove the closing parenthesis
        ParsingContext<?, ?> parsingContext = getParsingContextFromString(dataTypeString);
        return new MatchingContext(
                key,
                Optional.of(parsingContext),
                Optional.of(parsingContext.parser.apply(valueString))
        );
    }

    private static ParsingContext<?, ?> getParsingContextFromString(String arg) {
        return switch (arg.toLowerCase()) {
            case "string" -> new ParsingContext<>(PersistentDataType.STRING, Function.identity());
            case "integer", "int" -> new ParsingContext<>(PersistentDataType.INTEGER, Integer::parseInt);
            case "long" -> new ParsingContext<>(PersistentDataType.LONG, Long::parseLong);
            case "double" -> new ParsingContext<>(PersistentDataType.DOUBLE, Double::parseDouble);
            case "boolean", "bool" -> new ParsingContext<>(PersistentDataType.BOOLEAN, Boolean::parseBoolean);
            case "byte" -> new ParsingContext<>(PersistentDataType.BYTE, Byte::parseByte);
            case "float" -> new ParsingContext<>(PersistentDataType.FLOAT, Float::parseFloat);
            case "short" -> new ParsingContext<>(PersistentDataType.SHORT, Short::parseShort);
            default -> throw new IllegalArgumentException("Unsupported data type: " + arg);
        };
    }

    public record ParsingContext<T, Z>(
            PersistentDataType<T, Z> dataType,
            Function<String, Z> parser
    ) {
    }

    public record MatchingContext(
            NamespacedKey key,
            Optional<ParsingContext<?, ?>> parsingContext,
            Optional<Object> value
    ) {

    }
}
