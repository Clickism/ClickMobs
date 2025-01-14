package me.clickism.clickmobs;

import me.clickism.clickmobs.config.Setting;
import me.clickism.clickmobs.message.Message;
import me.clickism.clickmobs.mob.PickupManager;
import me.clickism.clickmobs.nbt.NBTHelper;
import me.clickism.clickmobs.nbt.NBTHelperFactory;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ClickMobs extends JavaPlugin {
    public static ClickMobs INSTANCE;
    public static Logger LOGGER;

    // TODO: CONFIG OPTIONS: ALLOW-HOSTILE, ALLOW-BOSSES
    @Override
    public void onLoad() {
        INSTANCE = this;
        LOGGER = getLogger();
    }

    @Override
    public void onEnable() {
        // Load config/messages
        try {
            Setting.initialize(this);
            Message.initialize(this);
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Failed to load config/messages: ", exception);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        // Initialize NBT helper
        NBTHelper nbtHelper;
        try {
            nbtHelper = NBTHelperFactory.create();
        } catch (UnsupportedOperationException exception) {
            LOGGER.severe("This server version is not supported.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        new PickupManager(this, nbtHelper);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
