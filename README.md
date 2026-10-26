# Create: Mixer Upgrades

An addon for **Create 6.0** on **NeoForge 1.21.1**. It adds four upgrade kits for the Mechanical Mixer. When a mixing cycle finishes, an upgraded mixer runs the recipe up to N times instead of once:

| Kit | Tier | Recipe runs per cycle |
|---|---|---|
| Brass Mixer Upgrade | I | 16 |
| Precision Mixer Upgrade | II | 32 |
| Sturdy Mixer Upgrade | III | 48 |
| Blazing Mixer Upgrade | IV | 64 |

The cycle time stays the same. The batch ends early if the basin runs out of ingredients or output space. For example, fluid outputs are capped by the basin's tank size.

## Using a kit

- **Install:** right-click a Mechanical Mixer with a kit. If a different kit is already installed, you get the old one back.
- **Remove:** sneak and right-click the mixer with an empty hand.
- **Breaking the mixer** drops its kit. A mixer moved by a contraption keeps its kit.
- **Engineer's Goggles** show the installed tier and batch size.
- **Ponder:** hover any kit, or Create's Mechanical Mixer, and hold W for an animated walkthrough. The scene code is in `src/main/java/dev/mixerupgrades/ponder/` and the scene's blocks are in `assets/create_mixer_upgrades/ponder/mixer_upgrade.nbt`. Each `.text(...)` call in the scene needs a matching `ponder.mixer_upgrade.text_N` entry in `en_us.json`, numbered in order.

## Recipes

Each kit is made from the one before it, so you have to go through all the tiers in order.

**Tier I: Brass Mixer Upgrade** (Sequenced Assembly, 2 loops)
Start from a Brass Casing. Each loop: deploy a Whisk, deploy a Precision Mechanism, deploy a Brass Sheet, then press.

**Tier II: Precision Mixer Upgrade** (Mechanical Crafting)
```
 T Q T
B P K P B
 T Q T
```
T = Electron Tube, Q = Polished Rose Quartz, P = Precision Mechanism, B = Brass Sheet, K = Brass Mixer Upgrade

**Tier III: Sturdy Mixer Upgrade** (Sequenced Assembly, 3 loops)
Start from a Precision Mixer Upgrade. Each loop: deploy a Sturdy Sheet, fill with 250 mB of lava, deploy a Precision Mechanism, then press.

**Tier IV: Blazing Mixer Upgrade** (Mechanical Crafting)
```
 S B S
T P K P T
 S N S
```
S = Sturdy Sheet, B = Blaze Cake, T = Electron Tube, P = Precision Mechanism, N = Netherite Ingot, K = Sturdy Mixer Upgrade

Total cost from scratch to Tier IV: about 9 Precision Mechanisms, 7 Sturdy Sheets, 6 Electron Tubes, 1 Blaze Cake and 1 Netherite Ingot.

Every recipe is a normal datapack JSON under `data/create_mixer_upgrades/recipe/`, so modpacks can override them.

## Config

`config/create_mixer_upgrades-common.toml` sets the batch size for each tier (1–1024).

## Building

```bash
./gradlew build
```
The jar is written to `build/libs/`. Built against Create 6.0.10.

Supports Create 6.0.7–6.0.10 on NeoForge 21.1.200 or newer. Each of those Create releases passed the GameTests, and the Ponder scene was checked on 6.0.7. Create 6.0.0–6.0.6 use older recipe formats, so the kit recipes don't load there.

To test against a released Create jar instead of the dev dependencies, put that jar in `<dir>/mods` and run:

```bash
./gradlew runGameTestServer -PcompatDir=<dir>
```

- `./gradlew runClient` starts a dev client with Create loaded.
- `./gradlew runGameTestServer` runs the automated in-world tests in `src/gametest`.
