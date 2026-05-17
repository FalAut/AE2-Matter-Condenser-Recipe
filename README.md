# AE2 Matter Condenser Recipe

A NeoForge mod for **Minecraft 1.21.1** that replaces AE2 Matter Condenser output logic with a strict, datapack-driven recipe system.

## Features

- Replaces AE2 condenser output selection with custom recipes (`ae2_matter_condenser_recipe:condenser`).
- Uses **strict recipe mode**: no fallback to AE2 condenser config outputs.
- Keeps output cycling, and adds a `>` selector button in GUI for direct output selection.
- Renders output as real item icons in buttons and selection lists, with synced tooltips.
- Includes a selectable `trash` mode in GUI selectors.
- Adds recipe-viewer compatibility layers:
  - JEI (via AE2 JEI Integration)
  - EMI
  - REI
- Adds ExtendedAE **ME Void Cell** support:
  - Void Cell uses the same condenser recipe data
  - Reworked Void Cell GUI (single mode button + output selector)
  - Void Cell item tooltip is synced to current selected output

## Recipe Format

Recipe location example:

- `data/ae2_matter_condenser_recipe/recipe/condenser/matter_ball.json`

Schema:

```json
{
  "type": "ae2_matter_condenser_recipe:condenser",
  "result": {
    "id": "ae2:matter_ball",
    "count": 1
  },
  "required_power": 256
}
```

Notes:

- `required_power` is the amount needed before output is produced.
- There is no `input` field; condenser/void cell logic is output+power driven.

## Build

```bash
./gradlew build -x test
```

Client run:

```bash
./gradlew runClient
```

## Compatibility Notes

- Requires AE2 on 1.21.1 NeoForge environment.
- JEI integration depends on AE2 JEI Integration being present.
- EMI/REI hooks are implemented with mixins against AE2 integration modules.
- Viewer-side condenser recipe lists exclude trash entries by design.
