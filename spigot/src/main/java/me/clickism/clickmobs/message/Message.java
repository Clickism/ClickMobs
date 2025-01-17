/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickmobs.message;

import me.clickism.clickmobs.config.Setting;
import me.clickism.clickmobs.util.MessageParameterizer;
import me.clickism.clickmobs.util.Parameterizer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public enum Message {

    @WithParameters("version")
    UPDATE(MessageType.WARN),
    NO_PERMISSION(MessageType.FAIL),
    @WithParameters("mob")
    PICK_UP(MessageType.PICK_UP),
    BLACKLISTED_MOB(MessageType.FAIL),

    WRITE_ERROR(MessageType.FAIL),
    READ_ERROR(MessageType.FAIL),

    @WithParameters("mob")
    BABY_MOB,
    MOB;

    private static final MessageType MISSING = MessageType.silent("&2[?] &c", "&8< &2? &c%s &8>");

    @Nullable
    private static MessageManager messageManager;

    private final String path;
    private final MessageType type;

    Message() {
        this(null);
    }

    Message(MessageType type) {
        this.type = type;
        this.path = name().toLowerCase();
    }

    public void send(CommandSender player) {
        getTypeOrDefault().send(player, toString());
    }

    public void sendSilently(CommandSender player) {
        getTypeOrDefault().sendSilently(player, toString());
    }

    public void sendActionbar(CommandSender player) {
        getTypeOrDefault().sendActionbar(player, toString());
    }

    public void sendActionbarSilently(CommandSender player) {
        getTypeOrDefault().sendActionbarSilently(player, toString());
    }

    @Override
    public String toString() {
        return get(path);
    }

    public List<String> getLore() {
        if (messageManager == null) return List.of(path);
        return messageManager.getLore(path);
    }

    public List<String> getParameterizedLore(Parameterizer parameterizer) {
        return getLore().stream()
                .map(parameterizer::replace)
                .collect(Collectors.toList());
    }

    public MessageType getTypeOrDefault() {
        return type != null ? type : MISSING;
    }

    public MessageParameterizer parameterizer() {
        return new MessageParameterizer(this);
    }

    public static void initialize(JavaPlugin plugin) throws IOException {
        if (messageManager != null) return;
        Setting.initialize(plugin);
        messageManager = new MessageManager(plugin, Setting.LANGUAGE.getString());
    }

    @NotNull
    public static String get(String key) {
        if (messageManager == null) return key;
        return messageManager.getOrPath(key);
    }
}
