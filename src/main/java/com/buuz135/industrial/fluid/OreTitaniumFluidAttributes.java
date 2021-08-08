package com.buuz135.industrial.fluid;

import com.buuz135.industrial.utils.ItemStackUtils;
import com.hrznstudio.titanium.util.TagUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class OreTitaniumFluidAttributes extends FluidAttributes {

    public static final String NBT_TAG = "Tag";

    public OreTitaniumFluidAttributes(Builder builder, Fluid fluid) {
        super(builder, fluid);
    }

    @Override
    public int getColor(FluidStack stack) {
        if (Minecraft.getInstance().world != null && stack.hasTag() && stack.getTag().contains(NBT_TAG)){
            String tag = stack.getTag().getString(NBT_TAG);
            List<Item> items = TagCollectionManager.getManager().getItemTags().getTagByID(new ResourceLocation(tag.replace("forge:ores/", "forge:dusts/"))).getAllElements();
            if (items.size() > 0){
                return ItemStackUtils.getColor(new ItemStack(items.get(0)));
            }
        }
        return super.getColor(stack);
    }

    @Override
    public String getTranslationKey(FluidStack stack) {
        String extra = "";
        if (stack.hasTag() && stack.getTag().contains(NBT_TAG)){
            String tag = stack.getTag().getString(NBT_TAG);
            List<Item> items = TagCollectionManager.getManager().getItemTags().getTagByID(new ResourceLocation(tag)).getAllElements();
            if (items.size() > 0){
                extra = " (" + new TranslationTextComponent(items.get(0).getTranslationKey()).getString() + ")";
            }
        }
        return new TranslationTextComponent(super.getTranslationKey(stack)).getString() + extra;
    }

    @Override
    public ITextComponent getDisplayName(FluidStack stack) {
        String extra = "";
        if (stack.hasTag() && stack.getTag().contains(NBT_TAG)){
            String tag = stack.getTag().getString(NBT_TAG);
            List<Item> items = TagCollectionManager.getManager().getItemTags().getTagByID(new ResourceLocation(tag)).getAllElements();
            if (items.size() > 0){
                extra = " (" + new TranslationTextComponent(items.get(0).getTranslationKey()).getString() + ")";
            }
        }
        return new StringTextComponent(super.getDisplayName(stack).getString() + extra);
    }

    public static FluidStack getFluidWithTag(OreFluidInstance fluidInstance, int amount, ResourceLocation itemITag){
        FluidStack stack = new FluidStack(fluidInstance.getSourceFluid(), amount);
        stack.getOrCreateTag().putString(NBT_TAG, itemITag.toString());
        return stack;
    }

    public static String getFluidTag(FluidStack stack){
        return stack.getOrCreateTag().getString(NBT_TAG);
    }

    public static boolean isValid(ResourceLocation resourceLocation){
        return TagCollectionManager.getManager().getItemTags().getRegisteredTags().contains(new ResourceLocation(resourceLocation.toString().replace("forge:ores/", "forge:dusts/"))) && !TagCollectionManager.getManager().getItemTags().get(new ResourceLocation(resourceLocation.toString().replace("forge:ores/", "forge:dusts/"))).getAllElements().isEmpty() && !TagCollectionManager.getManager().getItemTags().get(resourceLocation).getAllElements().isEmpty();
    }

    public static ItemStack getOutputDust(FluidStack stack){
        String tag = getFluidTag(stack);
        return TagUtil.getItemWithPreference(TagCollectionManager.getManager().getItemTags().get(new ResourceLocation(tag.replace("forge:ores/", "forge:dusts/"))));
    }

    @Override
    public ItemStack getBucket(FluidStack stack) {
        ItemStack bucket = super.getBucket(stack);
        if(stack.hasTag() && stack.getTag().contains(NBT_TAG)) {
            String tag = stack.getTag().getString(NBT_TAG);
            bucket.getOrCreateTag().putString(NBT_TAG, tag);
        }
        return bucket;
    }
}
