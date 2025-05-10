![banner](https://i.imgur.com/jMvJaKO.png)

[![modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/plugin/clickmobs)
[![curseforge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/curseforge_vector.svg)](https://www.curseforge.com/minecraft/mc-mods/clickmobs)
[![discord](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/social/discord-plural_vector.svg)](https://discord.gg/zUetzp3Gzk)



## ClickMobs
**ClickMobs is a simple quality of life mod/plugin that allows you to pick up any mob into your inventory and carry
them around.**

![paper](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/paper_vector.svg)
![fabric](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/fabric_vector.svg)
![spigot](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/spigot_vector.svg)
![purpur](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/purpur_vector.svg)

### Features
- Pick mobs up into your inventory by **shift + right clicking** them.
- Place them back by placing the mob head.
  - Place the mobs directly into boats, minecarts, etc.
  - Use dispensers to spawn the mobs back automatically.
- Clean messages, sound effects and particle effects.
- Blacklist/whitelist any mob you want using **tags**!
- **Language support:** German & English (Paper/Spigot only)

### Blacklisting & Whitelisting Mobs
- Blacklist/whitelist any mob you want using **tags**.
  - You can use and combine tags like `?hostile`, `?nametagged(...)`, `?baby`, etc.
    to select multiple mobs based on their properties.
  - **For more information, read the wiki:** [Wiki/Tags](https://github.com/Clickism/ClickMobs/wiki/Tags)
- You can also blacklist/whitelist individual mobs.
- You can manage the blacklist/whitelist in the **"config.yml"** file.

### Configuration
- For Paper/Spigot, you will find the configuration file at **"plugins/ClickMobs/config.yml"**.
- For Fabric, you will find the configuration file at **".minecraft/config/ClickMobs/config.yml"**.
  - WARNING: If you are using another client (i.E. Modrinth App), the config file will be located at the installation
    directory instead.
- Make sure you **save** the config file after making changes and do the following:
  - For Paper/Spigot, reload/restart your server or use the command *"/clickvillagers reload"*.
  - For Fabric, restart your game or server fully.

### Compatibility
- ClickMobs is fully compatible with [ClickVillagers](https://modrinth.com/plugin/clickvillagers)!

### Licensing
- This project is licensed under the GPLv3 license.
- Refer to LICENSE.md for more information.
