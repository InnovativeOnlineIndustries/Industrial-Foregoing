package com.buuz135.industrial.utils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class BlockUtils {

    public static List<BlockPos> getBlockPosInAABB(AxisAlignedBB axisAlignedBB) {
        List<BlockPos> blocks = new ArrayList<BlockPos>();
        for (double x = axisAlignedBB.minX; x < axisAlignedBB.maxX; ++x) {
            for (double z = axisAlignedBB.minZ; z < axisAlignedBB.maxZ; ++z) {
                for (double y = axisAlignedBB.minY; y < axisAlignedBB.maxY; ++y) {
                    blocks.add(new BlockPos(x, y, z));
                }
            }
        }
        return blocks;
    }

    public static boolean isBlockOreDict(World world, BlockPos pos, String ore) {
        IBlockState state = world.getBlockState(pos);
        Item item = Item.getItemFromBlock(state.getBlock());
        if (!item.equals(Items.AIR)) {
            ItemStack stack = new ItemStack(item);
            int id = OreDictionary.getOreID(ore);
            for (int i : OreDictionary.getOreIDs(stack)) {
                if (i == id) return true;
            }
        }
        return false;
    }

    public static boolean isLog(World world, BlockPos pos) {
        return isBlockOreDict(world, pos, "logWood") || isBlockOreDict(world, pos, "blockSlimeCongealed");
    }

    public static boolean isLeaves(World world, BlockPos pos) {
        return isBlockOreDict(world, pos, "treeLeaves");
    }

    public static void renderLaserBeam(TileEntity tile, double x, double y, double z, EnumFacing direction, float partialTicks, int length) {
        Tessellator tess = Tessellator.getInstance();
        GlStateManager.pushMatrix();
        double tempX = x;
        double tempY = y;
        double tempZ = z;
        switch (direction) {
            case NORTH:
                GlStateManager.rotate(270, 1, 0, 0);
                tempY = -z;
                tempZ = y;
                break;
            case SOUTH:
                GlStateManager.rotate(90, 1, 0, 0);
                tempY = z + 1;
                tempZ = -y - 1;
                break;
            case EAST:
                GlStateManager.rotate(270, 0, 0, 1);
                tempY = x + 1;
                tempX = -y - 1;
                break;
            case WEST:
                GlStateManager.rotate(90, 0, 0, 1);
                tempY = -x;
                tempX = y;
                break;
            default:
                tempY -= length;

        }
        RenderHelper.disableStandardItemLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        BufferBuilder buffer = tess.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        double d1 = -(tile.getWorld().getWorldTime() % 15) / 15D;
        double d2 = (tile.getWorld().getWorldTime() % 40) / 2D;
        double pointA = 0.45 - d2 / 200D;
        if (d2 >= 10) {
            pointA = 0.35 + d2 / 200D;
        }
        double pointB = 1 - pointA;
        double uStart = 0.0D;
        double uEnd = 1.0D;
        double vStart = -1.0F + d1;
        double vEnd = (length * 2) + vStart;
        buffer.pos(tempX + pointA, tempY + length, tempZ + pointA).tex(uEnd, vEnd).endVertex();
        buffer.pos(tempX + pointA, tempY, tempZ + pointA).tex(uEnd, vStart).endVertex();
        buffer.pos(tempX + pointB, tempY, tempZ + pointA).tex(uStart, vStart).endVertex();
        buffer.pos(tempX + pointB, tempY + length, tempZ + pointA).tex(uStart, vEnd).endVertex();
        buffer.pos(tempX + pointB, tempY + length, tempZ + pointB).tex(uEnd, vEnd).endVertex();
        buffer.pos(tempX + pointB, tempY, tempZ + pointB).tex(uEnd, vStart).endVertex();
        buffer.pos(tempX + pointA, tempY, tempZ + pointB).tex(uStart, vStart).endVertex();
        buffer.pos(tempX + pointA, tempY + length, tempZ + pointB).tex(uStart, vEnd).endVertex();
        buffer.pos(tempX + pointB, tempY + length, tempZ + pointA).tex(uEnd, vEnd).endVertex();
        buffer.pos(tempX + pointB, tempY, tempZ + pointA).tex(uEnd, vStart).endVertex();
        buffer.pos(tempX + pointB, tempY, tempZ + pointB).tex(uStart, vStart).endVertex();
        buffer.pos(tempX + pointB, tempY + length, tempZ + pointB).tex(uStart, vEnd).endVertex();
        buffer.pos(tempX + pointA, tempY + length, tempZ + pointB).tex(uEnd, vEnd).endVertex();
        buffer.pos(tempX + pointA, tempY, tempZ + pointB).tex(uEnd, vStart).endVertex();
        buffer.pos(tempX + pointA, tempY, tempZ + pointA).tex(uStart, vStart).endVertex();
        buffer.pos(tempX + pointA, tempY + length, tempZ + pointA).tex(uStart, vEnd).endVertex();
        tess.draw();
        GlStateManager.popMatrix();
    }
}
