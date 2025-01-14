package me.clickism.clickmobs;

import me.clickism.clickmobs.config.Setting;
import me.clickism.clickmobs.listener.DispenserListener;
import me.clickism.clickmobs.listener.VehicleInteractListener;
import me.clickism.clickmobs.message.Message;
import me.clickism.clickmobs.mob.PickupManager;
import me.clickism.clickmobs.nbt.NBTHelper;
import me.clickism.clickmobs.nbt.NBTHelperFactory;
import me.clickism.clickmobs.util.MessageParameterizer;
import me.clickism.clickmobs.util.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ClickMobs extends JavaPlugin {

    public static final String RESOURCE_ID = "121939";

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

        PickupManager pickupManager = new PickupManager(this, nbtHelper);
        new DispenserListener(this, pickupManager);
        new VehicleInteractListener(this, pickupManager);

        checkUpdates();
    }

    private void checkUpdates() {
        if (Setting.CHECK_UPDATE.isDisabled()) return;
        LOGGER.info("Checking for updates...");
        new UpdateChecker(this, RESOURCE_ID).checkVersion(version -> {
            if (getDescription().getVersion().equals(version)) return;
            LOGGER.info("New version available: " + version);
            MessageParameterizer parameterizer = Message.UPDATE.parameterizer()
                    .put("version", version);
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (!player.isOp()) return;
                parameterizer.send(player);
            });
        });
    }
}
