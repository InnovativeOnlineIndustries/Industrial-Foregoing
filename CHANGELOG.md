# 1.5
+ Try to fix [#71](https://github.com/Buuz135/Industrial-Foregoing/issues/71) as it is hard to track
+ Fixed [#70](https://github.com/Buuz135/Industrial-Foregoing/issues/70
+ Changed `IPlantRecollectable` to `PlantRecollectable` and changed to a IForgeRegistry )
+ Added Protein Reactor with JEI Handler and Craftweaker support.

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