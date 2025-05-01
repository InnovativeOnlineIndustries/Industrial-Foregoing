# Version 3.629

* Added better support pumpkins and melons in the hydroponic bed
* Added better Seed support in the Simulated Processor for:
  * Bamboo
  * Sugarcane & Cactus
  * Kelp
  * Pumpkin & Melon

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
