/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs;

import de.clickism.clickmobs.mob.PickupHandler;
import de.clickism.configured.Config;
import de.clickism.configured.ConfigOption;

import java.util.List;

public interface ClickMobsConfig {
    Config CONFIG =
            Config.of("config/ClickMobs/config.yml")
                    .version(7)
                    .header("""
                            ---------------------------------------------------------
                            ClickMobs Config
                            NOTE: RELOAD/RESTART SERVER FOR CHANGES TO TAKE EFFECT
                            ---------------------------------------------------------
                            """);

    ConfigOption<Boolean> CHECK_UPDATE =
            CONFIG.option("check_update", true)
                    .description("""
                            Whether to check for updates on server startup. Strongly Recommended.
                            """);

    ConfigOption<List<String>> WHITELISTED_MOBS =
            CONFIG.option("whitelisted_mobs", List.of("cow", "pig", "sheep"))
                    .listOf(String.class)
                    .header("""
                            ---------------------------------------------------------
                            In the following section you can whitelist/blacklist mobs.
                            ---------------------------------------------------------
                            To whitelist/blacklist a vanilla mob, add its entity name.
                                i.E: "creeper" or "ender_dragon"
                            To whitelist/blacklist mobs from other mods, use the full identifier with the namespace.
                                i.E: "othermod:fancy_creeper"
                            ---------------------------------------------------------
                            You can also use tags (predicates) to whitelist/blacklist mobs with certain properties.
                            Available tags are:
                                ?all, ?hostile, ?baby, ?tamed, ?nametagged, ?silent, ?mob
                            
                            You can (optionally) pass arguments to certain tags:
                                - ?nametagged(Dinnerbone)
                            You can combine multiple tags:
                                - ?tamed ?nametagged
                            You can negate tags using "not":
                                - not ?hostile
                            You can use the ?mob tag alongside other tags to use tags on specific mobs.
                                - ?mob(creeper, zombie) ?nametagged(Friendly!)
                            
                            Check the wiki for more documentation on tags:
                            https://github.com/Clickism/ClickMobs/wiki/Tags
                            ---------------------------------------------------------
                            """)
                    .description("""
                            Mobs that are allowed to be picked up.
                            The whitelist takes precedence over the blacklist.
                            (Blacklisted mobs included in the whitelist will still be allowed)
                            """);

    ConfigOption<List<String>> BLACKLISTED_MOBS =
            CONFIG.option("blacklisted_mobs", List.of("?hostile", "wither", "ender_dragon"))
                    .listOf(String.class)
                    .description("""
                            Mobs that are not allowed to be picked up.
                            """);

    ConfigOption<Boolean> ENABLE_DISPENSERS =
            CONFIG.option("enable_dispensers", true)
                    .description("""
                            Whether dispensers can dispense picked up mobs.
                            """);

    ConfigOption<List<String>> BLACKLISTED_ITEMS_IN_HAND =
            CONFIG.option("blacklisted_items_in_hand", List.of("lead", "saddle"))
                    .listOf(String.class)
                    .description("""
                            Items that prevent picking up mobs when held in hand.
                            Use the (full) item identifiers of the items.
                            
                            Harnesses are always blocked for Happy Ghast compatibility
                            and do not need to be added here.
                            
                            Use the tag "?all" to block all items. This will make it so players
                            can only pick up mobs with an empty hand.
                            
                            For items from another mod, add the namespace before the item name.
                                i.E: "othermod:otheritem"
                            """)
                    .onChange(list -> {
                        PickupHandler.BLACKLISTED_MATERIALS_IN_HAND.clear();
                        list.forEach(item -> {
                            String name = item.toLowerCase();
                            if (!item.contains(":")) {
                                // Add minecraft namespace
                                name = "minecraft:" + name;
                            }
                            PickupHandler.BLACKLISTED_MATERIALS_IN_HAND.add(name);
                        });
                    });

}
