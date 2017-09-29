package com.buuz135.industrial.item;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.gui.GuiHandler;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class BookManualItem extends IFCustomItem {

    public BookManualItem() {
        super("book_manual");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        playerIn.openGui(IndustrialForegoing.instance, GuiHandler.BOOK, worldIn, playerIn.getPosition().getX(), playerIn.getPosition().getY(), playerIn.getPosition().getZ());
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }


    @Override
    public void createRecipe() {
        RecipeUtils.addShapelessRecipe(new ItemStack(this), Items.PAPER, "logWood");
    }
}
