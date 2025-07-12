/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs;

import de.clickism.clickmobs.message.Message;
import de.clickism.configured.Config;
import de.clickism.configured.ConfigOption;

import java.util.List;
import java.util.Map;

public class ClickMobsConfig {
    public static final Config CONFIG =
            Config.of("plugins/ClickMobs/config.yml")
                    .version(6)
                    .header("""
                            ---------------------------------------------------------
                            ClickMobs Config
                            NOTE: RELOAD/RESTART SERVER FOR CHANGES TO TAKE EFFECT
                            ---------------------------------------------------------
                            """);

    public static final ConfigOption<String> LANGUAGE =
            CONFIG.optionOf("language", "en_US")
                    .description("""
                            Language of the plugin.
                            Currently supported languages: en_US, de_DE
                            """)
                    .onLoad(lang -> Message.LOCALIZATION
                            .language(lang)
                            .load());

    public static final ConfigOption<Boolean> CHECK_UPDATE =
            CONFIG.optionOf("check_update", true)
                    .description("""
                            Whether to check for updates on server startup. Strongly Recommended.
                            """)
                    .appendDefaultValue();

    public static final ConfigOption<Boolean> PER_MOB_PERMISSIONS =
            CONFIG.optionOf("per_mob_permissions", false)
                    .description("""
                            Whether to have specific permissions for each mob.
                            For example, players won't be able to pick up a creeper unless they have
                            BOTH "clickmobs.pickup" and "clickmobs.pickup.creeper" permissions.
                            - Whitelisted mobs will still be able to be picked up without a mob-specific permission.
                            - Blacklisted mobs will not be able to be picked up even with a mob-specific permission.
                            """)
                    .appendDefaultValue();

    public static final ConfigOption<List<String>> WHITELISTED_MOBS =
            CONFIG.optionOf("whitelisted_mobs", List.of("cow", "pig", "sheep"), String.class)
                    .header("""
                            ---------------------------------------------------------
                            In the following section you can whitelist/blacklist mobs.
                            ---------------------------------------------------------
                            To whitelist/blacklist a vanilla mob, add its entity name.
                                i.E: "creeper" or "ender_dragon"
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
                            You can use the ?mob tag alongside other tags to use tags on specific mobs:
                                - ?mob(creeper, zombie) ?nametagged(Friendly!)
                            You can use the ?customdata tag to target mobs added by other plugins or mobs with
                            custom data. Please read the wiki for more information on this tag.
                            
                            Check the wiki for more documentation on tags:
                            https://clickism.de/docs/clickmobs/tags
                            ---------------------------------------------------------
                            """)
                    .description("""
                            Mobs that are allowed to be picked up.
                            The whitelist takes precedence over the blacklist.
                            (Blacklisted mobs included in the whitelist will still be allowed)
                            """)
                    .appendDefaultValue();

    public static final ConfigOption<List<String>> BLACKLISTED_MOBS =
            CONFIG.optionOf("blacklisted_mobs", List.of("?hostile", "wither", "ender_dragon"), String.class)
                    .description("""
                            Mobs that are not allowed to be picked up.
                            """)
                    .appendDefaultValue();

    public static final ConfigOption<Map<String, Integer>> CUSTOM_MODEL_DATA =
            CONFIG.optionOf("custom_model_data",
                            Map.of("creeper", 0, "skeleton", 0),
                            String.class, Integer.class)
                    .description("""
                            Set a custom model data for the picked up mobs.
                            This is useful for resource packs that want to change the model/texture of picked up mobs.
                            Value 0 will not change the model.
                            """);
}
