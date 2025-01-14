package me.clickism.clickmobs;

import me.clickism.clickmobs.callback.MobUseBlockCallback;
import me.clickism.clickmobs.callback.MobUseEntityCallback;
import me.clickism.clickmobs.callback.VehicleUseEntityCallback;
import me.clickism.clickmobs.config.Config;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ClickMobs implements ModInitializer {
	public static final String MOD_ID = "clickmobs";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		UseEntityCallback.EVENT.register(new MobUseEntityCallback());
		UseEntityCallback.EVENT.register(new VehicleUseEntityCallback());
		UseBlockCallback.EVENT.register(new MobUseBlockCallback());
		try {
			new Config("ClickMobs.json");
		} catch (IOException e) {
			LOGGER.error("Failed to load config file", e);
		}
	}
}