package me.clickism.clickmobs.config;

import com.google.gson.JsonElement;
import me.clickism.clickmobs.ClickMobs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public enum Settings {
    CONFIG_VERSION(0),

    ALLOW_HOSTILE(false),
    ONLY_ALLOW_WHITELISTED(false),

    WHITELISTED_MOBS(List.of()),
    BLACKLISTED_MOBS(List.of("wither", "ender_dragon")),
    ;

    private final Object defaultValue;
    private final String path;

    Settings(Object defaultValue) {
        this.defaultValue = defaultValue;
        this.path = name().toLowerCase().replace("_", "-");
    }

    public String getPath() {
        return path;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    @SuppressWarnings("unchecked")
    private <T> T get(Function<JsonElement, T> getter) {
        T value = null;
        try {
            value = getter.apply(Config.INSTANCE.get(path));
        } catch (Exception ignored) {
            ClickMobs.LOGGER.warn("Invalid value for \"{}\" in config.yml. Default value {} is used instead.", path, defaultValue);
            value = (T) defaultValue;
        }
        return value;
    }

    public List<String> getStringList() {
        return get(json -> {
            List<String> list = new ArrayList<>();
            for (JsonElement element : json.getAsJsonArray()) {
                list.add(element.getAsString());
            }
            return list;
        });
    }

    public int getInt() {
        return get(JsonElement::getAsInt);
    }

    public float getFloat() {
        return get(JsonElement::getAsFloat);
    }

    public double getDouble() {
        return get(JsonElement::getAsDouble);
    }

    public String getString() {
        return get(JsonElement::getAsString);
    }

    public boolean isEnabled() {
        return get(JsonElement::getAsBoolean);
    }

    public boolean isDisabled() {
        return !isEnabled();
    }
}
