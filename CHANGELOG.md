- Added full Happy Ghast compatibility.
  - You can now attach harnesses and boats without picking the mob up.
- Added new config option `blacklisted_items_in_hand` to prevent
picking up mobs when holding specific items.
  - Use the `?all` tag to blacklist all items. This will make it so players
    can only pick up mobs with an empty hand.
- Added new config option `enable_dispensers` that controls whether dispensers can dispense mobs.
- Overhauled dispenser logic.
  - Dispensers now dispense mobs on all versions.
  - Fixed: Mob item was not being removed after being dispensed.
  - Fixed: Mob only gets dispensed if a player/mob is in front of the dispenser.