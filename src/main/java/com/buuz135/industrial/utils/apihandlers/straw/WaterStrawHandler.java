package com.buuz135.industrial.utils.apihandlers.straw;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class WaterStrawHandler extends StrawHandlerBase {

    public WaterStrawHandler() {
        super("water");
        setRegistryName("water");
    }

    @Override
    public void onDrink(World world, BlockPos pos, FluidStack stack, EntityPlayer player, boolean fromFluidContainer) {
        player.extinguish();
        NBTTagCompound tag = player.getEntityData();
        if (tag.hasKey("lavaDrink") && world.getTotalWorldTime() - tag.getLong("lavaDrink") < 120) { //6 Seconds to drink water after drinking lava to create obsidian
            player.entityDropItem(new ItemStack(Blocks.OBSIDIAN), player.getEyeHeight());

            tag.setLong("lavaDrink", 0);
            world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 1.5F, world.rand.nextFloat() * 0.1F + 0.9F);
        }
    }

}