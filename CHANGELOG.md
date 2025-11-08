- Added full Happy Ghast compatibility.
  - You can now attach harnesses and boats without picking the mob up.
- Added new config option **blacklisted_items_in_hand** to prevent
picking up mobs when holding specific items.
- Added new config option **enable_dispensers** that controls whether dispensers can dispense mobs.
- Overhauled dispenser logic.
  - Dispensers now dispense mobs on all versions.
  - Fixed a bug with the mob item not being removed after being dispensed.
  - Fixed a bug where the mob would only be dispensed if a player/mob is in front of it.