#1.3
+ Added a Machine Produce JEI Handler that shows what produces some of the machines
+ Added a Villager Trade Exchanger that trades with villagers automatically
+ Added JEI Handler to show how much power it would produce something in the Petrified Fuel Generator
+ Added Pink Slime Entity
+ Fixed [#47](https://github.com/Buuz135/Industrial-Foregoing/issues/47), [#45](https:/github.com/Buuz135/Industrial-Foregoing/issues/45), [#19](https://github.com/Buuz135/Industrial-Foregoing/issues/19), [#40](https://github.com/Buuz135/Industrial-Foregoing/issues/40), [#43](https://github.com/Buuz135/Industrial-Foregoing/issues/43)
+ Improved Block break rendering for the Tree Fluid Extractor and now its shared between blocks
+ Added Pink Slime Creation in the Mob Slaughter Factory

#1.2.1
+ Fixed issue [#37](https://github.com/Buuz135/Industrial-Foregoing/issues/37)
+ Removed crafttweaker loading dep to fix [#38](https://github.com/Buuz135/Industrial-Foregoing/issues/38)
+ Reverted Plastic Oredictionary in recipes

#1.2
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

#1.1
+ Added Black Hole Tank
+ Decreased slot stack size limit to 16 for the Plant Sower
+ Added a Contributor Reward as a Patreon Reward http://i.imgur.com/ReDZDWP.png https://www.patreon.com/buuz135
+ Added support for pumpkins and melon for the Plant Recollector