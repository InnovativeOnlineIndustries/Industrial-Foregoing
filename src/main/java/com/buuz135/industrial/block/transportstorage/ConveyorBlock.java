/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
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
package com.buuz135.industrial.block.transportstorage;

import com.buuz135.industrial.api.conveyor.ConveyorUpgrade;
import com.buuz135.industrial.block.transportstorage.tile.ConveyorTile;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.utils.IndustrialTags;
import com.buuz135.industrial.utils.Reference;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.IRecipeProvider;
import com.hrznstudio.titanium.api.raytrace.DistanceRayTraceResult;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import com.hrznstudio.titanium.util.RayTraceUtils;
import com.hrznstudio.titanium.util.TileUtil;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ConveyorBlock extends BasicTileBlock<ConveyorTile> implements IWaterLoggable, IRecipeProvider {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static final EnumProperty<EnumType> TYPE = EnumProperty.create("type", EnumType.class);
    //public static final EnumProperty<EnumSides> SIDES = EnumProperty.create("sides", EnumSides.class);
    private ConveyorItem item;

    public ConveyorBlock(ItemGroup group) {
        super(Properties.create(Material.ANVIL, MaterialColor.ADOBE).doesNotBlockMovement().hardnessAndResistance(2.0f), ConveyorTile.class);
        this.setRegistryName(Reference.MOD_ID, "conveyor");
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(WATERLOGGED, false));
        this.item = new ConveyorItem(this, group);
        this.setItemGroup(group);
    }

    @Override
    public Item asItem() {
        return item;
    }

    @Override
    public IFactory<BlockItem> getItemBlockFactory() {
        return this::getItem;
    }

    @Override
    public int getWeakPower(BlockState blockState, IBlockReader world, BlockPos pos, Direction side) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof ConveyorTile) {
            return ((ConveyorTile) tileEntity).getPower();
        }
        return super.getWeakPower(blockState, world, pos, side);
    }

    @Override
    public int getStrongPower(BlockState blockState, IBlockReader world, BlockPos pos, Direction side) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof ConveyorTile) {
            return side == Direction.UP ? ((ConveyorTile) tileEntity).getPower() : 0;
        }
        return super.getStrongPower(blockState, world, pos, side);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof ConveyorTile) {
            if (target instanceof DistanceRayTraceResult) {
                ConveyorUpgrade upgrade = ((ConveyorTile) tileEntity).getUpgradeMap().get(getFacingUpgradeHit(state, player.world, pos, player));
                if (upgrade != null) {
                    return new ItemStack(upgrade.getFactory().getUpgradeItem(), 1);
                }
            }
            return new ItemStack(this, 1);
        }
        return super.getPickBlock(state, target, world, pos, player);
    }

    @Override
    public BlockState getStateAtViewpoint(BlockState state, IBlockReader world, BlockPos pos, Vector3d viewpoint) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof ConveyorTile) {
            state = state.with(FACING, ((ConveyorTile) tileEntity).getFacing()).with(TYPE, ((ConveyorTile) tileEntity).getConveyorType());
        }
        if (state.get(TYPE).equals(EnumType.FLAT) || state.get(TYPE).equals(EnumType.FLAT_FAST)) {
            Direction right = state.get(FACING).rotateY();
            Direction left = state.get(FACING).rotateYCCW();
            if (isConveyorAndFacing(pos.offset(right), world, left) && isConveyorAndFacing(pos.offset(left), world, right) || (isConveyorAndFacing(pos.offset(right).down(), world, left) && isConveyorAndFacing(pos.offset(left).down(), world, right))) {
                //state = state.with(SIDES, EnumSides.BOTH);
            } else if (isConveyorAndFacing(pos.offset(right), world, left) || isConveyorAndFacing(pos.offset(right).down(), world, left)) {
                //state = state.with(SIDES, EnumSides.RIGHT);
            } else if (isConveyorAndFacing(pos.offset(left), world, right) || isConveyorAndFacing(pos.offset(left).down(), world, right)) {
                //state = state.with(SIDES, EnumSides.LEFT);
            } else {
                //state = state.with(SIDES, EnumSides.NONE);
            }
        }
        return state;
    }

    private boolean isConveyorAndFacing(BlockPos pos, IBlockReader world, Direction toFace) {
        return world.getBlockState(pos).getBlock() instanceof ConveyorBlock && (toFace == null || world.getBlockState(pos).get(FACING).equals(toFace));
    }

    //@Override
    //public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
    //    return state.get(TYPE).isVertical() ? BlockFaceShape.UNDEFINED : face == Direction.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    //}

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean p_220069_6_) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, p_220069_6_);
        //        if (!worldIn.isRemote && !canPlaceBlockAt(worldIn, pos)) {
//            worldIn.destroyBlock(pos, false);
//        }
    }

    @Override
    public List<VoxelShape> getBoundingBoxes(BlockState state, IBlockReader source, BlockPos pos) {
        return getShapes(state, source, pos, conveyorUpgrade -> true);
    }

    private static List<VoxelShape> getShapes(BlockState state, IBlockReader source, BlockPos pos, Predicate<ConveyorUpgrade> filter){
        List<VoxelShape> boxes = new ArrayList<>();
        if (state.get(TYPE).isVertical()) {
            boxes.add(VoxelShapes.create(0, 0, 0, 1, 1 / 16D, 1));
        } else {
            boxes.add(VoxelShapes.create(0, 0, 0, 1, 1 / 16D, 1));
        }
        TileEntity entity = source.getTileEntity(pos);
        if (entity instanceof ConveyorTile) {
            for (ConveyorUpgrade upgrade : ((ConveyorTile) entity).getUpgradeMap().values())
                if (upgrade != null && filter.test(upgrade))
                    boxes.add(VoxelShapes.create(upgrade.getBoundingBox().getBoundingBox()));
        }
        return boxes;
    }

    @Nonnull
    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext selectionContext) {
        VoxelShape shape = VoxelShapes.empty();
        for (VoxelShape shape1 : getShapes(state, world, pos, conveyorUpgrade -> !conveyorUpgrade.ignoresCollision())) {
            shape = VoxelShapes.combineAndSimplify(shape, shape1, IBooleanFunction.OR);
        }
        return shape;
    }

    @Override
    public boolean hasCustomBoxes(BlockState state, IBlockReader source, BlockPos pos) {
        return true;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(FACING, TYPE, WATERLOGGED);
    }

    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    public IFactory<ConveyorTile> getTileEntityFactory() {
        return ConveyorTile::new;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        if (placer != null) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof ConveyorTile) {
                ((ConveyorTile) tileEntity).setFacing(placer.getHorizontalFacing());
            }
            updateConveyorPlacing(worldIn, pos, state, true);
        }
    }

    private void updateConveyorPlacing(World worldIn, BlockPos pos, BlockState state, boolean first) {
        TileEntity entity = worldIn.getTileEntity(pos);
        if (entity instanceof ConveyorTile) {
            Direction direction = ((ConveyorTile) entity).getFacing();
            Direction right = state.get(FACING).rotateY();
            Direction left = state.get(FACING).rotateYCCW();
            if (((ConveyorTile) entity).getUpgradeMap().isEmpty()) {
                if (isConveyorAndFacing(pos.up().offset(direction), worldIn, null)) {//SELF UP
                    ((ConveyorTile) entity).setType(((ConveyorTile) entity).getConveyorType().getVertical(Direction.UP));
                } else if (isConveyorAndFacing(pos.up().offset(direction.getOpposite()), worldIn, null)) { //SELF DOWN
                    ((ConveyorTile) entity).setType(((ConveyorTile) entity).getConveyorType().getVertical(Direction.DOWN));
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
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        ItemStack handStack = player.getHeldItem(hand);
        if (tileEntity instanceof ConveyorTile) {
            Direction facing = getFacingUpgradeHit(state, worldIn, pos, player);
            if (player.isCrouching()) {
                if (facing != null) {
                    ((ConveyorTile) tileEntity).removeUpgrade(facing, true);
                    return ActionResultType.SUCCESS;
                }
                return ActionResultType.PASS;
            } else {
                if (facing == null) {
                    if (handStack.getItem().equals(Items.GLOWSTONE_DUST) && !((ConveyorTile) tileEntity).getConveyorType().isFast()) {
                        ((ConveyorTile) tileEntity).setType(((ConveyorTile) tileEntity).getConveyorType().getFast());
                        handStack.shrink(1);
                        return ActionResultType.SUCCESS;
                    }
                    if (handStack.getItem().equals(ModuleCore.PLASTIC) && !((ConveyorTile) tileEntity).isSticky()) {
                        ((ConveyorTile) tileEntity).setSticky(true);
                        handStack.shrink(1);
                        return ActionResultType.SUCCESS;
                    }
                    if (handStack.getItem() instanceof DyeItem) {
                        ((ConveyorTile) tileEntity).setColor(((DyeItem) handStack.getItem()).getDyeColor());
                        return ActionResultType.SUCCESS;
                    }
                } else {
                    if (((ConveyorTile) tileEntity).hasUpgrade(facing)) {
                        ConveyorUpgrade upgrade = ((ConveyorTile) tileEntity).getUpgradeMap().get(facing);
                        if (upgrade.onUpgradeActivated(player, hand)) {
                            return ActionResultType.SUCCESS;
                        } else if (upgrade.hasGui()) {
                            ((ConveyorTile) tileEntity).openGui(player, facing);
                            return ActionResultType.SUCCESS;
                        }
                    }
                }
                return ActionResultType.PASS;
            }
        }
        return super.onBlockActivated(state, worldIn, pos, player, hand, ray);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext p_220053_4_) {
        if (state.get(TYPE).isVertical()) {
            return VoxelShapes.create(0, 0, 0, 1, 0.40, 1);
        } else {
            VoxelShape shape = VoxelShapes.create(0, 0, 0, 1, 1 / 16D, 1);
            TileEntity entity = world.getTileEntity(pos);
            if (entity instanceof ConveyorTile) {
                for (ConveyorUpgrade upgrade : ((ConveyorTile) entity).getUpgradeMap().values())
                    if (upgrade != null)
                        shape = VoxelShapes.or(shape, VoxelShapes.create(upgrade.getBoundingBox().getBoundingBox()));
            }
            return shape;
        }
    }

    public Direction getFacingUpgradeHit(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        RayTraceResult result = RayTraceUtils.rayTraceSimple(worldIn, player, 32, 0);
        if (result instanceof BlockRayTraceResult) {
            VoxelShape hit = RayTraceUtils.rayTraceVoxelShape((BlockRayTraceResult) result, worldIn, player, 32, 0);
            if (hit != null && tileEntity instanceof ConveyorTile) {
                for (Direction direction : ((ConveyorTile) tileEntity).getUpgradeMap().keySet()) {
                    if (VoxelShapes.compare(((ConveyorTile) tileEntity).getUpgradeMap().get(direction).getBoundingBox(), hit, IBooleanFunction.AND)) {
                        return direction;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public boolean hasIndividualRenderVoxelShape() {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        ConveyorTile tile = new ConveyorTile();
        return tile;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        FluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
        return this.getDefaultState().with(FACING, context.getPlayer().getHorizontalFacing()).with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
    }

    @Override
    public BlockRenderType getRenderType(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        super.onEntityCollision(state, worldIn, pos, entityIn);
        TileEntity entity = worldIn.getTileEntity(pos);
        if (entity instanceof ConveyorTile) {
            ((ConveyorTile) entity).handleEntityMovement(entityIn);
        }
    }

    @Override
    public NonNullList<ItemStack> getDynamicDrops(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        NonNullList<ItemStack> drops = NonNullList.create();
        Optional<ConveyorTile> entity = TileUtil.getTileEntity(worldIn, pos, ConveyorTile.class);
        entity.ifPresent(tileEntityConveyor -> {
            for (Direction value : Direction.values()) {
                if (tileEntityConveyor.getUpgradeMap().containsKey(value)) {
                    ConveyorUpgrade upgrade = tileEntityConveyor.getUpgradeMap().get(value);
                    drops.addAll(upgrade.getDrops());
                }
            }
            if (tileEntityConveyor.isSticky()) drops.add(new ItemStack(ModuleCore.PLASTIC));
            if (tileEntityConveyor.getConveyorType().isFast()) drops.add(new ItemStack(Items.GLOWSTONE_DUST));
        });
        return drops;
    }

    @Override
    public boolean canCreatureSpawn(BlockState state, IBlockReader world, BlockPos pos, EntitySpawnPlacementRegistry.PlacementType type, @Nullable EntityType<?> entityType) {
        return true;
    }

    @Override
    public boolean canSpawnInBlock() {
        return true;
    }

    public ConveyorItem getItem() {
        return item;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new ConveyorTile();
    }

    @Override
    public void registerRecipe(Consumer<IFinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this, 6)
                .patternLine("ppp").patternLine("iri").patternLine("ppp")
                .key('p', IndustrialTags.Items.PLASTIC)
                .key('i', Tags.Items.INGOTS_IRON)
                .key('r', Items.REDSTONE)
                .build(consumer);
    }

    public enum EnumType implements IStringSerializable {

        FLAT(false, "industrialforegoing:block/conveyor"),
        UP(false, "industrialforegoing:block/conveyor_ramp_inverted", "industrialforegoing:blocks/conveyor_color_inverted"),
        DOWN(false, "industrialforegoing:block/conveyor_ramp"),
        FLAT_FAST(true, "industrialforegoing:block/conveyor", "industrialforegoing:blocks/conveyor_color_fast"),
        UP_FAST(true, "industrialforegoing:block/conveyor_ramp_inverted", "industrialforegoing:blocks/conveyor_color_inverted_fast"),
        DOWN_FAST(true, "industrialforegoing:block/conveyor_ramp", "industrialforegoing:blocks/conveyor_color_fast");

        private boolean fast;
        private String model;
        private String texture;

        EnumType(boolean fast, String model) {
            this(false, model, "industrialforegoing:blocks/conveyor_color");
        }

        EnumType(boolean fast, String model, String texture) {
            this.fast = fast;
            this.model = model;
            this.texture = texture;
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

        public EnumType getVertical(Direction facing) {
            if (this.isFast()) {
                if (facing == Direction.UP) {
                    return UP_FAST;
                }
                if (facing == Direction.DOWN) {
                    return DOWN_FAST;
                }
                return FLAT_FAST;
            } else {
                if (facing == Direction.UP) {
                    return UP;
                }
                if (facing == Direction.DOWN) {
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

        public String getName() {
            return getString();
        }

        public String getModel() {
            return model;
        }

        public String getTexture() {
            return texture;
        }

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }

        @Override
        public String getString() { //getName
            return this.toString().toLowerCase();
        }
    }

    public enum EnumSides implements IStringSerializable {
        NONE, LEFT, RIGHT, BOTH;

        public String getName() {
            return getString();
        }

        @Override
        public String getString() { //getName
            return this.toString().toLowerCase();
        }
    }

    private class ConveyorItem extends BlockItem {

        public ConveyorItem(Block block, ItemGroup group) {
            super(block, new Item.Properties().group(group));
            this.setRegistryName(block.getRegistryName());
        }

        @Nullable
        @Override
        public String getCreatorModId(ItemStack itemStack) {
            return new TranslationTextComponent("itemGroup." + this.group.getPath()).getString();
        }
    }

}
