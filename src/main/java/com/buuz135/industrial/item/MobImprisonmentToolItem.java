package com.buuz135.industrial.item;

import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.utils.RecipeUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class MobImprisonmentToolItem extends IFCustomItem {

    public MobImprisonmentToolItem() {
        super("mob_imprisonment_tool");
        setMaxStackSize(1);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.getEntityWorld().isRemote) return EnumActionResult.FAIL;
        if (!containsEntity(stack)) return EnumActionResult.FAIL;
        Entity entity = getEntityFromStack(stack, worldIn, true);
        BlockPos blockPos = pos.offset(facing);
        entity.setPositionAndRotation(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0, 0);
        stack.setTagCompound(new NBTTagCompound());
        player.setHeldItem(hand, stack);
        worldIn.spawnEntity(entity);
        if (entity instanceof EntityLiving) ((EntityLiving) entity).playLivingSound();
        return EnumActionResult.SUCCESS;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
        if (target.getEntityWorld().isRemote) return false;
        if (target instanceof EntityPlayer || !target.isNonBoss()) return false;
        if (containsEntity(stack)) return false;
        String entityID = EntityList.getKey(target).toString();
        if (isBlacklisted(entityID)) return false;
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("entity", entityID);
        nbt.setInteger("id", EntityList.getID(target.getClass()));
        target.writeToNBT(nbt);
        stack.setTagCompound(nbt);
        playerIn.swingArm(hand);
        playerIn.setHeldItem(hand, stack);
        target.setDead();
        return true;
    }


    public boolean isBlacklisted(String entity) {
        return false;
    }

    public boolean containsEntity(ItemStack stack) {
        return !stack.isEmpty() && stack.hasTagCompound() && stack.getTagCompound().hasKey("entity");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (containsEntity(stack)) {
            tooltip.add("Mob: " + new TextComponentTranslation(EntityList.getTranslationName(new ResourceLocation(getID(stack)))).getUnformattedComponentText());
            tooltip.add("Health: " + stack.getTagCompound().getDouble("Health"));
            if (BlockRegistry.mobDuplicatorBlock.blacklistedEntities.contains(stack.getTagCompound().getString("entity")))
                tooltip.add(TextFormatting.RED + "Entity blacklisted in the Mob Duplicator");
        }
    }

    public Entity getEntityFromStack(ItemStack stack, World world, boolean withInfo) {
        Entity entity = EntityList.createEntityByIDFromName(new ResourceLocation(stack.getTagCompound().getString("entity")), world);
        if (withInfo) entity.readFromNBT(stack.getTagCompound());
        return entity;
    }

    public String getID(ItemStack stack) {
        return stack.getTagCompound().getString("entity");
    }

    public void createRecipe() {
        RecipeUtils.addShapedRecipe(new ItemStack(this), " p ", "pgp", " p ", 'p', ItemRegistry.plastic, 'g',
                new ItemStack(Items.GHAST_TEAR));
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if (!containsEntity(stack))
            return new TextComponentTranslation(super.getUnlocalizedName(stack) + ".name").getUnformattedComponentText();
        return new TextComponentTranslation(super.getUnlocalizedName(stack) + ".name").getUnformattedComponentText() + " (" + EntityList.getTranslationName(new ResourceLocation(getID(stack))) + ")";
    }

}
