/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs;

import de.clickism.clickmobs.callback.MobUseBlockCallback;
import de.clickism.clickmobs.callback.MobUseEntityCallback;
import de.clickism.clickmobs.callback.UpdateNotifier;
import de.clickism.clickmobs.callback.VehicleUseEntityCallback;
import de.clickism.clickmobs.predicate.MobList;
import de.clickism.clickmobs.predicate.MobListParser;
import de.clickism.clickmobs.util.UpdateChecker;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.MinecraftVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.clickism.clickmobs.ClickMobsConfig.*;

public class ClickMobs implements ModInitializer {
	public static final String MOD_ID = "clickmobs";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static String newerVersion = null;

	@Override
	public void onInitialize() {
		CONFIG.load();
		MobListParser parser = new MobListParser();
		MobList whitelistedMobs = parser.parseMobList(CONFIG.get(WHITELISTED_MOBS));
		MobList blacklistedMobs = parser.parseMobList(CONFIG.get(BLACKLISTED_MOBS));
		UseEntityCallback.EVENT.register(new MobUseEntityCallback(whitelistedMobs, blacklistedMobs));
		UseEntityCallback.EVENT.register(new VehicleUseEntityCallback());
		UseBlockCallback.EVENT.register(new MobUseBlockCallback());
		if (CONFIG.get(CHECK_UPDATE)) {
			checkUpdates();
			ServerPlayConnectionEvents.JOIN.register(new UpdateNotifier(() -> newerVersion));
		}
	}

	private void checkUpdates() {
		String modVersion = FabricLoader.getInstance().getModContainer(MOD_ID)
				.map(container -> container.getMetadata().getVersion().getFriendlyString())
				.orElse(null);
		String minecraftVersion = MinecraftVersion.CURRENT.getName();
		new UpdateChecker(MOD_ID, "fabric", minecraftVersion).checkVersion(version -> {
			if (modVersion == null || UpdateChecker.getRawVersion(modVersion).equals(version)) {
				return;
			}
			newerVersion = version;
			LOGGER.info("Newer version available: {}", version);
		});
	}

	public static boolean isClickVillagersPresent() {
		return FabricLoader.getInstance().isModLoaded("clickvillagers");
	}
}