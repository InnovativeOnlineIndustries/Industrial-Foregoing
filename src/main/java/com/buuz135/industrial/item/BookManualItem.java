package com.buuz135.industrial.item;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.gui.GuiHandler;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class BookManualItem extends IFCustomItem {

    public BookManualItem() {
        super("book_manual");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        BlockPos pos = playerIn.getPosition();
        if (playerIn.isSneaking()) {
            RayTraceResult result = rayTrace(worldIn, playerIn, false);
            if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
                pos = result.getBlockPos();
            }
        }
        playerIn.openGui(IndustrialForegoing.instance, GuiHandler.BOOK, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }


    @Override
    public void createRecipe() {
        RecipeUtils.addShapelessRecipe(new ItemStack(this), Items.PAPER, "logWood");
    }
}
