# Version 3.7.4

### Changes
* **Plant Gatherer seed blacklist**: Added config option `seedCollectionBlacklistTags` (default: `["mysticalagriculture:seeds"]`). Seeds matching any listed item tag are discarded from the Plant Gatherer's drops instead of being collected; the crop yield is still gathered, and any seed used for auto-replant (ether mode) is kept.

---

# Version 3.7.3

### Bug Fixes
* **Fixed Potion Generator crash**: Added null check for PotionContents to prevent NullPointerException when ticking the Mycelial Potion Generator.

---

# Version 3.7.2

### Bug Fixes
* **Fixed Simulation Processor data duplication**: Cached simulation data was being transferred between different processors when swapping them in Hydroponic Bed slot. Now uses object reference comparison to correctly detect processor changes.

---

# Version 3.7.0

## Hydroponic Bed - Virtual-Only Mode

### Changes
* **Removed physical growth mode**: Hydroponic Bed now operates exclusively in virtual mode
* **Removed mode toggle button**: The UI no longer has a button to switch between physical and virtual modes
* **New config option**: `progressMultiplier` controls how much slower virtual mode is (default: 3x)
* **Seeds are no longer produced**: In virtual mode, the crop is not physically harvested, so seeds/plantable items are filtered from drops
  * This fixes compatibility with mods like Mystical Agriculture that disable seed drops
* **Improved Simulation Processor caching**: Cache is now invalidated only when the item changes, not when NBT data updates
* **Deferred NBT saving**: Simulation data is saved every 20 ticks instead of every work cycle
  * Reduces `toNBT()` calls by ~95%, significantly lowering CPU overhead from codec serialization

### Breaking Changes
* Physical growth mode is no longer available
* Existing Hydroponic Beds will automatically use virtual mode

---

# Version 3.6.42

## ServerLoadBalancer Configuration Overhaul

### Centralized Configuration
* **Separate config section**: ServerLoadBalancer now has its own configuration in `[ServerConfig.ServerLoadBalancerConfig]`
* **Removed duplicate settings**: Tick skipping settings removed from `HydroponicBedConfig` and `SimulatedHydroponicBedConfig`
* **Master enable switch**: New `enabled` option to completely enable/disable the adaptive tick skipping system
* **Per-world TPS tracking**: New `perWorldTps` option for servers with separate worlds per player
  * When enabled, machines only slow down in worlds with low TPS
  * Other worlds with normal TPS continue at full speed
  * Ideal for skyblock/island servers where each player has their own dimension

### Configuration Options
| Option | Default | Description |
|--------|---------|-------------|
| `enabled` | true | Master switch for the entire system |
| `perWorldTps` | false | Track TPS per-world instead of globally |
| `tpsSampleInterval` | 20 | TPS sampling interval in ticks |
| `normalTps` | 19.0 | TPS threshold for normal operation (no skipping) |
| `highLoadTps` | 15.0 | TPS threshold for medium load (skip every 2nd tick) |
| `criticalLoadTps` | 10.0 | TPS threshold for high load (skip every 4th tick) |
| `minSkippedTicks` | 1 | Minimum skip interval (1 = no skipping) |
| `maxSkippedTicks` | 8 | Maximum skip interval |
| `criticalGradualProgressiveSkippedTicks` | true | Gradually increase skipping when load increases |
| `nonCriticalGradualRegressiveSkippedTicks` | true | Gradually decrease skipping when load decreases |

### Breaking Changes
* **Config migration required**: Old tick skipping settings in `HydroponicBedConfig` and `SimulatedHydroponicBedConfig` are no longer used
* All tick skipping settings are now in `[ServerConfig.ServerLoadBalancerConfig]`

### Updated Machines
* Hydroponic Bed and Simulated Hydroponic Bed now use centralized ServerLoadBalancer config
* Both machines support per-world TPS mode

---

# Version 3.6.39

## Additional Performance Optimizations

### Adaptive Tick Skipping (ServerLoadBalancer)
* **TPS-aware throttling**: Hydroponic beds and Simulated Hydroponic beds automatically reduce tick frequency when server TPS drops
* **Configurable thresholds**: TPS ≥19 = normal, 15-19 = every 2nd tick, 10-15 = every 4th tick, <10 = every 8th tick
* **Growth compensation**: Skipped ticks are compensated by multiplying growth increments, maintaining overall growth rate
* **Estimated savings**: Up to 87.5% CPU reduction for Hydroponic Beds during severe lag

### Simulation Processor Caching (HydroponicBedTile)
* **Cached Simulation object**: NBT parsing for HydroponicSimulationProcessorItem now happens only when the item in the slot changes, not on every harvest operation
* **Eliminates expensive codec parsing**: `ItemStack.parseOptional()` was being called every tick during harvesting — now cached
* **Estimated savings**: ~2% CPU reduction when using simulation processors

### Progress Bar Optimization (IndustrialWorkingTile & IndustrialProcessingTile)
* **Cached augment checks**: Speed augment checks now happen every 20 ticks instead of every tick
* **Conditional updates**: `setProgressIncrease()` is only called when the value actually changes
* **Reduced method call overhead**: Eliminates unnecessary calls to Titanium's ProgressBarComponent

### Ether Growth Optimization (HydroponicBedTile)
* **Fast growth with ether**: Now uses `tryFastGrow()` with 2 increments instead of `performBonemeal()` for standard crops
* **Eliminates neighbor updates**: `performBonemeal()` triggers expensive `Level.setBlock()` with neighbor shape updates (~3% CPU)
* **Fallback preserved**: `performBonemeal()` still used for StemBlock and modded plants that need special handling

---

# Version 3.6.38

## Performance Optimizations for HydroponicBedTile

This update brings significant performance improvements to the Hydroponic Bed, reducing server tick time and memory allocations.

### Caching Optimizations
* **BlockPos caching**: Cache `BlockPos.above()` to avoid object creation every tick
* **Neighbor tile caching**: Cache references to neighboring HydroponicBedTile blocks, refreshed every 100 ticks instead of querying every tick
* **Augment check caching**: Cache augment presence checks every 20 ticks instead of every tick
* **BlockState caching**: Pass cached BlockState to PlantRecollectable methods instead of re-querying

### Algorithm Improvements
* **Fast grow path**: Add `tryFastGrow()` for direct age manipulation via `CropBlock.getStateForAge()` instead of expensive `randomTick()` calls
* **Optimized block updates**: Replace `setBlockAndUpdate()` with `setBlock()` using minimal update flags (Block.UPDATE_CLIENTS) — reduces neighbor updates and block update cascades
* **Balance interval increase**: Increase ether balance interval from 5 to 10 ticks — 50% fewer balance operations
* **Static directions array**: Use pre-allocated `HORIZONTAL_DIRECTIONS` array instead of creating streams

### Memory & GC Improvements
* **Lambda elimination**: Replace lambda allocations with method references in hot paths
* **Stream elimination**: Replace `stream().filter().findFirst()` with simple for-loops in `findRecollectable()`
* **forEach elimination**: Use indexed for-loop instead of `forEach` in `tryToHarvestAndReplant()`
* **NonNullList removal**: Remove unnecessary `NonNullList` creation in `getBlockDrops()`
* **Disable auto-tick**: Disable automatic progress bar ticking for `etherBuffer` — reduces unnecessary syncs

### BlockUtils Optimizations
* **isLeaves() optimization**: Reduce from 5 to 1 `getBlockState()` calls by reusing cached state
* **isLog() optimization**: Reduce redundant `getBlockState()` calls
* **isChorus() optimization**: Reduce redundant `getBlockState()` calls
* **Block comparison**: Use `==` instead of `equals()` for Block instance comparisons (singleton pattern)

### Estimated Performance Gains
* **~60-80% reduction** in object allocations per tick per Hydroponic Bed
* **~40-50% reduction** in `getBlockState()` calls for tree/chorus harvesting
* **~30% reduction** in neighbor block update overhead
* Particularly noticeable in large farms with 50+ Hydroponic Beds

---

# Version 3.6.37

* Fixed plant gatherer getting stuck on bamboo #1398
* Fixed Meat Feeder not consuming meat #1597 - flutz1
* Fix: Correct JEI energy display to use configured Dissolution Chamber powerPerTick value #1529 - flutz1
* Fix: MobDetector should now work as intended #1609 - flutz1
* Updated Simplified Chinese localization - UraraChiya

# Version 3.6.36

* Fixed oil recipe, closes #1608

# Version 3.6.35

* Fix: java.util.ConcurrentModificationException in ItemProperties.register - Viola-Siemens 
* Updated PT_BR localization - PrincessStelllar
* Retextured Industrial-Foregoing - MHanHanBing & RuiXuqi
* Update ru_ru.json - Shiro4ka
* Fix equals check in EnchantmentApplicatorTile - LuminaSapphira
* Ore Fluid Drilling recipe rework - Satherov
* Spanish Translation for Industrial Foregoing - RadzRatz

# Version 3.6.32

* Fixed Laser drill crashing when registry its not present, closes #1585
* Improved Mycelial Reactor to be able to function better with multiple reactors, closes #1583

# Version 3.6.31

* Fixed Hydroponic Simulation Processor number formatting on the tooltip

# Version 3.6.29

* Added better support pumpkins and melons in the hydroponic bed
* Added better Seed support in the Simulated Processor for:
  * Bamboo
  * Sugarcane & Cactus
  * Kelp
  * Pumpkin & Melon
* Added a render of the current crop to the Simulated Hydroponic Processor
* Added a tag to blacklist from the simulated hydroponic bed `industrialforegoing:hydroponic_simulation_blacklist`

# Version 3.6.28

* Fixed Simulated Hydroponic bed power config not being used
* Added a chance to increase the executions of processor in the simulation chamber
* Turkish Localization (#1575) - RuyaSavascis
* ja-jp 2025/04/16 update (#1572) - kyuta683

# Version 3.6.27

* Changed Simulated Hydroponic beds default working time to 700 ticks to be more in line with Normal Hydroponic beds

# Version 3.6.26

* Added Simulated Hydroponic Beds

# Version 3.6.25

* Fixed tags in sand to silicon crush recipe by SiriosDev
* Change depth mask back to true after rendering by RaphiMC closes #1562
* fix DissolutionChamberRecipe only use first fluid in tag by DancingSnow0517
* Update Conveyor Facing, closes #1535
* Ignore updating the durability of an item when extracting enchantments if the item is unbreakable to avoid other mods
  faults, closes #1554

# Version 3.6.24

* Added backpack safety checks when syncing to avoid NPE, closes #1553
* Allow potion brewer output if no filter is specified by ZeroMemes
* Added Dissolution Chamber serializer support for fluid tags by Christofmeg
* Added back oil fluid laser drill integration by Vectrobe, closes #1558
* Updated Latex Processing Unit Manual entry to add the changes to latex processing, closes #1550

# Version 3.6.23

* DRY up latex bonus code, make "triple" display conditional by kylev
* Fixed boss bar not being hidden when in the stasis chamber, closes #1549
* Added missing tag to pink slime blocks, closes #1544
* Added FTB Chunks integration back to the Infinity Nuke, closes #1543
* Improved Pitiful Furnace fuel consumption, closes #1541

# Version 3.6.22

* Renamed mixin file to avoid future problems, closes #1526
* Changed how the wither builder works to just spawn the wither instead of building the structure, closes #1474
* Fixed Backpacks saving when inserting stacks bigger than 64 items, closes #1525
* Added missing translatable strings and translated to ja_jo by momo-i2
* Fixed Shiny Vex Particles
* Fixed wrong pink item added to the ingot tags, closes #1520
* Fixed crash when Plungers got teleported, closes #1522

# Version 3.6.21

* Changed how item components are added to items
* Modified Infinity Backpack recipe easier to make
* Added ingot tags to pink slime

# Version 3.6.20

* Improve Hydroponic Bed code efficiency

# Version 3.6.19

* Fixed EMI not having the crafting info from the Dissolution Chamber

# Version 3.6.17

* Fixed Processing addons tooltip having the wrong number
* Make DissolutionChamberRecipe JEI Category respect NBT on input items #1502 - Christofmeg
* Rework Transporter Logic #1500 - Kanzaji
  * Transporters now work in Regulate Mode on the Extraction side!
  * They will leave in the storage the amount of items specified in the filter.
  * Regulate Mode affects only whitelist mode - it's ignored in blacklist mode.
  * Efficiency is being calculated now with use of Math.ceil(int) instead of direct cast to int, fixing an issue that
    max transporters were able to handle "only" 63 of stuff per work cycle. (They now handle at max 64/t)
  * Blacklist now correctly passes all items (except blacklisted ones) on the insertion side.
  * Fluid Transporters now can handle 333,(3)mb/t without upgrades (16,(6)mb/t before), and 64 000mb/t with all tier 2
    upgrades (3 200mb/t before)
* Fix Enchanter Extractor making tools indestructible. #1498 - Kanzaji
* Entity.RemovalReason change in MobImprisonmentToolItem.java #1494 - frikinjay
* Update ru_ru.json #1452 - gri3229
* Create ko_kr.json #1444 - new-3
* Fixed gears missing "c:gears" tag, closes #1504
* Fixed infinity drill combining items passed their stack limit, closes #1501
* Fixed infinity drill artifact tier not having the proper depth
* Fixed infinity drill mining area not showing
* Added missing Mycelial Generator Catergory to EMI, closes #1505

# Version 3.6.15

* Changed mods.toml config to use proper required dependencies, closes #1490

# Version 3.6.14

* Optimize JEI startup time (#1481) - mezz
* Add item_exists condition to the guide recipe (#1482)  - Mrbysco
* Add a Dissolution page to Patchouli (#1483) - Mrbysco
* A bunch of Bug Fixes for 1.21 (#1486) - Kanzaji
  * StrawUtils tries to sort immutable list causing crash #1478
  * Infinity Tools don't appear to consume biofuel #1476
  * Mob Crusher UI problem #1472
  * Plant Sower isnt working with netherwart #1477
  * Potion brewer causes game to exit upon adding ingredients after netherwart #1479

# Version 3.6.13

* Fixed crusher recipes for the Material Stonework Factory

# Version 3.6.12

* Renamed some tags to add plural versions
* Fixed Ore Fluids having repeated ore names
* Added EMI support
* Fixed Sneak + Right Click in the Mycelial Reactor not showing which generators aren't working
* Fixed Myceliar Reactor Having the render enabled always
* Added a button to disable particle spawning in machines with a working area

# Version 3.6.11

* Added Pink Slime Block
* Fixed addon filter in the fluid/ore laser base
* Fixed fluid/ore laser base progress bar going over the limit in big setups

# Version 3.6.10

* Fixed manual recipe

# Version 3.6.9

* Fixed Potion Brewer Functionality

# Version 3.6.8

* Added donation link
* Fixed Infinity Tools server side crash

# Version 3.6.7

* Fixed being able to enchant at level 0

# Version 3.6.6

* Fixed Fluid/Ore Laser drill JEI recipe closes #1467
* Fixed Fluid Laser Drill recipe requiring nether dimension and specific biomes
* Fixed manual closes #1466
* Changed Recipes to use wooden chests only

# Version 3.6.5

* Fixed Meat Feeder not showing in JEI
* Maybe fixed curios integration?
* Fixed tint color issues

# Version 3.6.4

* Enchantment Factory tank now syncs properly

# Version 3.6.2

* Working area renders shouldn't have z fighting anymore
* Machine Frames can now be placed
* Removed Black Hole Units and Tanks & Controller (use Functional Storage)
* Infinity Backpacks can now be placed so contents can be extracted easily
* Laser recipes now use biome tags and dimensions
* Infinity trident now has increased launch speed based on tier and increased max loyalty and riptide (no longer needs
  water to use, and does more damage)
* Machines that use fake players now use the player uuid
* Addons now have a description of what they do
* Transporters now have an inventory for addons
* In the Enchantment Factory now you can choose what level you want to enchant an item on
* Buffed bioreactor production
* Ore/Fluid Laser Base depth selector is now a text field
* Added a Filter to the Potion Brewer, now items can only be extracted if it matches the filter
* Ported to 1.21

# Version 3.5.19

* Fixed Machine Settings Copier not working properly in some machines

# Version 3.5.18

* Added Machine Settings Copier

# Version 3.5.17

* Fix bug where Black Hole Units could transmute items (by notcake)
* Fix FireworkGeneratorCategory not showing up in JEI (by Christofmeg)
* Fixed black hole units losing the nbt of items when stored in controllers, closes #1435

# Version 3.5.16

* Fixed Shiny versions of tools not being added to Infinity Tools
* Changed Enchantment Applicator slot to limit to 1, closes #1419
* Fixed end version of ores in the Laser Ore Drill having an extra tag, closes #1417
* Changed Potion Brewer slot to limit to 1 so bottle don't stack when a mod changes that, closes #1413
* Added support for mangrove trees in the plant gatherer, closes #1412
* Fixed Cast Exception on player renderer, closes #1407
* Added a tag cache for ore fluid, closes #1405

# Version 3.5.13

* Fixed Plant Gatherer skipping blocks

# Version 3.5.12

* Fixed Infinity Tools resetting when being used in the Smithing table, closes #1311

# Version 3.5.11

* Sewer can now extract essence from players
* Fixed Plant Sower rotating when it's not supposed to
* Addons can now be inserted into machines when shift + right click into machines
* Added support for trees and more stuff in the Hydroponic Bed

# Version 3.5.10

* Added Manual Changelog
* Changed Latex Processing Unit to make crafted dry rubber instead of tiny ones with increased fluid latex req
* Changed Latex Processing Unit recipe to not use Latex Bucket
* Fixed Bioreactor efficiency bar not resetting and buffed biofuel production rates
