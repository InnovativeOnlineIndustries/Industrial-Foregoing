package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.api.book.IPage;
import com.buuz135.industrial.api.book.page.PageText;
import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.misc.BlackHoleTankTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.ndrei.teslacorelib.items.MachineCaseItem;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class BlackHoleTankBlock extends CustomOrientedBlock<BlackHoleTankTile> {

    public BlackHoleTankBlock() {
        super("black_hole_tank", BlackHoleTankTile.class);
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "ppp", "eae", "cmc",
                'p', ItemRegistry.plastic,
                'e', Items.ENDER_EYE,
                'a', Items.ENDER_PEARL,
                'c', Items.BUCKET,
                'm', MachineCaseItem.INSTANCE);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (world.getTileEntity(pos) instanceof BlackHoleTankTile) {
            BlackHoleTankTile tile = (BlackHoleTankTile) world.getTileEntity(pos);
            ItemStack stack = new ItemStack(Item.getItemFromBlock(this), 1);
            if (tile.getAmount() > 0) {
                if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
                if (tile.getTank().getFluid() != null) {
                    stack.setTagCompound(tile.getTank().getFluid().writeToNBT(new NBTTagCompound()));
                }
            }
            float f = 0.7F;
            float d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5F;
            float d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5F;
            float d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5F;
            EntityItem entityitem = new EntityItem(world, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, stack);
            entityitem.setDefaultPickupDelay();
            if (stack.hasTagCompound()) {
                entityitem.getItem().setTagCompound(stack.getTagCompound().copy());
            }
            world.spawnEntity(entityitem);
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        return Arrays.asList();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        if (stack.hasTagCompound() && world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof BlackHoleTankTile && FluidRegistry.isFluidRegistered(stack.getTagCompound().getString("FluidName"))) {
            BlackHoleTankTile tile = (BlackHoleTankTile) world.getTileEntity(pos);
            tile.getTank().fill(new FluidStack(FluidRegistry.getFluid(stack.getTagCompound().getString("FluidName")), stack.getTagCompound().getInteger("Amount")), true);
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        if (stack.hasTagCompound() && FluidRegistry.isFluidRegistered(stack.getTagCompound().getString("FluidName"))) {
            tooltip.add(new TextComponentTranslation("text.display.fluid").getUnformattedText() + " " + new TextComponentTranslation(FluidRegistry.getFluid(stack.getTagCompound().getString("FluidName")).getUnlocalizedName()).getUnformattedText());
            tooltip.add(new TextComponentTranslation("text.display.amount").getUnformattedText() + " " + stack.getTagCompound().getInteger("Amount"));
        }
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.STORAGE;
    }

    @Override
    public List<IPage> getBookDescriptionPages() {
        List<IPage> pages = super.getBookDescriptionPages();
        pages.add(0, new PageText("It can can hold up to " + PageText.bold(new DecimalFormat().format(Integer.MAX_VALUE)) + "mb of one fluid."));
        return pages;
    }
}
