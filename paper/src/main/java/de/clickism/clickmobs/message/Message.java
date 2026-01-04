/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs.message;

import de.clickism.clickmobs.ClickMobs;
import de.clickism.configured.localization.Localization;
import de.clickism.configured.localization.LocalizationKey;
import de.clickism.configured.localization.Parameters;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Message implements LocalizationKey {

    @Parameters("version")
    UPDATE(MessageType.WARN),
    NO_PERMISSION(MessageType.FAIL),
    @Parameters("mob")
    PICK_UP(MessageType.PICK_UP),
    BLACKLISTED_MOB(MessageType.FAIL),

    WRITE_ERROR(MessageType.FAIL),
    READ_ERROR(MessageType.FAIL),

    @Parameters("mob")
    BABY_MOB,
    @Parameters("mob")
    MOB$LORE,

    @Parameters("usage")
    USAGE(MessageType.FAIL),
    RELOAD_SUCCESS(MessageType.CONFIRM),
    RELOAD_FAIL(MessageType.FAIL),

    @Parameters({"option", "value"})
    CONFIG_SET(MessageType.CONFIG),
    @Parameters({"option", "value"})
    CONFIG_GET(MessageType.CONFIG),
    @Parameters("path")
    CONFIG_PATH(MessageType.CONFIG),
    CONFIG_RELOAD(MessageType.CONFIG);

    public static final Localization LOCALIZATION =
            Localization.of(lang -> "plugins/ClickMobs/lang/" + lang + ".json")
                    .resourceProvider(ClickMobs.class, lang -> "/lang/" + lang + ".json")
                    .fallbackLanguage("en_US")
                    .version(4);

    private static final MessageType MISSING = MessageType.silent("&2[?] &c", "&8< &2? &f%s &8>");

    private final MessageType type;

    Message() {
        this(null);
    }

    Message(MessageType type) {
        this.type = type;
    }

    public static String localize(String key, Object... params) {
        return LOCALIZATION.get(LocalizationKey.of(key), params);
    }

    public String localized(Object... params) {
        return LOCALIZATION.get(this, params);
    }

    public void send(CommandSender sender, Object... params) {
        getTypeOrDefault().sendSilently(sender, localized(params));
    }

    public void sendSilently(CommandSender sender, Object... params) {
        getTypeOrDefault().sendSilently(sender, localized(params));
    }

    public void sendActionbar(CommandSender sender, Object... params) {
        getTypeOrDefault().sendActionbar(sender, localized(params));
    }

    public void sendActionbarSilently(CommandSender sender, Object... params) {
        getTypeOrDefault().sendActionbarSilently(sender, localized(params));
    }

    @Override
    public String toString() {
        return localized();
    }

    public List<String> getLore(Object... params) {
        String pathToLore = name().toLowerCase() + ".lore";
        String text = localize(pathToLore, params);
        return Arrays.stream(text.split("\n"))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public MessageType getTypeOrDefault() {
        return type != null ? type : MISSING;
    }
}
