package com.buuz135.industrial.module;

import com.buuz135.industrial.item.BookManualItem;
import com.buuz135.industrial.item.FertilizerItem;
import com.buuz135.industrial.item.IFCustomItem;
import com.buuz135.industrial.item.ItemStraw;
import com.hrznstudio.titanium.module.Feature;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class ModuleCore implements IModule {

    public static IFCustomItem TINY_DRY_RUBBER = new IFCustomItem("tinydryrubber");
    public static IFCustomItem DRY_RUBBER = new IFCustomItem("dryrubber");
    public static IFCustomItem PLASTIC = new IFCustomItem("plastic");
    public static FertilizerItem FERTILIZER = new FertilizerItem();
    public static IFCustomItem PINK_SLIME = new IFCustomItem("pink_slime");
    public static BookManualItem BOOK_MANUAL = new BookManualItem();
    public static IFCustomItem PINK_SLIME_INGOT = new IFCustomItem("pink_slime_ingot");
    public static ItemStraw STRAW = new ItemStraw();

    @Override
    public List<Feature.Builder> generateFeatures() {
        List<Feature.Builder> features = new ArrayList<>();
        features.add(Feature.builder("plastic").
                content(Item.class, TINY_DRY_RUBBER).
                content(Item.class, DRY_RUBBER).
                content(Item.class, PLASTIC));
        features.add(Feature.builder("pink_slime").
                content(Item.class, PINK_SLIME).
                content(Item.class, PINK_SLIME_INGOT));
        features.add(Feature.builder("fertilizer").
                content(Item.class, FERTILIZER));
        features.add(Feature.builder("straw").
                content(Item.class, STRAW));
        return features;
    }
}
