/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.misc.BlackHoleTankTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import net.ndrei.teslacorelib.items.MachineCaseItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
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
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {

    }

    @Override
    public void breakBlock(@NotNull World world, @NotNull BlockPos pos, @NotNull IBlockState state) {
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
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        if (stack.hasTagCompound() && world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof BlackHoleTankTile && FluidRegistry.isFluidRegistered(stack.getTagCompound().getString("FluidName"))) {
            BlackHoleTankTile tile = (BlackHoleTankTile) world.getTileEntity(pos);
            tile.getTank().fill(new FluidStack(FluidRegistry.getFluid(stack.getTagCompound().getString("FluidName")), stack.getTagCompound().getInteger("Amount"), stack.getTagCompound().hasKey("Tag") && !stack.getTagCompound().getTag("Tag").isEmpty() ? stack.getTagCompound().getCompoundTag("Tag") : null), true);
        }
    }

    @Override
    public List<String> getTooltip(ItemStack stack) {
        List<String> tooltip = super.getTooltip(stack);
        if (stack.hasTagCompound() && FluidRegistry.isFluidRegistered(stack.getTagCompound().getString("FluidName"))) {
            tooltip.add(new TextComponentTranslation("text.industrialforegoing.display.fluid").getUnformattedText() + " " + new TextComponentTranslation(FluidRegistry.getFluid(stack.getTagCompound().getString("FluidName")).getUnlocalizedName()).getUnformattedText());
            tooltip.add(new TextComponentTranslation("text.industrialforegoing.display.amount").getUnformattedText() + " " + stack.getTagCompound().getInteger("Amount"));
        }
        return tooltip;
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.STORAGE;
    }

    @Override
    public boolean onBlockActivated(@Nullable World worldIn, @Nullable BlockPos pos, @Nullable IBlockState state, @Nullable EntityPlayer playerIn, @Nullable EnumHand hand, @Nullable EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (playerIn != null && hand != null && worldIn != null && pos != null) {
            ItemStack stack = playerIn.getHeldItem(hand);
            if (!stack.isEmpty() && stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null) && worldIn.getTileEntity(pos) instanceof BlackHoleTankTile) {
                BlackHoleTankTile tile = (BlackHoleTankTile) worldIn.getTileEntity(pos);
                FluidActionResult result = FluidUtil.tryFillContainer(stack, (IFluidHandler) tile.getTank(), Integer.MAX_VALUE, playerIn, true);
                if (result.isSuccess()) {
                    stack.shrink(1);
                    if (stack.isEmpty()) {
                        playerIn.setHeldItem(hand, result.result);
                    } else {
                        playerIn.addItemStackToInventory(result.result);
                    }
                    return true;
                }
            }
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    private static final String FLUID_NBT = "FluidName";

    @Override
    public void registerItem(@NotNull IForgeRegistry<Item> registry) {
        registry.register(new BlockTankItem(this).setRegistryName(this.getRegistryName()));
    }

    public class BlockTankItem extends ItemBlock {

        public BlockTankItem(Block block) {
            super(block);
            setMaxStackSize(1);
        }

        @javax.annotation.Nullable
        @Override
        public ICapabilityProvider initCapabilities(ItemStack stack, @javax.annotation.Nullable NBTTagCompound nbt) {
            return new TankCapabilityProvider(stack);
        }
    }

    public class TankCapabilityProvider implements ICapabilityProvider {

        public final ItemStack itemStack;
        public final FluidHandlerItemStack fluidHandlerItemStack;

        public TankCapabilityProvider(ItemStack itemStack) {
            this.itemStack = itemStack;
            this.fluidHandlerItemStack = new FluidHandlerItemStack(this.itemStack, Integer.MAX_VALUE) {
                @javax.annotation.Nullable
                @Override
                public FluidStack getFluid() {
                    NBTTagCompound tagCompound = container.getTagCompound();
                    if (tagCompound == null || !tagCompound.hasKey(FLUID_NBT) || !FluidRegistry.isFluidRegistered(tagCompound.getString(FLUID_NBT))) {
                        return null;
                    }
                    return new FluidStack(FluidRegistry.getFluid(tagCompound.getString(FLUID_NBT)), itemStack.getTagCompound().getInteger("Amount"), itemStack.getTagCompound().hasKey("Tag") && !itemStack.getTagCompound().getTag("Tag").isEmpty() ? itemStack.getTagCompound().getCompoundTag("Tag") : null);
                }

                @Override
                protected void setFluid(FluidStack fluid) {
                    if (!container.hasTagCompound()) {
                        container.setTagCompound(new NBTTagCompound());
                    }
                    NBTTagCompound fluidTag = new NBTTagCompound();
                    fluid.writeToNBT(fluidTag);
                    container.setTagCompound(fluidTag);
                }

                @Override
                protected void setContainerToEmpty() {
                    FluidStack fluid = getFluid();
                    if (fluid == null) container.setTagCompound(null);
                    else setFluid(new FluidStack(fluid.getFluid(), 0));
                }
            };
        }


        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @javax.annotation.Nullable EnumFacing facing) {
            return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
        }

        @javax.annotation.Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @javax.annotation.Nullable EnumFacing facing) {
            if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) return (T) fluidHandlerItemStack;
            return null;
        }
    }
}
