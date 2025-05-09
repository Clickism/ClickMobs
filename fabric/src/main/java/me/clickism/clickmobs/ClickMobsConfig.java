/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickmobs;

import me.clickism.configured.Config;
import me.clickism.configured.ConfigOption;

import java.util.List;

public class ClickMobsConfig {
    public static final Config CONFIG =
            Config.ofYaml("config/ClickMobs.yml")
                    .version(1)
                    .header("""
                            ---------------------------------------------------------
                            ClickMobs Config
                            NOTE: RELOAD/RESTART SERVER FOR CHANGES TO TAKE EFFECT
                            ---------------------------------------------------------
                            """);

    public static final ConfigOption<Boolean> CHECK_UPDATE =
            CONFIG.optionOf("check_update", true)
                    .description("""
                            Whether to check for updates on server startup. Strongly Recommended.
                            """)
                    .appendDefaultValue();

    public static final ConfigOption<List<String>> WHITELISTED_MOBS =
            CONFIG.optionOf("whitelisted_mobs", List.<String>of())
                    .header("""
                            ---------------------------------------------------------
                            In the following section you can whitelist/blacklist mobs.
                            ---------------------------------------------------------
                            To blacklist a vanilla mob, add its entity name.
                                i.E: "creeper" or "ender_dragon"
                            To blacklist mobs from other mods, use the full identifier with the namespace.
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

                            Check the wiki for more documentation on tags and predicates.
                            ---------------------------------------------------------
                            """)
                    .description("""
                            Mobs that are allowed to be picked up.
                            The whitelist takes precedence over the blacklist.
                            (Blacklisted mobs included in the whitelist will still be allowed)
                            """)
                    .appendDefaultValue();

    public static final ConfigOption<List<String>> BLACKLISTED_MOBS =
            CONFIG.optionOf("blacklisted_mobs", List.of("?hostile", "wither", "ender_dragon"))
                    .description("""
                            Mobs that are not allowed to be picked up.
                            """)
                    .appendDefaultValue();
}
