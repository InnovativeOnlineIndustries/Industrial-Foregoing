package com.buuz135.industrial.proxy.block;

import com.buuz135.industrial.IndustrialForegoing;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BlockConveyor extends BlockBase {

    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyEnum<EnumType> TYPE = PropertyEnum.create("type", EnumType.class);
    public static final PropertyEnum<EnumSides> SIDES = PropertyEnum.create("sides", EnumSides.class);

    private ConveyorItem item;

    public BlockConveyor() {
        super("conveyor");
        this.setDefaultState(this.getDefaultState().withProperty(FACING, EnumFacing.NORTH).withProperty(SIDES, EnumSides.NONE));
        this.item = new ConveyorItem(this);
        this.setCreativeTab(IndustrialForegoing.creativeTab);
        this.setHardness(2);
    }

    @Override
    public void registerItem(IForgeRegistry<Item> items) {
        items.register(item);
    }

    @Override
    public void registerRender() {
        for (int i = 0; i < EnumDyeColor.values().length; ++i) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), i, new ModelResourceLocation(this.getRegistryName(), "inventory"));
        }
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return Arrays.asList(EnumFacing.values()).indexOf(state.getValue(FACING));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.values()[meta]);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TileEntityConveyor) {
            state = state.withProperty(FACING, ((TileEntityConveyor) tileEntity).getFacing()).withProperty(TYPE, ((TileEntityConveyor) tileEntity).getType());
        }
        if (state.getValue(TYPE).equals(EnumType.FLAT) || state.getValue(TYPE).equals(EnumType.FLAT_FAST)) {
            EnumFacing right = state.getValue(FACING).rotateY();
            EnumFacing left = state.getValue(FACING).rotateYCCW();
            if (isConveyorAndFacing(pos.offset(right), worldIn, left) && isConveyorAndFacing(pos.offset(left), worldIn, right) || (isConveyorAndFacing(pos.offset(right).down(), worldIn, left) && isConveyorAndFacing(pos.offset(left).down(), worldIn, right))) {
                state = state.withProperty(SIDES, EnumSides.BOTH);
            } else if (isConveyorAndFacing(pos.offset(right), worldIn, left) || isConveyorAndFacing(pos.offset(right).down(), worldIn, left)) {
                state = state.withProperty(SIDES, EnumSides.RIGHT);
            } else if (isConveyorAndFacing(pos.offset(left), worldIn, right) || isConveyorAndFacing(pos.offset(left).down(), worldIn, right)) {
                state = state.withProperty(SIDES, EnumSides.LEFT);
            } else {
                state = state.withProperty(SIDES, EnumSides.NONE);
            }
        }
        return state;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        TileEntity entity = worldIn.getTileEntity(pos);
        if (entity instanceof TileEntityConveyor) {
            if (!((TileEntityConveyor) entity).getType().isVertical()) {
                super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
                return;
            }
            ((TileEntityConveyor) entity).getCollisionBoxes().forEach(axisAlignedBB -> addCollisionBoxToList(pos, entityBox, collidingBoxes, axisAlignedBB));
        }
    }

    private boolean isConveyorAndFacing(BlockPos pos, IBlockAccess world, EnumFacing toFace) {
        return world.getBlockState(pos).getBlock() instanceof BlockConveyor && (toFace == null || world.getBlockState(pos).getValue(FACING).equals(toFace));
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (int i = 0; i < EnumDyeColor.values().length; ++i) {
            items.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(worldIn, pos, state, rand);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        if (worldIn.getBlockState(pos.down()).getBlock() instanceof BlockConveyor) {
            return false;
        }
        return super.canPlaceBlockAt(worldIn, pos);
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, SIDES, TYPE);
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        TileEntity tileEntity = this.createTileEntity(worldIn, state);
        worldIn.setTileEntity(pos, tileEntity);
        EnumFacing direction = placer.getHorizontalFacing();
        ((TileEntityConveyor) tileEntity).setFacing(direction);
        ((TileEntityConveyor) tileEntity).setColor(EnumDyeColor.values()[stack.getMetadata()]);
        updateConveyorPlacing(worldIn, pos, state, true);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        NonNullList<ItemStack> list = NonNullList.create();
        getDrops(list, world, pos, state, 0);
        for (ItemStack stack : list) {
            float f = 0.7F;
            float d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5F;
            float d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5F;
            float d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5F;
            EntityItem entityitem = new EntityItem(world, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, stack);
            world.spawnEntity(entityitem);
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity entity = world.getTileEntity(pos);
        if (entity instanceof TileEntityConveyor) {
            drops.add(new ItemStack(this, 1, EnumDyeColor.byDyeDamage(((TileEntityConveyor) entity).getColor()).getMetadata()));
            if (((TileEntityConveyor) entity).getType().isFast()) {
                drops.add(new ItemStack(Items.GLOWSTONE_DUST, 1));
            }
        }
    }

    private void updateConveyorPlacing(World worldIn, BlockPos pos, IBlockState state, boolean first) {
        TileEntity entity = worldIn.getTileEntity(pos);
        if (entity instanceof TileEntityConveyor) {
            EnumFacing direction = ((TileEntityConveyor) entity).getFacing();
            EnumFacing right = state.getValue(FACING).rotateY();
            EnumFacing left = state.getValue(FACING).rotateYCCW();
            if (isConveyorAndFacing(pos.up().offset(direction), worldIn, null)) {//SELF UP
                ((TileEntityConveyor) entity).setType(((TileEntityConveyor) entity).getType().getVertical(EnumFacing.UP));
            } else if (isConveyorAndFacing(pos.up().offset(direction.getOpposite()), worldIn, null)) { //SELF DOWN
                ((TileEntityConveyor) entity).setType(((TileEntityConveyor) entity).getType().getVertical(EnumFacing.DOWN));
            }
            //UPDATE SURROUNDINGS
            if (!first) return;
            if (isConveyorAndFacing(pos.offset(direction.getOpposite()).down(), worldIn, direction)) { //BACK DOWN
                updateConveyorPlacing(worldIn, pos.offset(direction.getOpposite()).down(), state, false);
            }
            if (isConveyorAndFacing(pos.offset(left).down(), worldIn, right)) { //LEFT DOWN
                updateConveyorPlacing(worldIn, pos.offset(left).down(), state, false);
            }
            if (isConveyorAndFacing(pos.offset(right).down(), worldIn, left)) { //RIGHT DOWN
                updateConveyorPlacing(worldIn, pos.offset(right).down(), state, false);
            }
            if (isConveyorAndFacing(pos.offset(direction).down(), worldIn, direction)) { //FRONT DOWN
                updateConveyorPlacing(worldIn, pos.offset(direction).down(), state, false);
            }
            worldIn.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        ItemStack handStack = playerIn.getHeldItem(hand);
        if (tileEntity instanceof TileEntityConveyor) {
            if (handStack.getItem().equals(Items.GLOWSTONE_DUST) && !((TileEntityConveyor) tileEntity).getType().isFast()) {
                ((TileEntityConveyor) tileEntity).setType(((TileEntityConveyor) tileEntity).getType().getFast());
                handStack.shrink(1);
                return true;
            }
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        TileEntityConveyor tile = new TileEntityConveyor();
        tile.setWorld(world);
        return tile;
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
    }

    @Override
    public void registerBlock(IForgeRegistry<Block> blocks) {
        super.registerBlock(blocks);
        GameRegistry.registerTileEntity(TileEntityConveyor.class, "conveyor_tile");
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        TileEntity entity = source.getTileEntity(pos);
        if (entity instanceof TileEntityConveyor) {
            if (((TileEntityConveyor) entity).getType().isVertical()) {
                return new AxisAlignedBB(0, 0, 0, 1, 0.40, 1);
            }
            return new AxisAlignedBB(0, 0, 0, 1, 1 / 16D, 1);
        }
        return FULL_BLOCK_AABB;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        TileEntity entity = worldIn.getTileEntity(pos);
        if (entity instanceof TileEntityConveyor) {
            ((TileEntityConveyor) entity).handleEntityMovement(entityIn);
        }
    }

    public ConveyorItem getItem() {
        return item;
    }

    public enum EnumType implements IStringSerializable {


        FLAT(false), UP(false), DOWN(false), FLAT_FAST(true), UP_FAST(true), DOWN_FAST(true);

        private boolean fast;

        EnumType(boolean fast) {
            this.fast = fast;
        }

        public boolean isFast() {
            return fast;
        }

        public EnumType getFast() {
            switch (this) {
                case FLAT:
                    return FLAT_FAST;
                case UP:
                    return UP_FAST;
                case DOWN:
                    return DOWN_FAST;
                default:
                    return this;
            }
        }

        public EnumType getVertical(EnumFacing facing) {
            if (this.isFast()) {
                if (facing == EnumFacing.UP) {
                    return UP_FAST;
                }
                if (facing == EnumFacing.DOWN) {
                    return DOWN_FAST;
                }
                return FLAT_FAST;
            } else {
                if (facing == EnumFacing.UP) {
                    return UP;
                }
                if (facing == EnumFacing.DOWN) {
                    return DOWN;
                }
                return FLAT_FAST;
            }
        }

        public boolean isVertical() {
            return isDown() || isUp();
        }

        public boolean isUp() {
            return this.equals(UP) || this.equals(UP_FAST);
        }

        public boolean isDown() {
            return this.equals(DOWN) || this.equals(DOWN_FAST);
        }

        @Override
        public String getName() {
            return this.toString().toLowerCase();
        }

    }

    public enum EnumSides implements IStringSerializable {
        NONE, LEFT, RIGHT, BOTH;

        @Override
        public String getName() {
            return this.toString().toLowerCase();
        }
    }

    private class ConveyorItem extends ItemBlock {

        public ConveyorItem(Block block) {
            super(block);
            this.setRegistryName(block.getRegistryName());
            this.setHasSubtypes(true);
            this.setCreativeTab(IndustrialForegoing.creativeTab);
        }

        @Override
        public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
            if (this.isInCreativeTab(tab)) {
                for (int i = 0; i < EnumDyeColor.values().length; ++i) {
                    items.add(new ItemStack(this, 1, i));
                }
            }
        }
    }

}
