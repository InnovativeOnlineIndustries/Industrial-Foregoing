/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2018, Buuz135
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
package com.buuz135.industrial.proxy.block;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.api.conveyor.ConveyorUpgrade;
import com.buuz135.industrial.proxy.ItemRegistry;
import com.buuz135.industrial.proxy.block.tile.TileEntityConveyor;
import com.buuz135.industrial.proxy.client.render.FluidConveyorTESR;
import com.buuz135.industrial.utils.RayTraceUtils;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.raytrace.DistanceRayTraceResult;
import com.hrznstudio.titanium.block.BlockTileBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockConveyor extends BlockTileBase<TileEntityConveyor> {

    public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;
    public static final EnumProperty<EnumType> TYPE = EnumProperty.create("type", EnumType.class);
    public static final EnumProperty<EnumSides> SIDES = EnumProperty.create("sides", EnumSides.class);
    private static String[] dyes = {"Black", "Red", "Green", "Brown", "Blue", "Purple", "Cyan", "LightGray", "Gray", "Pink", "Lime", "Yellow", "LightBlue", "Magenta", "Orange", "White"};
    private ConveyorItem item;

    public BlockConveyor() {
        super("conveyor", Properties.create(Material.ANVIL, MaterialColor.ADOBE).doesNotBlockMovement().hardnessAndResistance(2.0f), TileEntityConveyor.class);
        this.setDefaultState(this.getDefaultState().with(FACING, EnumFacing.NORTH).with(SIDES, EnumSides.NONE));
        this.item = new ConveyorItem(this);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void registerModels() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityConveyor.class, new FluidConveyorTESR());
    }

    @OnlyIn(Dist.CLIENT)
    public void createRecipe() {
//        RecipeUtils.addShapedRecipe(new ItemStack(this, 4, 0), "ppp", "iri", "ppp",
//                'p', ItemRegistry.plastic,
//                'i', "ingotIron",
//                'r', Items.REDSTONE);
//        for (int i = 0; i < dyes.length; i++) {
//            RecipeUtils.addShapedRecipe(new ItemStack(this, 8, 15 - i), "_" + dyes[i].toLowerCase(), new HashMap<>(), "ccc", "cdc", "ccc",
//                    'c', new ItemStack(this, 1, OreDictionary.WILDCARD_VALUE),
//                    'd', "dye" + dyes[i]);
//        }
    }

    @Override
    public int getWeakPower(IBlockState blockState, IBlockReader world, BlockPos pos, EnumFacing side) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityConveyor) {
            return ((TileEntityConveyor) tileEntity).getPower();
        }
        return super.getWeakPower(blockState, world, pos, side);
    }

    @Override
    public int getStrongPower(IBlockState blockState, IBlockReader world, BlockPos pos, EnumFacing side) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityConveyor) {
            return side == EnumFacing.UP ? ((TileEntityConveyor) tileEntity).getPower() : 0;
        }
        return super.getStrongPower(blockState, world, pos, side);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, EntityPlayer player) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityConveyor) {
            if (target instanceof DistanceRayTraceResult) {
                ConveyorUpgrade upgrade = ((TileEntityConveyor) tileEntity).getUpgradeMap().get(getFacingUpgradeHit(state, player.world, pos, player));
                if (upgrade != null) {
                    return new ItemStack(upgrade.getFactory().getUpgradeItem(), 1);
                }
            }
            return new ItemStack(this, 1);//TODO Fix types
        }
        return super.getPickBlock(state, target, world, pos, player);
    }

    @Override
    public IBlockState getStateAtViewpoint(IBlockState state, IBlockReader world, BlockPos pos, Vec3d viewpoint) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityConveyor) {
            state = state.with(FACING, ((TileEntityConveyor) tileEntity).getFacing()).with(TYPE, ((TileEntityConveyor) tileEntity).getConveyorType());
        }
        if (state.get(TYPE).equals(EnumType.FLAT) || state.get(TYPE).equals(EnumType.FLAT_FAST)) {
            EnumFacing right = state.get(FACING).rotateY();
            EnumFacing left = state.get(FACING).rotateYCCW();
            if (isConveyorAndFacing(pos.offset(right), world, left) && isConveyorAndFacing(pos.offset(left), world, right) || (isConveyorAndFacing(pos.offset(right).down(), world, left) && isConveyorAndFacing(pos.offset(left).down(), world, right))) {
                state = state.with(SIDES, EnumSides.BOTH);
            } else if (isConveyorAndFacing(pos.offset(right), world, left) || isConveyorAndFacing(pos.offset(right).down(), world, left)) {
                state = state.with(SIDES, EnumSides.RIGHT);
            } else if (isConveyorAndFacing(pos.offset(left), world, right) || isConveyorAndFacing(pos.offset(left).down(), world, right)) {
                state = state.with(SIDES, EnumSides.LEFT);
            } else {
                state = state.with(SIDES, EnumSides.NONE);
            }
        }
        return state;
    }

    private boolean isConveyorAndFacing(BlockPos pos, IBlockReader world, EnumFacing toFace) {
        return world.getBlockState(pos).getBlock() instanceof BlockConveyor && (toFace == null || world.getBlockState(pos).get(FACING).equals(toFace));
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return state.get(TYPE).isVertical() ? BlockFaceShape.UNDEFINED : face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
//        if (!worldIn.isRemote && !canPlaceBlockAt(worldIn, pos)) {
//            worldIn.destroyBlock(pos, false);
//        }
    }

    @Override
    public List<VoxelShape> getBoundingBoxes(IBlockState state, IBlockReader source, BlockPos pos) {
        List<VoxelShape> boxes = new ArrayList<>();
        if (state.get(TYPE).isVertical()) {
            boxes.add(VoxelShapes.create(0, 0, 0, 1, 0.40, 1));
        } else {
            boxes.add(VoxelShapes.create(0, 0, 0, 1, 1 / 16D, 1));
        }
        TileEntity entity = source.getTileEntity(pos);
        if (entity instanceof TileEntityConveyor) {
            for (ConveyorUpgrade upgrade : ((TileEntityConveyor) entity).getUpgradeMap().values())
                if (upgrade != null)
                    boxes.add(VoxelShapes.create(upgrade.getBoundingBox().getBoundingBox()));
        }
        return boxes;
    }

    @Override
    public boolean hasCustomBoxes(IBlockState state, IBlockReader source, BlockPos pos) {
        return true;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(FACING, SIDES, TYPE);
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    public IFactory<TileEntityConveyor> getTileEntityFactory() {
        return TileEntityConveyor::new;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        if (placer != null) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof TileEntityConveyor) {
                ((TileEntityConveyor) tileEntity).setFacing(placer.getHorizontalFacing());
            }
            updateConveyorPlacing(worldIn, pos, state, true);
        }
    }

    @Override
    public void onPlayerDestroy(IWorld world, BlockPos pos, IBlockState state) {
        NonNullList<ItemStack> list = NonNullList.create();
        getDrops(state, list, world.getWorld(), pos, 0);
        for (ItemStack stack : list) {
            float f = 0.7F;
            float d0 = world.getRandom().nextFloat() * f + (1.0F - f) * 0.5F;
            float d1 = world.getRandom().nextFloat() * f + (1.0F - f) * 0.5F;
            float d2 = world.getRandom().nextFloat() * f + (1.0F - f) * 0.5F;
            EntityItem entityitem = new EntityItem(world.getWorld(), pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, stack);
            world.spawnEntity(entityitem);
        }
        super.onPlayerDestroy(world, pos, state);
    }

    @Override
    public void getDrops(IBlockState state, NonNullList<ItemStack> drops, World world, BlockPos pos, int fortune) {
        TileEntity entity = world.getTileEntity(pos);
        if (entity instanceof TileEntityConveyor) {
            drops.add(new ItemStack(this, 1));
            for (ConveyorUpgrade upgrade : ((TileEntityConveyor) entity).getUpgradeMap().values()) {
                drops.addAll(upgrade.getDrops());
            }
            if (((TileEntityConveyor) entity).getConveyorType().isFast()) {
                drops.add(new ItemStack(Items.GLOWSTONE_DUST, 1));
            }
            if (((TileEntityConveyor) entity).isSticky()) {
                drops.add(new ItemStack(ItemRegistry.plastic, 1));
            }
        }
    }

    private void updateConveyorPlacing(World worldIn, BlockPos pos, IBlockState state, boolean first) {
        TileEntity entity = worldIn.getTileEntity(pos);
        if (entity instanceof TileEntityConveyor) {
            EnumFacing direction = ((TileEntityConveyor) entity).getFacing();
            EnumFacing right = state.get(FACING).rotateY();
            EnumFacing left = state.get(FACING).rotateYCCW();
            if (((TileEntityConveyor) entity).getUpgradeMap().isEmpty()) {
                if (isConveyorAndFacing(pos.up().offset(direction), worldIn, null)) {//SELF UP
                    ((TileEntityConveyor) entity).setType(((TileEntityConveyor) entity).getConveyorType().getVertical(EnumFacing.UP));
                } else if (isConveyorAndFacing(pos.up().offset(direction.getOpposite()), worldIn, null)) { //SELF DOWN
                    ((TileEntityConveyor) entity).setType(((TileEntityConveyor) entity).getConveyorType().getVertical(EnumFacing.DOWN));
                }
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
    public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        ItemStack handStack = player.getHeldItem(hand);
        if (tileEntity instanceof TileEntityConveyor) {
            if (player.isSneaking()) {
                EnumFacing facing = getFacingUpgradeHit(state, worldIn, pos, player);
                if (facing != null) {
                    ((TileEntityConveyor) tileEntity).removeUpgrade(facing, true);
                    return true;
                }
                return false;
            } else {
                EnumFacing facing = getFacingUpgradeHit(state, worldIn, pos, player);
                if (facing == null) {
                        if (handStack.getItem().equals(Items.GLOWSTONE_DUST) && !((TileEntityConveyor) tileEntity).getConveyorType().isFast()) {
                            ((TileEntityConveyor) tileEntity).setType(((TileEntityConveyor) tileEntity).getConveyorType().getFast());
                            handStack.shrink(1);
                            return true;
                        }
                        if (handStack.getItem().equals(ItemRegistry.plastic) && !((TileEntityConveyor) tileEntity).isSticky()) {
                            ((TileEntityConveyor) tileEntity).setSticky(true);
                            handStack.shrink(1);
                            return true;
                        }
                    } else {
                    if (((TileEntityConveyor) tileEntity).hasUpgrade(facing)) {
                        ConveyorUpgrade upgrade = ((TileEntityConveyor) tileEntity).getUpgradeMap().get(facing);
                            if (upgrade.onUpgradeActivated(player, hand)) {
                                return true;
                            } else if (upgrade.hasGui()) {
                                ((TileEntityConveyor) tileEntity).openGui(player);
                                //player.openGui(IndustrialForegoing.instance, 1, worldIn, pos.getX(), pos.getY(), pos.getZ());
                                return true;
                            }
                        }
                    }
                    return false;

            }
        }
        return super.onBlockActivated(state, worldIn, pos, player, hand, side, hitX, hitY, hitZ);
    }


    @Override
    public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
        if (state.get(TYPE).isVertical()) {
            return VoxelShapes.create(0, 0, 0, 1, 0.40, 1);
        } else {
            return VoxelShapes.create(0, 0, 0, 1, 1 / 16D, 1);
        }
    }

    public EnumFacing getFacingUpgradeHit(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        RayTraceResult hit = RayTraceUtils.rayTraceSimple(worldIn, player, 32, 0);
        if (tileEntity instanceof TileEntityConveyor && hit instanceof DistanceRayTraceResult) {
            for (EnumFacing enumFacing : ((TileEntityConveyor) tileEntity).getUpgradeMap().keySet()) {
                if (VoxelShapes.compare(((TileEntityConveyor) tileEntity).getUpgradeMap().get(enumFacing).getBoundingBox(), ((DistanceRayTraceResult) hit).getHitBox(), IBooleanFunction.AND)) {
                    return enumFacing;
                }
            }
        }
        return null;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(IBlockState state, IBlockReader world) {
        TileEntityConveyor tile = new TileEntityConveyor();
        return tile;
    }

    @Nullable
    @Override
    public IBlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlayer().getHorizontalFacing());
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState p_149645_1_) {
        return EnumBlockRenderType.MODEL;
    }

    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public void onEntityCollision(IBlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        super.onEntityCollision(state, worldIn, pos, entityIn);
        TileEntity entity = worldIn.getTileEntity(pos);
        if (entity instanceof TileEntityConveyor) {
            ((TileEntityConveyor) entity).handleEntityMovement(entityIn);
        }
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IWorldReaderBase world, BlockPos pos, EntitySpawnPlacementRegistry.SpawnPlacementType type, @Nullable EntityType<? extends EntityLiving> entityType) {
        return true;
    }

    @Override
    public boolean canSpawnInBlock() {
        return true;
    }

    @Override
    public boolean canEntitySpawn(IBlockState state, Entity entityIn) {
        return true;
    }

    public ConveyorItem getItem() {
        return item;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileEntityConveyor();
    }

    public enum EnumType implements IStringSerializable {


        FLAT(false), UP(false), DOWN(false), FLAT_FAST(true), UP_FAST(true), DOWN_FAST(true);

        private boolean fast;

        EnumType(boolean fast) {
            this.fast = fast;
        }

        public static EnumType getFromName(String name) {
            for (EnumType type : EnumType.values()) {
                if (type.getName().equalsIgnoreCase(name)) {
                    return type;
                }
            }
            return FLAT;
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
            super(block, new Item.Properties().group(IndustrialForegoing.creativeTab));
            this.setRegistryName(block.getRegistryName());

        }

    }

}
