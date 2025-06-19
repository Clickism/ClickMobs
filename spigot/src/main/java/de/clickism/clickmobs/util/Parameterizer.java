/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs.util;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Parameterizer {

    protected static final String UPPERCASE_SELECTOR = "^";
    protected static final String FORMAT = "{%s}";
    protected final Map<String, Object> params = new HashMap<>();
    private final String string;
    protected boolean colorize = true;

    protected Parameterizer(String string) {
        this.string = string;
    }

    public static Parameterizer empty() {
        return new Parameterizer("");
    }

    public static Parameterizer of(String string) {
        return new Parameterizer(string);
    }

    public Parameterizer put(String key, @NotNull Object value) {
        this.params.put(key, value);
        return this;
    }

    public Parameterizer putAll(Parameterizer parameterizer) {
        this.params.putAll(parameterizer.params);
        return this;
    }

    public Parameterizer disableColorizeParameters() {
        this.colorize = false;
        return this;
    }

    public String replace(String string) {
        String result = Utils.colorize(string);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String value = entry.getValue().toString();
            String uppercasedKey = String.format(UPPERCASE_SELECTOR + FORMAT, entry.getKey());
            result = result.replace(uppercasedKey, Utils.capitalize(value));
            String key = String.format(FORMAT, entry.getKey());
            result = result.replace(key, value);
        }
        if (colorize) result = Utils.colorize(result);
        return result;
    }

    @Override
    public String toString() {
        return replace(this.string);
    }
}
