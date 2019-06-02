package com.buuz135.industrial.module;

import com.buuz135.industrial.api.conveyor.ConveyorUpgradeFactory;
import com.buuz135.industrial.item.ItemConveyorUpgrade;
import com.buuz135.industrial.proxy.block.BlockConveyor;
import com.buuz135.industrial.proxy.block.upgrade.*;
import com.hrznstudio.titanium.module.Feature;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class ModuleTransport implements IModule {

    public static ConveyorUpgradeFactory upgrade_extraction = new ConveyorExtractionUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_insertion = new ConveyorInsertionUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_detector = new ConveyorDetectorUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_bouncing = new ConveyorBouncingUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_dropping = new ConveyorDroppingUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_blinking = new ConveyorBlinkingUpgrade.Factory();
    public static ConveyorUpgradeFactory upgrade_splitting = new ConveyorSplittingUpgrade.Factory();

    public static BlockConveyor CONVEYOR;

    @Override
    public List<Feature.Builder> generateFeatures() {
        List<Feature.Builder> features = new ArrayList<>();
        features.add(Feature.builder("conveyor").content(Block.class, CONVEYOR = new BlockConveyor()));
        Feature.Builder builder = Feature.builder("conveyor_upgrades");
        ConveyorUpgradeFactory.FACTORIES.forEach(conveyorUpgradeFactory -> builder.content(Item.class, new ItemConveyorUpgrade(conveyorUpgradeFactory)));
        features.add(builder);
        return features;
    }
}
