package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.tile.BlackHoleUnitTile;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class BlackHoleUnitBlock extends CustomOrientedBlock<BlackHoleUnitTile> {

    public BlackHoleUnitBlock() {
        super("black_hole_unit", BlackHoleUnitTile.class, Material.ROCK);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (world.getTileEntity(pos) instanceof BlackHoleUnitTile) {
            BlackHoleUnitTile tile = (BlackHoleUnitTile) world.getTileEntity(pos);
            ItemStack stack = new ItemStack(Item.getItemFromBlock(this), 1);
            if (tile.getAmount() > 0) {
                if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
                stack.getTagCompound().setInteger(BlackHoleUnitTile.NBT_AMOUNT, tile.getAmount());
                stack.getTagCompound().setString(BlackHoleUnitTile.NBT_ITEMSTACK, tile.getStack().getItem().getRegistryName().toString());
                stack.getTagCompound().setInteger(BlackHoleUnitTile.NBT_META, tile.getStack().getMetadata());
            }
            float f = 0.7F;
            float d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5F;
            float d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5F;
            float d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5F;
            EntityItem entityitem = new EntityItem(world, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, stack);
            entityitem.setDefaultPickupDelay();
            if (stack.hasTagCompound()) {
                entityitem.getEntityItem().setTagCompound(stack.getTagCompound().copy());
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
        if (stack.hasTagCompound() && world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof BlackHoleUnitTile) {
            BlackHoleUnitTile tile = (BlackHoleUnitTile) world.getTileEntity(pos);
            if (stack.getTagCompound().hasKey(BlackHoleUnitTile.NBT_ITEMSTACK) && stack.getTagCompound().hasKey(BlackHoleUnitTile.NBT_META)) {
                tile.setStack(new ItemStack(Item.getByNameOrId(stack.getTagCompound().getString(BlackHoleUnitTile.NBT_ITEMSTACK)), 1, stack.getTagCompound().getInteger(BlackHoleUnitTile.NBT_META)));
            }
            if (stack.getTagCompound().hasKey(BlackHoleUnitTile.NBT_AMOUNT))
                tile.setAmount(stack.getTagCompound().getInteger(BlackHoleUnitTile.NBT_AMOUNT));
        }
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        if (stack.hasTagCompound()) {
            if (stack.getTagCompound().hasKey(BlackHoleUnitTile.NBT_ITEMSTACK) && stack.getTagCompound().hasKey(BlackHoleUnitTile.NBT_META)) {
                tooltip.add(new TextComponentTranslation("text.display.block").getUnformattedText() + " " + new TextComponentTranslation(new ItemStack(Item.getByNameOrId(stack.getTagCompound().getString(BlackHoleUnitTile.NBT_ITEMSTACK)), 1, stack.getTagCompound().getInteger(BlackHoleUnitTile.NBT_META)).getUnlocalizedName() + ".name").getUnformattedText());
            }
            if (stack.getTagCompound().hasKey(BlackHoleUnitTile.NBT_AMOUNT))
                tooltip.add(new TextComponentTranslation("text.display.amount").getUnformattedText() + " " + stack.getTagCompound().getInteger(BlackHoleUnitTile.NBT_AMOUNT));
        }

        tooltip.add("\"the BHU\"");
    }
}
