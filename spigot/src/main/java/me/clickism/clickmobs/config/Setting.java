/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickmobs.config;

import me.clickism.clickmobs.ClickMobs;

import java.io.IOException;
import java.util.List;

public enum Setting {
    CONFIG_VERSION(0),
    LANGUAGE("en_US"),
    CHECK_UPDATE(true),

    PER_MOB_PERMISSIONS(false),

    ALLOW_HOSTILE(false),
    ONLY_ALLOW_WHITELISTED(false),

    WHITELISTED_MOBS(List.of()),
    BLACKLISTED_MOBS(List.of("wither", "ender_dragon")),

    CUSTOM_MODEL_DATA(0)
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

    public static void initialize() throws IOException {
        if (settingManager != null) return;
        settingManager = new SettingManager(ClickMobs.INSTANCE);
    }

    public static void saveSettings() {
        if (settingManager == null) {
            ClickMobs.LOGGER.warning("Couldn't save settings config. SettingManager is null.");
            return;
        }
        settingManager.getDataManager().saveConfig();
    }

    public static void reloadSettings() throws IOException {
        settingManager = null;
        initialize();
    }
}
