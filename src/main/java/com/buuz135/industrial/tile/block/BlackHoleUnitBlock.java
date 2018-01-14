package com.buuz135.industrial.tile.block;

import com.buuz135.industrial.book.BookCategory;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.tile.misc.BlackHoleUnitTile;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
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
import net.ndrei.teslacorelib.items.MachineCaseItem;

import java.util.Arrays;
import java.util.List;

public class BlackHoleUnitBlock extends CustomOrientedBlock<BlackHoleUnitTile> {

    public BlackHoleUnitBlock() {
        super("black_hole_unit", BlackHoleUnitTile.class, Material.ROCK, 0, 0);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (world.getTileEntity(pos) instanceof BlackHoleUnitTile) {
            BlackHoleUnitTile tile = (BlackHoleUnitTile) world.getTileEntity(pos);
            ItemStack stack = new ItemStack(Item.getItemFromBlock(this), 1);
            if (tile.getAmount() > 0) {
                if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
                stack.getTagCompound().setInteger(BlackHoleUnitTile.NBT_AMOUNT, tile.getAmount());
                stack.getTagCompound().setString(BlackHoleUnitTile.NBT_ITEMSTACK, tile.getItemStack().getItem().getRegistryName().toString());
                stack.getTagCompound().setInteger(BlackHoleUnitTile.NBT_META, tile.getItemStack().getMetadata());
                if (tile.getItemStack().hasTagCompound())
                    stack.getTagCompound().setTag(BlackHoleUnitTile.NBT_ITEM_NBT, tile.getItemStack().getTagCompound());
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
        world.removeTileEntity(pos);
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        return Arrays.asList();
    }


    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        if (stack.hasTagCompound() && world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof BlackHoleUnitTile && Item.getByNameOrId(stack.getTagCompound().getString(BlackHoleUnitTile.NBT_ITEMSTACK)) != null) {
            BlackHoleUnitTile tile = (BlackHoleUnitTile) world.getTileEntity(pos);
            if (stack.getTagCompound().hasKey(BlackHoleUnitTile.NBT_ITEMSTACK) && stack.getTagCompound().hasKey(BlackHoleUnitTile.NBT_META)) {
                ItemStack item = new ItemStack(Item.getByNameOrId(stack.getTagCompound().getString(BlackHoleUnitTile.NBT_ITEMSTACK)), 1, stack.getTagCompound().getInteger(BlackHoleUnitTile.NBT_META));
                if (stack.getTagCompound().hasKey(BlackHoleUnitTile.NBT_ITEM_NBT))
                    item.setTagCompound(stack.getTagCompound().getCompoundTag(BlackHoleUnitTile.NBT_ITEM_NBT));
                tile.setStack(item);
            }
            if (stack.getTagCompound().hasKey(BlackHoleUnitTile.NBT_AMOUNT))
                tile.setAmount(stack.getTagCompound().getInteger(BlackHoleUnitTile.NBT_AMOUNT));
        }
    }

    @Override
    public List<String> getTooltip(ItemStack stack) {
        List<String> tooltip = super.getTooltip(stack);
        if (stack.hasTagCompound() && Item.getByNameOrId(stack.getTagCompound().getString(BlackHoleUnitTile.NBT_ITEMSTACK)) != null) {
            if (stack.getTagCompound().hasKey(BlackHoleUnitTile.NBT_ITEMSTACK) && stack.getTagCompound().hasKey(BlackHoleUnitTile.NBT_META)) {
                tooltip.add(new TextComponentTranslation("text.industrialforegoing.display.item").getUnformattedText() + " " + new TextComponentTranslation(new ItemStack(Item.getByNameOrId(stack.getTagCompound().getString(BlackHoleUnitTile.NBT_ITEMSTACK)), 1, stack.getTagCompound().getInteger(BlackHoleUnitTile.NBT_META)).getUnlocalizedName() + ".name").getUnformattedText());
            }
            if (stack.getTagCompound().hasKey(BlackHoleUnitTile.NBT_AMOUNT))
                tooltip.add(new TextComponentTranslation("text.industrialforegoing.display.amount").getUnformattedText() + " " + stack.getTagCompound().getInteger(BlackHoleUnitTile.NBT_AMOUNT));
        }
        return tooltip;
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), "ppp", "eae", "cmc",
                'p', ItemRegistry.plastic,
                'e', Items.ENDER_EYE,
                'a', Items.ENDER_PEARL,
                'c', "chestWood",
                'm', MachineCaseItem.INSTANCE);
    }

    public ItemStack getItemStack(ItemStack blackHole) {
        NBTTagCompound compound = blackHole.getTagCompound();
        ItemStack stack = ItemStack.EMPTY;
        if (compound == null || !compound.hasKey(BlackHoleUnitTile.NBT_ITEMSTACK)) return stack;
        Item item = Item.getByNameOrId(compound.getString(BlackHoleUnitTile.NBT_ITEMSTACK));
        if (item != null) {
            stack = new ItemStack(item, 1, compound.hasKey(BlackHoleUnitTile.NBT_META) ? compound.getInteger(BlackHoleUnitTile.NBT_META) : 0);
            if (compound.hasKey(BlackHoleUnitTile.NBT_ITEM_NBT))
                stack.setTagCompound(compound.getCompoundTag(BlackHoleUnitTile.NBT_ITEM_NBT));
        }
        return stack;
    }

    public int getAmount(ItemStack blackHole) {
        NBTTagCompound compound = blackHole.getTagCompound();
        int amount = 0;
        if (compound != null && compound.hasKey(BlackHoleUnitTile.NBT_AMOUNT)) {
            amount = compound.getInteger(BlackHoleUnitTile.NBT_AMOUNT);
        }
        return amount;
    }

    public void setAmount(ItemStack blackHole, int amount) {
        NBTTagCompound compound = blackHole.getTagCompound();
        if (compound != null) {
            compound.setInteger(BlackHoleUnitTile.NBT_AMOUNT, amount);
        }
    }

    public void setItemStack(ItemStack hole, ItemStack item) {
        if (!hole.hasTagCompound()) hole.setTagCompound(new NBTTagCompound());
        hole.getTagCompound().setString(BlackHoleUnitTile.NBT_ITEMSTACK, item.getItem().getRegistryName().toString());
        hole.getTagCompound().setInteger(BlackHoleUnitTile.NBT_META, item.getMetadata());
        if (item.hasTagCompound())
            hole.getTagCompound().setTag(BlackHoleUnitTile.NBT_ITEM_NBT, item.getTagCompound());
    }

    @Override
    public BookCategory getCategory() {
        return BookCategory.STORAGE;
    }

}
