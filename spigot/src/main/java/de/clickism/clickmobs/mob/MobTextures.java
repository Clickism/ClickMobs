/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs.mob;

import de.clickism.clickmobs.ClickMobs;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

public class MobTextures {

    public static final String DEFAULT_TEXTURE = "http://textures.minecraft.net/texture/ee7700096b5a2a87386d6205b4ddcc14fd33cf269362fa6893499431ce77bf9";

    public static final Map<Object, String> TEXTURE_MAP = Map.<Object, String>ofEntries(
            Map.entry("ELDER_GUARDIAN", "http://textures.minecraft.net/texture/30f868caf19cf2124f0fef98e6b8773d27fbf42d93aab06b22ee033b2aee6447"),
            Map.entry("WITHER_SKELETON", "http://textures.minecraft.net/texture/1e4d204ebc242eca2148f5853e3af00f84f0d674099dc394f6d2924b240ca2e3"),
            Map.entry("STRAY", "http://textures.minecraft.net/texture/9e391c6e535f7aa5a2b6ee6d137f59f2d7c60def88853ba611ceb2d16a7e7c73"),
            Map.entry("HUSK", "http://textures.minecraft.net/texture/c096164f81950a5cc0e33e87999f98cde792517f4d7f99a647a9aedab23ae58"),
            Map.entry("ZOMBIE_VILLAGER", "http://textures.minecraft.net/texture/c45c11e0327035649ca0600ef938900e25fd1e38017422bc9740e4cda2cba892"),
            Map.entry("SKELETON_HORSE", "http://textures.minecraft.net/texture/ac7d8a16d3f0f98b598df93f2c2d34e75171cd52dbf4a1211d7b84c019416a40"),
            Map.entry("ZOMBIE_HORSE", "http://textures.minecraft.net/texture/d22950f2d3efddb18de86f8f55ac518dce73f12a6e0f8636d551d8eb480ceec"),
            Map.entry("DONKEY", "http://textures.minecraft.net/texture/dfb6c3c052cf787d236a2915f8072b77c547497715d1d2f8cbc9d241d88a"),
            Map.entry("MULE", "http://textures.minecraft.net/texture/a0486a742e7dda0bae61ce2f55fa13527f1c3b334c57c034bb4cf132fb5f5f"),
            Map.entry("EVOKER", "http://textures.minecraft.net/texture/630ce775edb65db8c2741bdfae84f3c0d0285aba93afadc74900d55dfd9504a5"),
            Map.entry("VEX", "http://textures.minecraft.net/texture/b663134d7306bb604175d2575d686714b04412fe501143611fcf3cc19bd70abe"),
            Map.entry("VINDICATOR", "http://textures.minecraft.net/texture/6deaec344ab095b48cead7527f7dee61b063ff791f76a8fa76642c8676e2173"),
            Map.entry("ILLUSIONER", "http://textures.minecraft.net/texture/4639d325f4494258a473a93a3b47f34a0c51b3fceaf59fee87205a5e7ff31f68"),
            Map.entry("CREEPER", "http://textures.minecraft.net/texture/163bf3137b37ff15dca924f681dd78a495d8a30a86140fec66d4fadde577e43b"),
            Map.entry("SKELETON", "http://textures.minecraft.net/texture/482b78da6ee713d5acfe5fcb0754ee56900831a5098313064108de6e7e406839"),
            Map.entry("SPIDER", "http://textures.minecraft.net/texture/35e248da2e108f09813a6b848a0fcef111300978180eda41d3d1a7a8e4dba3c3"),
            Map.entry("GIANT", "http://textures.minecraft.net/texture/a51a18b2d437eaf674e75bc48a601c42f0da49060eede2182a0bbcc5cbf7f42f"),
            Map.entry("ZOMBIE", "http://textures.minecraft.net/texture/64528b3229660f3dfab42414f59ee8fd01e80081dd3df30869536ba9b414e089"),
            Map.entry("SLIME", "http://textures.minecraft.net/texture/c7d29dbf3d98213ec2fb0ca25da74779e57bd0c1234268f828a3ec9869e15a9c"),
            Map.entry("GHAST", "http://textures.minecraft.net/texture/7a8b714d32d7f6cf8b37e221b758b9c599ff76667c7cd45bbc49c5ef19858646"),
            Map.entry("ZOMBIFIED_PIGLIN", "http://textures.minecraft.net/texture/e935842af769380f78e8b8a88d1ea6ca2807c1e5693c2cf797456620833e936f"),
            Map.entry("ENDERMAN", "http://textures.minecraft.net/texture/9689c200980e4c54adcfbbdad492c1d2edbd92366aabf89724ed19930cb5b6e2"),
            Map.entry("CAVE_SPIDER", "http://textures.minecraft.net/texture/eccc4a32d45d74e8b14ef1ffd55cd5f381a06d4999081d52eaea12e13293e209"),
            Map.entry("SILVERFISH", "http://textures.minecraft.net/texture/92ec2c3cb95ab77f7a60fb4d160bced4b879329b62663d7a9860642e588ab210"),
            Map.entry("BLAZE", "http://textures.minecraft.net/texture/b78ef2e4cf2c41a2d14bfde9caff10219f5b1bf5b35a49eb51c6467882cb5f0"),
            Map.entry("MAGMA_CUBE", "http://textures.minecraft.net/texture/a1c97a06efde04d00287bf20416404ab2103e10f08623087e1b0c1264a1c0f0c"),
            Map.entry("ENDER_DRAGON", "http://textures.minecraft.net/texture/74ecc040785e54663e855ef0486da72154d69bb4b7424b7381ccf95b095a"),
            Map.entry("WITHER", "http://textures.minecraft.net/texture/74f328f5044129b5d1f96affd1b8c05bcde6bd8e756aff5c5020585eef8a3daf"),
            Map.entry("BAT", "http://textures.minecraft.net/texture/6de75a2cc1c950e82f62abe20d42754379dfad6f5ff546e58f1c09061862bb92"),
            Map.entry("WITCH", "http://textures.minecraft.net/texture/8aa986a6e1c2d88ff198ab2c3259e8d2674cb83a6d206f883bad2c8ada819"),
            Map.entry("ENDERMITE", "http://textures.minecraft.net/texture/5bc7b9d36fb92b6bf292be73d32c6c5b0ecc25b44323a541fae1f1e67e393a3e"),
            Map.entry("GUARDIAN", "http://textures.minecraft.net/texture/b8e725779c234c590cce854db5c10485ed8d8a33fa9b2bdc3424b68bb1380bed"),
            Map.entry("SHULKER", "http://textures.minecraft.net/texture/b5abf9151760c9395fae4f3d2517bdf57837815d254537870596270cb7ff196"),
            Map.entry("PIG", "http://textures.minecraft.net/texture/bee8514892f3d78a32e8456fcbb8c6081e21b246d82f398bd969fec19d3c27b3"),
            Map.entry("SHEEP", "http://textures.minecraft.net/texture/84e5cdb0edb362cb454586d1fd0ebe971423f015b0b1bfc95f8d5af8afe7e810"),
            Map.entry("COW", "http://textures.minecraft.net/texture/b667c0e107be79d7679bfe89bbc57c6bf198ecb529a3295fcfdfd2f24408dca3"),
            Map.entry("CHICKEN", "http://textures.minecraft.net/texture/3ad3dd0083faa69a062f9ad81418f5a596180bf1592e4b8d1303b230b64bc79e"),
            Map.entry("SQUID", "http://textures.minecraft.net/texture/938a42a80c70b243643ee5015e9d5149af54ade83ff55c2179f8a8b3b10805f6"),
            Map.entry("WOLF", "http://textures.minecraft.net/texture/8f0b221786f193c06dd19a7875a903635113f84523927bb69764237fe20703de"),
            Map.entry("MOOSHROOM", "http://textures.minecraft.net/texture/42a805209fa3f99983f42b99b92b99ebb89da4bcdaf6628645f11257d16ddabb"),
            Map.entry("SNOW_GOLEM", "http://textures.minecraft.net/texture/e6f20aec528c3968dd8164f9d9336b081b3a2c7ecf189cf73df6f925e5a4ed14"),
            Map.entry("OCELOT", "http://textures.minecraft.net/texture/15852d265d960da3e06b8b3a91632253f4a107febfb7069c9e46e5f73a4de9b4"),
            Map.entry("IRON_GOLEM", "http://textures.minecraft.net/texture/4271913a3fc8f56bdf6b90a4b4ed6a05c562ce0076b5344d444fb2b040ae57d"),
            Map.entry("HORSE", "http://textures.minecraft.net/texture/628d1ab4be1e28b7b461fdea46381ac363a7e5c3591c9e5d2683fbe1ec9fcd3"),
            Map.entry("RABBIT", "http://textures.minecraft.net/texture/c1db38ef3c1a1d59f779a0cd9f9e616de0cc9acc7734b8facc36fc4ea40d0235"),
            Map.entry("POLAR_BEAR", "http://textures.minecraft.net/texture/3d3cd8548e7dceb5c2394d1b00da2c61ffc0dde46229b10509eb27a0dcb23bfb"),
            Map.entry("LLAMA", "http://textures.minecraft.net/texture/bae25ddc2d2539c565dff2aa5006033f14cc06379fe28b0731c7bdc65ba0e016"),
            Map.entry("PARROT", "http://textures.minecraft.net/texture/2b94f236c4a642eb2bcdc3589b9c3c4a0b5bd5df9cd5d68f37f8c83f8e3f1"),
            Map.entry("VILLAGER", "http://textures.minecraft.net/texture/40cc0a12577898e7167c43cf9fe8cb289563921b70946012a60893cab76e549"),
            Map.entry("TURTLE", "http://textures.minecraft.net/texture/0a4050e7aacc4539202658fdc339dd182d7e322f9fbcc4d5f99b5718a"),
            Map.entry("PHANTOM", "http://textures.minecraft.net/texture/7e95153ec23284b283f00d19d29756f244313a061b70ac03b97d236ee57bd982"),
            Map.entry("COD", "http://textures.minecraft.net/texture/7892d7dd6aadf35f86da27fb63da4edda211df96d2829f691462a4fb1cab0"),
            Map.entry("SALMON", "http://textures.minecraft.net/texture/d4d001589b86c22cf24f1618fe7efef12932aa9148b5e4fc6ff4a614b990ae12"),
            Map.entry("PUFFERFISH", "http://textures.minecraft.net/texture/17152876bc3a96dd2a2299245edb3beef647c8a56ac8853a687c3e7b5d8bb"),
            Map.entry("TROPICAL_FISH", "http://textures.minecraft.net/texture/5d7b92a1bdc733d2a53fb095c70b732b71a223a9ceca72b872e1ee990a6a907e"),
            Map.entry("DROWNED", "http://textures.minecraft.net/texture/c3f7ccf61dbc3f9fe9a6333cde0c0e14399eb2eea71d34cf223b3ace22051"),
            Map.entry("DOLPHIN", "http://textures.minecraft.net/texture/8e9688b950d880b55b7aa2cfcd76e5a0fa94aac6d16f78e833f7443ea29fed3"),
            Map.entry("CAT", "http://textures.minecraft.net/texture/a0db41376ca57df10fcb1539e86654eecfd36d3fe75e8176885e93185df280a5"),
            Map.entry("PANDA", "http://textures.minecraft.net/texture/962a024e871bfb2eb995dad21e9e70489043d3cbc73d7fa5520aeb765993347"),
            Map.entry("PILLAGER", "http://textures.minecraft.net/texture/18e57841607f449e76b7c820fcbd1913ec1b80c4ac81728874db230f5df2b3b"),
            Map.entry("RAVAGER", "http://textures.minecraft.net/texture/cd20bf52ec390a0799299184fc678bf84cf732bb1bd78fd1c4b441858f0235a8"),
            Map.entry("TRADER_LLAMA", "http://textures.minecraft.net/texture/5a4eed85697c78f462c4eb5653b05b76576c1178f704f3c5676f505d8f3983b4"),
            Map.entry("WANDERING_TRADER", "http://textures.minecraft.net/texture/303fdd25763b3d475e09f8bf5a42e8683e48ab7323a3cc542bb8d99ae4b703b9"),
            Map.entry("FOX", "http://textures.minecraft.net/texture/d8954a42e69e0881ae6d24d4281459c144a0d5a968aed35d6d3d73a3c65d26a"),
            Map.entry("BEE", "http://textures.minecraft.net/texture/886a509ff3cd471f1b428a194b6711470a54773e5de6ee07f7e601cc5e75a200"),
            Map.entry("HOGLIN", "http://textures.minecraft.net/texture/7ad7b5aeb220c079e319cd70ac8800e80774a9313c22f38e77afb89999e6ec87"),
            Map.entry("PIGLIN", "http://textures.minecraft.net/texture/5081a1239fffe135cbfa4a98a6aa6cc5b0787ad0790f56a16bf07f86374606c5"),
            Map.entry("STRIDER", "http://textures.minecraft.net/texture/125851a86ee1c54c94fc5bed017823dfb3ba08eddbcab2a914ef45b596c1603"),
            Map.entry("ZOGLIN", "http://textures.minecraft.net/texture/3c8c7c5d0556cd6629716e39188b21e7c0477479f242587bf19e0bc76b322551"),
            Map.entry("PIGLIN_BRUTE", "http://textures.minecraft.net/texture/3e300e9027349c4907497438bac29e3a4c87a848c50b34c21242727b57f4e1cf"),
            Map.entry("AXOLOTL", "http://textures.minecraft.net/texture/21c3aa0d539208b47972bf8e72f0505cdcfb8d7796b2fcf85911ce94fd0193d0"),
            Map.entry("GLOW_SQUID", "http://textures.minecraft.net/texture/4cb07d905888f8472252f9cfa39aa317babcad30af08cfe751adefa716b02036"),
            Map.entry("GOAT", "http://textures.minecraft.net/texture/a662336d8ae092407e58f7cc80d20f20e7650357a454ce16e3307619a0110648"),
            Map.entry("ALLAY", "http://textures.minecraft.net/texture/7ed1e82ef7c059df8fde561e3edaeaec835c6c3f072c16805db60bf9329a2b75"),
            Map.entry("FROG", "http://textures.minecraft.net/texture/45852a95928897746012988fbd5dbaa1b70b7a5fb65157016f4ff3f245374c08"),
            Map.entry("TADPOLE", "http://textures.minecraft.net/texture/987035f5352334c2cba6ac4c65c2b9059739d6d0e839c1dd98d75d2e77957847"),
            Map.entry("WARDEN", "http://textures.minecraft.net/texture/b0b202de27ce278893031f93005fb1310bbf952058bc41fe079074f77605a906"),
            Map.entry("CAMEL", "http://textures.minecraft.net/texture/ba4c95bfa0b61722255389141b505cf1a38bad9b0ef543de619f0cc9221ed974"),
            Map.entry("SNIFFER", "http://textures.minecraft.net/texture/bb1d06c9d2fd18db6739b4f870bc8610bc5ac9bdbb165c9f1c7b8bde39c78364"),
            Map.entry("BREEZE", "http://textures.minecraft.net/texture/a275728af7e6a29c88125b675a39d88ae9919bb61fdc200337fed6ab0c49d65c"),
            Map.entry("ARMADILLO", "http://textures.minecraft.net/texture/9852b33ba294f560090752d113fe728cbc7dd042029a38d5382d65a2146068b7"),
            Map.entry("BOGGED", "http://textures.minecraft.net/texture/7144d7bd1469593df59dce0cefb3afeaa54b8b3299cdbfc34b338bfb9ad2100"),
            Map.entry("CREAKING", "http://textures.minecraft.net/texture/aef009d86fcc420361a68cbb8bfa85a7422bfe9e2f306247be1e1b5d20fc52b1")
    );

    public static void setEntityTexture(ItemStack item, LivingEntity entity) {
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta == null) return;
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        try {
            profile.getTextures().setSkin(getTexture(entity.getType()));
        } catch (MalformedURLException exception) {
            ClickMobs.LOGGER.warning("Failed to set entity texture: " + exception.getMessage());
        }
        meta.setOwnerProfile(profile);
        item.setItemMeta(meta);
    }

    @SuppressWarnings("deprecation")
    private static URL getTexture(EntityType type) throws MalformedURLException {
        String name = type.getName();
        if (name == null) return URI.create(DEFAULT_TEXTURE).toURL();
        return URI.create(TEXTURE_MAP.getOrDefault(type.getName().toUpperCase(), DEFAULT_TEXTURE)).toURL();
    }
}
