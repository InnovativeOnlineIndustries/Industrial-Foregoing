package com.buuz135.industrial.module;

import com.buuz135.industrial.block.MachineFrameBlock;
import com.buuz135.industrial.block.core.DissolutionChamberBlock;
import com.buuz135.industrial.block.core.FluidExtractorBlock;
import com.buuz135.industrial.block.core.LatexProcessingUnitBlock;
import com.buuz135.industrial.block.core.tile.FluidExtractorTile;
import com.buuz135.industrial.item.*;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.fluid.TitaniumFluidInstance;
import com.hrznstudio.titanium.module.Feature;
import com.hrznstudio.titanium.tab.AdvancedTitaniumTab;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fluids.FluidAttributes;

import java.util.ArrayList;
import java.util.List;

public class ModuleCore implements IModule {

    public static AdvancedTitaniumTab TAB_CORE = new AdvancedTitaniumTab(Reference.MOD_ID + "_core", true);

    public static IFCustomItem TINY_DRY_RUBBER = new RecipelessCustomItem("tinydryrubber", TAB_CORE);
    public static IFCustomItem DRY_RUBBER = new RecipelessCustomItem("dryrubber", TAB_CORE);
    public static IFCustomItem PLASTIC = new RecipelessCustomItem("plastic", TAB_CORE);
    public static FertilizerItem FERTILIZER = new FertilizerItem(TAB_CORE);
    public static IFCustomItem PINK_SLIME = new RecipelessCustomItem("pink_slime", TAB_CORE);
    public static BookManualItem BOOK_MANUAL = new BookManualItem(TAB_CORE);
    public static IFCustomItem PINK_SLIME_INGOT = new RecipelessCustomItem("pink_slime_ingot", TAB_CORE);
    public static ItemStraw STRAW = new ItemStraw(TAB_CORE);
    public static MachineFrameBlock PITY = new MachineFrameBlock("pity", MachineFrameBlock.PITY_RARITY, TAB_CORE);
    public static MachineFrameBlock SIMPLE = new MachineFrameBlock("simple", MachineFrameBlock.SIMPLE_RARITY, TAB_CORE);
    public static MachineFrameBlock ADVANCED = new MachineFrameBlock("advanced", MachineFrameBlock.ADVANCED_RARITY, TAB_CORE);
    public static MachineFrameBlock SUPREME = new MachineFrameBlock("supreme", MachineFrameBlock.SUPREME_RARITY, TAB_CORE);
    public static FluidExtractorBlock FLUID_EXTRACTOR = new FluidExtractorBlock(TAB_CORE);
    public static LatexProcessingUnitBlock LATEX_PROCESSING = new LatexProcessingUnitBlock(TAB_CORE);
    public static DissolutionChamberBlock DISSOLUTION_CHAMBER = new DissolutionChamberBlock(TAB_CORE);
    public static RangeAddonItem[] RANGE_ADDONS = new RangeAddonItem[12];

    public static TitaniumFluidInstance LATEX = new TitaniumFluidInstance(Reference.MOD_ID, "latex", FluidAttributes.builder(new ResourceLocation(Reference.MOD_ID, "blocks/fluids/latex_still"), new ResourceLocation(Reference.MOD_ID, "blocks/fluids/latex_flow")), true, TAB_CORE);
    public static TitaniumFluidInstance MEAT = new TitaniumFluidInstance(Reference.MOD_ID, "meat", FluidAttributes.builder(new ResourceLocation(Reference.MOD_ID, "blocks/fluids/meat_still"), new ResourceLocation(Reference.MOD_ID, "blocks/fluids/meat_flow")), true, TAB_CORE);
    public static TitaniumFluidInstance SEWAGE = new TitaniumFluidInstance(Reference.MOD_ID, "sewage", FluidAttributes.builder(new ResourceLocation(Reference.MOD_ID, "blocks/fluids/sewage_still"), new ResourceLocation(Reference.MOD_ID, "blocks/fluids/sewage_flow")), true, TAB_CORE);

    @Override
    public List<Feature.Builder> generateFeatures() {
        List<Feature.Builder> features = new ArrayList<>();
        features.add(Feature.builder("plastic").
                content(Item.class, TINY_DRY_RUBBER).
                content(Item.class, DRY_RUBBER).
                content(Item.class, PLASTIC).
                content(TitaniumFluidInstance.class, LATEX).
                eventClient(() -> () -> EventManager.mod(TextureStitchEvent.Pre.class).process(this::textureStitch)));
        features.add(Feature.builder("plastic_generation").
                content(Block.class, FLUID_EXTRACTOR).
                content(Block.class, LATEX_PROCESSING).
                event(EventManager.forge(TickEvent.WorldTickEvent.class).
                        filter(worldTickEvent -> worldTickEvent.phase == TickEvent.Phase.END && worldTickEvent.type == TickEvent.Type.WORLD && worldTickEvent.world.getGameTime() % 40 == 0 && FluidExtractorTile.EXTRACTION.containsKey(worldTickEvent.world.dimension.getType())).
                        process(worldTickEvent -> FluidExtractorTile.EXTRACTION.get(worldTickEvent.world.dimension.getType()).values().forEach(blockPosFluidExtractionProgressHashMap -> blockPosFluidExtractionProgressHashMap.keySet().forEach(pos -> worldTickEvent.world.sendBlockBreakProgress(blockPosFluidExtractionProgressHashMap.get(pos).getBreakID(), pos, blockPosFluidExtractionProgressHashMap.get(pos).getProgress()))))));
        features.add(Feature.builder("pink_slime").
                content(Item.class, PINK_SLIME).
                content(Item.class, PINK_SLIME_INGOT));
        features.add(Feature.builder("fertilizer").
                content(Item.class, FERTILIZER));
        features.add(Feature.builder("straw").
                content(Item.class, STRAW));
        features.add(Feature.builder("machine_frames").
                content(Block.class, PITY).
                content(Block.class, SIMPLE).
                content(Block.class, ADVANCED).
                content(Block.class, SUPREME));
        features.add(Feature.builder("tier_2_production").
                content(Block.class, DISSOLUTION_CHAMBER));
        Feature.Builder builder = Feature.builder("range_addons");
        for (int i = 0; i < RANGE_ADDONS.length; i++) {
            RANGE_ADDONS[i] = new RangeAddonItem(i, TAB_CORE);
            builder.content(Item.class, RANGE_ADDONS[i]);
        }
        features.add(builder);
        features.add(Feature.builder("meat").
                content(TitaniumFluidInstance.class, MEAT));
        features.add(Feature.builder("sewage").
                content(TitaniumFluidInstance.class, SEWAGE));
        TAB_CORE.addIconStack(new ItemStack(PLASTIC));
        return features;
    }

    @OnlyIn(Dist.CLIENT)
    public void textureStitch(TextureStitchEvent.Pre event) {
        event.addSprite(LATEX.getSourceFluid().getAttributes().getFlowingTexture());
        event.addSprite(LATEX.getSourceFluid().getAttributes().getStillTexture());
    }
}
