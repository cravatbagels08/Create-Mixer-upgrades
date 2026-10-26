<!--
Modrinth project fields (copy into the project settings):

Name:     Create: Mixer Upgrades
Summary:  Upgrade kits for Create's Mechanical Mixer that run a recipe 16, 32, 48 or 64 times per mixing cycle.
Category: Technology (optionally also Utility)
Loader:   NeoForge     Game version: 1.21.1     Dependency version: Create 6.0.7+     Environment: client and server (required on both)
License:  MIT
Dependency: Create (required)
Source code:   https://github.com/cravatbagels08/Create-Mixer-upgrades
Issue tracker: https://github.com/cravatbagels08/Create-Mixer-upgrades/issues
Icon:     release/icon.png       Gallery: release/gallery/*.png

Everything below this comment is the project description.
-->

# Create: Mixer Upgrades

An addon for **Create** that adds **upgrade kits for the Mechanical Mixer**. When a mixing cycle finishes, an upgraded mixer runs its recipe many times at once instead of once.

| Kit | Tier | Recipes per cycle |
|---|---|---|
| Brass Mixer Upgrade | I | 16 |
| Precision Mixer Upgrade | II | 32 |
| Sturdy Mixer Upgrade | III | 48 |
| Blazing Mixer Upgrade | IV | 64 |

Cycle time stays the same; only the batch size goes up. If the basin runs out of ingredients or output space partway through, the mixer makes what it can and stops.

## How to use

- **Install:** right-click a Mechanical Mixer with a kit. If it already has a different kit, you get the old one back.
- **Remove:** sneak and right-click the mixer with an empty hand.
- **Break the mixer** and the kit drops. A mixer moved by a contraption keeps its kit.
- **Engineer's Goggles** show the installed tier and batch size.
- **Ponder:** hold **W** over a kit, or over the Mechanical Mixer, for an in-game walkthrough.

## Recipes

Each kit is made from the one before it, so you have to go through all the tiers in order. The recipes are meant to take real Create production without being a grind. All recipes appear in JEI.

**Tier I: Brass Mixer Upgrade** (Sequenced Assembly, 2 loops)
Start from a Brass Casing. Each loop: deploy a Whisk, deploy a Precision Mechanism, deploy a Brass Sheet, then press.

**Tier II: Precision Mixer Upgrade** (Mechanical Crafting)
4 Electron Tubes, 2 Polished Rose Quartz, 2 Precision Mechanisms, 2 Brass Sheets, and the Tier I kit.

**Tier III: Sturdy Mixer Upgrade** (Sequenced Assembly, 3 loops)
Start from the Tier II kit. Each loop: deploy a Sturdy Sheet, fill with 250 mB of lava, deploy a Precision Mechanism, then press.

**Tier IV: Blazing Mixer Upgrade** (Mechanical Crafting)
4 Sturdy Sheets, 1 Blaze Cake, 2 Electron Tubes, 2 Precision Mechanisms, 1 Netherite Ingot, and the Tier III kit.

From scratch to Tier IV costs roughly 9 Precision Mechanisms, 7 Sturdy Sheets, 6 Electron Tubes, a Blaze Cake and one Netherite Ingot. Modpack makers can replace any recipe with a datapack; they live under `data/create_mixer_upgrades/recipe/`.

## Config

`config/create_mixer_upgrades-common.toml` sets the batch size for each tier (1–1024).

## Compatibility

- Minecraft **1.21.1**, **NeoForge 21.1.200** or newer
- Create **6.0.7 – 6.0.10** (tested on each). Create 6.0.6 and older are not supported because their recipe format is different.
- Needed on **both client and server**.

This is an unofficial addon and isn't made by the Create team.

## Source and bug reports

Source code and issue tracker: https://github.com/cravatbagels08/Create-Mixer-upgrades

## License

MIT
