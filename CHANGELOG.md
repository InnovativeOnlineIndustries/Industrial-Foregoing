# 1.6.4
+ Added config options to disable machines from the game
+ Added config option to not show Book entries in JEI
+ More safe fluid crafting for the Fluid Crafter and making the tank fluid the default fluid for the recipe.
+ Improved leaf breaking so it doesn't drop items on the floor, I hope.
+ Added chorus support for the Plant Gatherer
+ Improved tooltips, Plant Gatherer shows what it can gather.

# 1.6.3
+ Updated to tesla core lib 1.0.12. Adding RF display to the machines, click the power bar to change it.
+ Fixed something wrong with my poop machine.
+ Added pt_BR translation thanks to InterPlay.
+ Added a null check for the Fluid Crafter to prevent NPE in bad recipes.
+ Moved manual construction to later in the game.
+ Fixed config GUI.

# 1.6.2
+ Fixed seeds not being placed in the Plant sower

# 1.6.1
+ Added a check for empty inputs in the furnace.
+ Nerfed the StoneWork Factory, now it does 1 operation at a time, from left to right https://i.imgur.com/zzL6z1E.gifv
+ Fixed Fluid Crafter consuming items that should return
+ Added a reducedChunkUpdates config for the Plant Gatherer where it will chop down the whole tree using the same amount of power and time as before, disabled by default

# 1.6.0
+ Fixed BlackHoleController spawning client side items.
+ Fixed a NPE with the Contributors Cat Ears when not connected to the internet.
+ Added adult filter to the mob crusher so it will only kill adults when installed.
+ Added safety checks to place an remove blocks.
+ Added blast resistance to the Mob Crusher.
+ Added Fluid Crafter.
+ Added Exact Copy in the mob duplicator as a config disabled by default

# 1.5.13
+ Added a config option to enable TE Machine Frames in the recipes
+ Added a blacklist for the mob duplicator, blacklisted entities are shown when captured in the Mob Imprisonment Tools
+ Changed a color in the StoneWork Factory so it's not shared with the power one
+ Wither Builders use items from any slot if they need it
+ Animal Rancher gathers black ink from squids

# 1.5.12
+ Fixed BHU being derpy with the new ItemHandler for big stacks

# 1.5.11
+ Added Thermal Expansion Machine Frame as a frame in the recipes
+ Added Tooltip descriptions for machines that accept range addons
+ Removed the limitation of the Animal Rancher to being only able to operate with sheeps and cows

# 1.5.10
+ Updated to Forge 1.12.2-14.23.1.2554
+ Fixed [#99](https://github.com/Buuz135/Industrial-Foregoing/issues/98)
+ The Mob Crusher now picks up the XP Orbs even if the tank is full
+ The Resourceful Furnace now doesn't allow to input items in the output slot
+ The Plant Sower will now get items from another slots if the current slot is empty and it is locked
+ Added modded ingots like copper or tin for the range addon if they exist like the old MFR

# 1.5.9
+ Fixed [#97](https://github.com/Buuz135/Industrial-Foregoing/issues/97)
+ Fixed [#95](https://github.com/Buuz135/Industrial-Foregoing/issues/95)

# 1.5.8
+ Fixed [#96](https://github.com/Buuz135/Industrial-Foregoing/issues/96)

# 1.5.7
+ Fixed [#92](https://github.com/Buuz135/Industrial-Foregoing/issues/92)
+ Fixed [#93](https://github.com/Buuz135/Industrial-Foregoing/issues/93) for BHU and BHUControllers
+ Improved collision checking in the spawner

# 1.5.6
+ Changed how the mobs are grown in the AnimalGrower
+ Added cleaning recipes for the Black Hole Units and Black Hole Tank
+ Increased Block Resistance of the Wither Builder and fixed and edge case where it could place the skulls even if the full structure wasn't present
+ Added a Fluid Pump

# 1.5.5 
+ Removed the farmland part of the Hydrator to fix [#88](https://github.com/Buuz135/Industrial-Foregoing/issues/88) but increased the range by 1
+ Added another check to fix #87 again. Maybe?

# 1.5.4
+ Fixed [#87](https://github.com/Buuz135/Industrial-Foregoing/issues/87)
+ Added some safety checks for the BHU and BHT

# 1.5.3
+ Added some changes to be able to pass information from the Gatherer to the Recollectable
+ Fixed [#83](https://github.com/Buuz135/Industrial-Foregoing/issues/83)
+ Added Leaf Shearing Addon for the Plant Gatherer
+ Fixed [#81](https://github.com/Buuz135/Industrial-Foregoing/issues/81)
+ Fixed [#85](https://github.com/Buuz135/Industrial-Foregoing/issues/85)
+ Added Wither Builder

# 1.5.2
+ Fixed dirt to diamonds
+ Updated zh_CN

# 1.5.1
+ Updated TCL Version fixing some GUI issues like [#74](https://github.com/Buuz135/Industrial-Foregoing/issues/74)
+ Added ExNihilo Creatio dust to the StoneWorks

# 1.5
+ Try to fix [#71](https://github.com/Buuz135/Industrial-Foregoing/issues/71) as it is hard to track
+ Fixed [#70](https://github.com/Buuz135/Industrial-Foregoing/issues/70)
+ Changed `IPlantRecollectable` to `PlantRecollectable` and changed to a IForgeRegistry
+ Added Protein Reactor with JEI Handler and Craftweaker support.
+ Added Protein Generator that produces 320RF/t with protein.
+ Sneak+Right Click a IF Block with the manual in the hand will open the manual entry of the block directly.
+ Fixed BHUC not being able to insert into a BHU if it was empty.
+ Added Hydrator Block to help plant growth.
+ Fixed Item Splitter voiding items when connected to inventories that aren't simple.
+ Added JEI Handler for the Manual entries.

# 1.4.3
+ Fixed [#65](https://github.com/Buuz135/Industrial-Foregoing/issues/65)
+ Added polish localization.  Thanks to @Kaperios
+ Fixed [#66](https://github.com/Buuz135/Industrial-Foregoing/issues/66)
+ Fixed [#68](https://github.com/Buuz135/Industrial-Foregoing/issues/68)
+ Fixed dupe bug with shulkerboxes and not keeping their custom name
+ Added Item Splitter
+ Spoopyfied the month for contributors

# 1.4.2
+ Fixed an issue where some machines would crash if you tried to insert fluid into them when you weren't supposed to.
+ Added some ores to the Laser Drill: Aluminum, Nickel, Draconium, Yellorium, Cobalt, Osmium and Ardite.

# 1.4.1
+ Fixed some book stuff
+ Fix [#57](https://github.com/Buuz135/Industrial-Foregoing/issues/57)

# 1.4
+ Fix [#54](https://github.com/Buuz135/Industrial-Foregoing/issues/54)
+ Better GUI Syncing for some machines
+ Added Energy Field Addon and Energy Field Provider to wireless power machines. (Place an Energy Field Provider and give it power, Get an Energy Field Addon Shift+Right Click to the Provider Block and add it to another machine in the range of the Energy Field Provider)
+ Fixed Bioreactor not filtering the slots
+ Added OreDictionary Converter to convert between different types of OreDictionary Entries. Supported types: "ore", "ingot", "nugget", "gem", "dust", "block", "gear" and "plate"
+ Fixed Villager Trade Exchanger not syncing to server. Closes [#55](https://github.com/Buuz135/Industrial-Foregoing/issues/55)
+ Added a manual and documentation 

# 1.3.1
+ Changed some names
+ Improved ItemStack handling of the Black Hole Unit a lot. Closes [#51](https://github.com/Buuz135/Industrial-Foregoing/issues/51)
+ Fixed Resourceful furnace not outputing fluids
+ Improved ItemStack handling of the Black Hole Unit Controller

# 1.3
+ Added a Machine Produce JEI Handler that shows what produces some of the machines
+ Added a Villager Trade Exchanger that trades with villagers automatically
+ Added JEI Handler to show how much power it would produce something in the Petrified Fuel Generator
+ Added Pink Slime Entity
+ Fixed [#47](https://github.com/Buuz135/Industrial-Foregoing/issues/47), [#45](https:/github.com/Buuz135/Industrial-Foregoing/issues/45), [#19](https://github.com/Buuz135/Industrial-Foregoing/issues/19), [#40](https://github.com/Buuz135/Industrial-Foregoing/issues/40), [#43](https://github.com/Buuz135/Industrial-Foregoing/issues/43)
+ Improved Block break rendering for the Tree Fluid Extractor and now its shared between blocks
+ Added Pink Slime Creation in the Mob Slaughter Factory
+ Added Inverted Lens that will decrease the chance of an item spawning in the Laser Drill
+ Changed The Straw Registry By: Coded
+ Added JEI button to the machines
+ Made the Laser Drill a bit more expensive
+ Made Non Electric machines more abstract to open it's sideness easier

# 1.2.1
+ Fixed issue [#37](https://github.com/Buuz135/Industrial-Foregoing/issues/37)
+ Removed crafttweaker loading dep to fix [#38](https://github.com/Buuz135/Industrial-Foregoing/issues/38)
+ Reverted Plastic Oredictionary in recipes

# 1.2
+ Added Resourceful Furnace, it produces Essence when doing furnace operations.
+ Made all inputs of the machine enabled by default.
+ Fixed issue [#31](https://github.com/Buuz135/Industrial-Foregoing/issues/31) 
  1) OreDicted Recipes that Contained Plastic.
  2) Fixed Space Checking for the entities when they were spawned.
  4) Added some extra space for the BHU when empty.
  5) Added a config option for the Mob Duplicator to increase essence used based con health (Default 12).
+ Removed some GUI pieces that weren't needed.
+ Added support for Forestry fertilizer in Crop Enrich Material Injector (Closes [#33](https://github.com/Buuz135/Industrial-Foregoing/issues/33))
+ Improved JEI Plugins
+ Fixed Plant Sower Filter Colors
+ Fixed Bioreactor allowing more of the same type of input
+ Added API Support for the BioReactor (Removed Config support for the entries)
+ Added API Support for the Laser Drill (Removed Config support for the entries)
+ Added Support for the BioReactor in Crafttweaker
   ```
   import mods.industrialforegoing.BioReactor;
   
   BioReactor.add(<minecraft:diamond>);
   ```
+ Added Support for the Laser Drill in Crafttweaker
  ```
  import mods.industrialforegoing.LaserDrill;
  
  LaserDrill.add(1, <minecraft:stone>, 10); // Arguments: (LaserItemMetadata, ItemStack, ItemWeight)
  ```
+ Added a PlantRecollector API to add custom ways to harvest plants.
+ Reduced PlantRecollector and sower waiting time. (Fixes #34)
+ Changed Block Placer to use a FakePlayer to place the block.
+ Added Support for the Sludge Refiner in Crafttweaker
  ```
  import mods.industrialforegoing.SludgeRefiner;
  
  SludgeRefiner.add(<minecraft:wheat_seeds>, 10); //Arguments: (ItemStack, ItemWeight)
  ```
+ Fixed some wierdness with creative tabs (Closes #36)

# 1.1
+ Added Black Hole Tank
+ Decreased slot stack size limit to 16 for the Plant Sower
+ Added a Contributor Reward as a Patreon Reward http://i.imgur.com/ReDZDWP.png https://www.patreon.com/buuz135
+ Added support for pumpkins and melon for the Plant Recollector