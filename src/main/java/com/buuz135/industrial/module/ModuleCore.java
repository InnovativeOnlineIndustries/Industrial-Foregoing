package com.buuz135.industrial.module;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.block.MachineFrameBlock;
import com.buuz135.industrial.item.BookManualItem;
import com.buuz135.industrial.item.FertilizerItem;
import com.buuz135.industrial.item.IFCustomItem;
import com.buuz135.industrial.item.ItemStraw;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.fluid.TitaniumFluidInstance;
import com.hrznstudio.titanium.module.Feature;
import com.hrznstudio.titanium.recipe.generator.FurnaceJsonData;
import com.hrznstudio.titanium.recipe.generator.IIngredient;
import com.hrznstudio.titanium.tab.AdvancedTitaniumTab;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fluids.FluidAttributes;

import java.util.ArrayList;
import java.util.List;

public class ModuleCore implements IModule {

    public static AdvancedTitaniumTab TAB_CORE = new AdvancedTitaniumTab(Reference.MOD_ID + "_core", true);

    public static IFCustomItem TINY_DRY_RUBBER = new IFCustomItem("tinydryrubber", TAB_CORE, null);
    public static IFCustomItem DRY_RUBBER = new IFCustomItem("dryrubber", TAB_CORE, null);
    public static IFCustomItem PLASTIC = new IFCustomItem("plastic", TAB_CORE, registry -> IndustrialForegoing.RECIPES.addRecipe(FurnaceJsonData.of(IIngredient.ItemStackIngredient.of(new ItemStack(Items.GREEN_DYE)), new ItemStack(ModuleCore.PLASTIC), 0.35, 300)));
    public static FertilizerItem FERTILIZER = new FertilizerItem(TAB_CORE);
    public static IFCustomItem PINK_SLIME = new IFCustomItem("pink_slime", TAB_CORE, null);
    public static BookManualItem BOOK_MANUAL = new BookManualItem(TAB_CORE);
    public static IFCustomItem PINK_SLIME_INGOT = new IFCustomItem("pink_slime_ingot", TAB_CORE, null);
    public static ItemStraw STRAW = new ItemStraw(TAB_CORE);
    public static MachineFrameBlock PITY = new MachineFrameBlock("pity", MachineFrameBlock.PITY_RARITY, TAB_CORE);
    public static MachineFrameBlock SIMPLE = new MachineFrameBlock("simple", MachineFrameBlock.SIMPLE_RARITY, TAB_CORE);
    public static MachineFrameBlock ADVANCED = new MachineFrameBlock("advanced", MachineFrameBlock.ADVANCED_RARITY, TAB_CORE);
    public static MachineFrameBlock SUPREME = new MachineFrameBlock("supreme", MachineFrameBlock.SUPREME_RARITY, TAB_CORE);
    public static TitaniumFluidInstance LATEX = new TitaniumFluidInstance(Reference.MOD_ID, "latex", FluidAttributes.builder("latex", new ResourceLocation(Reference.MOD_ID, "blocks/fluids/latex_still"), new ResourceLocation(Reference.MOD_ID, "blocks/fluids/latex_flow")).build(), true, TAB_CORE);

    @Override
    public List<Feature.Builder> generateFeatures() {
        List<Feature.Builder> features = new ArrayList<>();
        features.add(Feature.builder("plastic").
                content(Item.class, TINY_DRY_RUBBER).
                content(Item.class, DRY_RUBBER).
                content(Item.class, PLASTIC).
                content(TitaniumFluidInstance.class, LATEX).
                eventClient(() -> () -> EventManager.mod(TextureStitchEvent.Pre.class).process(this::textureStitch)));
        features.add(Feature.builder("pink_slime").
                content(Item.class, PINK_SLIME).
                content(Item.class, PINK_SLIME_INGOT));
        features.add(Feature.builder("fertilizer").
                content(Item.class, FERTILIZER));
        features.add(Feature.builder("straw").
                content(Item.class, STRAW));
        features.add(Feature.builder("machine_frames").
                content(Block.class, SIMPLE).
                content(Block.class, ADVANCED).
                content(Block.class, SUPREME).
                content(Block.class, PITY));
        TAB_CORE.addIconStack(new ItemStack(PLASTIC));
        return features;
    }

    @OnlyIn(Dist.CLIENT)
    public void textureStitch(TextureStitchEvent.Pre event) {
        event.addSprite(LATEX.getSourceFluid().getAttributes().getFlowingTexture());
        event.addSprite(LATEX.getSourceFluid().getAttributes().getStillTexture());
    }
}
