package me.clickism.clickmobs.config;

import me.clickism.clickmobs.ClickMobs;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.List;

public enum Setting {
    CONFIG_VERSION(0),
    LANGUAGE("en_US"),
    CHECK_UPDATE(true),

    WHITELISTED_MOBS(List.of()),
    BLACKLISTED_MOBS(List.of("WITHER", "ENDER_DRAGON")),
    ;

    private static SettingManager settingManager;

    private final Object defaultValue;
    private final String path;

    Setting(Object defaultValue) {
        this.defaultValue = defaultValue;
        this.path = name().toLowerCase().replace("_", "-");
    }

    public void set(Object object) {
        if (settingManager == null) {
            ClickMobs.LOGGER.warning("Couldn't save setting " + name() + " to config. SettingManager is null.");
            return;
        }
        settingManager.set(path, object);
    }

    private <T> T get(Class<T> type) {
        Object value = null;
        if (settingManager != null) {
            value = settingManager.get(path);
        }
        if (!type.isInstance(value)) {
            ClickMobs.LOGGER.warning("Invalid value for \"" + path + "\" in config.yml. Default value " +
                                          defaultValue + " is used instead.");
            value = defaultValue;
        }
        return type.cast(value);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getList() {
        return (List<T>) get(Object.class);
    }

    public int getInt() {
        return get(Integer.class);
    }

    public float getFloat() {
        return (float) getDouble();
    }

    public double getDouble() {
        return get(Double.class);
    }

    public String getString() {
        return get(String.class);
    }

    public boolean isEnabled() {
        return get(Boolean.class);
    }

    public boolean isDisabled() {
        return !isEnabled();
    }

    public static void initialize(JavaPlugin plugin) throws IOException {
        if (settingManager != null) return;
        settingManager = new SettingManager(plugin);
    }

    public static void saveSettings() {
        if (settingManager == null) {
            ClickMobs.LOGGER.warning("Couldn't save settings config. SettingManager is null.");
            return;
        }
        settingManager.getDataManager().saveConfig();
    }
}
