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
import com.hrznstudio.titanium.api.IRecipeProvider;
import com.hrznstudio.titanium.api.raytrace.DistanceRayTraceResult;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import com.hrznstudio.titanium.util.RayTraceUtils;
import com.hrznstudio.titanium.util.TileUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ConveyorBlock extends BasicTileBlock<ConveyorTile> implements SimpleWaterloggedBlock, IRecipeProvider {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<EnumType> TYPE = EnumProperty.create("type", EnumType.class);
    //public static final EnumProperty<EnumSides> SIDES = EnumProperty.create("sides", EnumSides.class);
    private ConveyorItem item;

    public ConveyorBlock(CreativeModeTab group) {
        super("conveyor", Properties.of(Material.HEAVY_METAL, MaterialColor.COLOR_ORANGE).noCollission().strength(2.0f), ConveyorTile.class);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
        //this.item = new ConveyorItem(this, group);
        this.setItemGroup(group);
    }

    @Override
    public Supplier<Item> getItemBlockFactory() {
        return this::getItem;
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter world, BlockPos pos, Direction side) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof ConveyorTile) {
            return ((ConveyorTile) tileEntity).getPower();
        }
        return super.getSignal(blockState, world, pos, side);
    }

    @Override
    public int getDirectSignal(BlockState blockState, BlockGetter world, BlockPos pos, Direction side) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof ConveyorTile) {
            return side == Direction.UP ? ((ConveyorTile) tileEntity).getPower() : 0;
        }
        return super.getDirectSignal(blockState, world, pos, side);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof ConveyorTile) {
            if (target instanceof DistanceRayTraceResult) {
                ConveyorUpgrade upgrade = ((ConveyorTile) tileEntity).getUpgradeMap().get(getFacingUpgradeHit(state, player.level, pos, player));
                if (upgrade != null) {
                    return new ItemStack(upgrade.getFactory().getUpgradeItem(), 1);
                }
            }
            return new ItemStack(this, 1);
        }
        return super.getCloneItemStack(state, target, world, pos, player);
    }

    @Override
    public BlockState getStateAtViewpoint(BlockState state, BlockGetter world, BlockPos pos, Vec3 viewpoint) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof ConveyorTile) {
            state = state.setValue(FACING, ((ConveyorTile) tileEntity).getFacing()).setValue(TYPE, ((ConveyorTile) tileEntity).getConveyorType());
        }
        if (state.getValue(TYPE).equals(EnumType.FLAT) || state.getValue(TYPE).equals(EnumType.FLAT_FAST)) {
            Direction right = state.getValue(FACING).getClockWise();
            Direction left = state.getValue(FACING).getCounterClockWise();
            if (isConveyorAndFacing(pos.relative(right), world, left) && isConveyorAndFacing(pos.relative(left), world, right) || (isConveyorAndFacing(pos.relative(right).below(), world, left) && isConveyorAndFacing(pos.relative(left).below(), world, right))) {
                //state = state.with(SIDES, EnumSides.BOTH);
            } else if (isConveyorAndFacing(pos.relative(right), world, left) || isConveyorAndFacing(pos.relative(right).below(), world, left)) {
                //state = state.with(SIDES, EnumSides.RIGHT);
            } else if (isConveyorAndFacing(pos.relative(left), world, right) || isConveyorAndFacing(pos.relative(left).below(), world, right)) {
                //state = state.with(SIDES, EnumSides.LEFT);
            } else {
                //state = state.with(SIDES, EnumSides.NONE);
            }
        }
        return state;
    }

    private boolean isConveyorAndFacing(BlockPos pos, BlockGetter world, Direction toFace) {
        return world.getBlockState(pos).getBlock() instanceof ConveyorBlock && (toFace == null || world.getBlockState(pos).getValue(FACING).equals(toFace));
    }

    //@Override
    //public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, BlockState state, BlockPos pos, Direction face) {
    //    return state.get(TYPE).isVertical() ? BlockFaceShape.UNDEFINED : face == Direction.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    //}

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean p_220069_6_) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, p_220069_6_);
        //        if (!worldIn.isRemote && !canPlaceBlockAt(worldIn, pos)) {
//            worldIn.destroyBlock(pos, false);
//        }
    }

    @Override
    public List<VoxelShape> getBoundingBoxes(BlockState state, BlockGetter source, BlockPos pos) {
        return getShapes(state, source, pos, conveyorUpgrade -> true);
    }

    private static List<VoxelShape> getShapes(BlockState state, BlockGetter source, BlockPos pos, Predicate<ConveyorUpgrade> filter){
        List<VoxelShape> boxes = new ArrayList<>();
        if (state.getValue(TYPE).isVertical()) {
            boxes.add(Shapes.box(0, 0, 0, 1, 1 / 16D, 1));
        } else {
            boxes.add(Shapes.box(0, 0, 0, 1, 1 / 16D, 1));
        }
        BlockEntity entity = source.getBlockEntity(pos);
        if (entity instanceof ConveyorTile) {
            for (ConveyorUpgrade upgrade : ((ConveyorTile) entity).getUpgradeMap().values())
                if (upgrade != null && filter.test(upgrade))
                    boxes.add(Shapes.create(upgrade.getBoundingBox().bounds()));
        }
        return boxes;
    }

    @Nonnull
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext selectionContext) {
        VoxelShape shape = Shapes.empty();
        for (VoxelShape shape1 : getShapes(state, world, pos, conveyorUpgrade -> !conveyorUpgrade.ignoresCollision())) {
            shape = Shapes.join(shape, shape1, BooleanOp.OR);
        }
        return shape;
    }

    @Override
    public boolean hasCustomBoxes(BlockState state, BlockGetter source, BlockPos pos) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, TYPE, WATERLOGGED);
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<ConveyorTile> getTileEntityFactory() {
        return ConveyorTile::new;
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        if (placer != null) {
            BlockEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity instanceof ConveyorTile) {
                ((ConveyorTile) tileEntity).setFacing(placer.getDirection());
            }
            updateConveyorPlacing(worldIn, pos, state, true);
        }
    }

    private void updateConveyorPlacing(Level worldIn, BlockPos pos, BlockState state, boolean first) {
        BlockEntity entity = worldIn.getBlockEntity(pos);
        if (entity instanceof ConveyorTile) {
            Direction direction = ((ConveyorTile) entity).getFacing();
            Direction right = state.getValue(FACING).getClockWise();
            Direction left = state.getValue(FACING).getCounterClockWise();
            if (((ConveyorTile) entity).getUpgradeMap().isEmpty()) {
                if (isConveyorAndFacing(pos.above().relative(direction), worldIn, null)) {//SELF UP
                    ((ConveyorTile) entity).setType(((ConveyorTile) entity).getConveyorType().getVertical(Direction.UP));
                } else if (isConveyorAndFacing(pos.above().relative(direction.getOpposite()), worldIn, null)) { //SELF DOWN
                    ((ConveyorTile) entity).setType(((ConveyorTile) entity).getConveyorType().getVertical(Direction.DOWN));
                }
            }
            //UPDATE SURROUNDINGS
            if (!first) return;
            if (isConveyorAndFacing(pos.relative(direction.getOpposite()).below(), worldIn, direction)) { //BACK DOWN
                updateConveyorPlacing(worldIn, pos.relative(direction.getOpposite()).below(), state, false);
            }
            if (isConveyorAndFacing(pos.relative(left).below(), worldIn, right)) { //LEFT DOWN
                updateConveyorPlacing(worldIn, pos.relative(left).below(), state, false);
            }
            if (isConveyorAndFacing(pos.relative(right).below(), worldIn, left)) { //RIGHT DOWN
                updateConveyorPlacing(worldIn, pos.relative(right).below(), state, false);
            }
            if (isConveyorAndFacing(pos.relative(direction).below(), worldIn, direction)) { //FRONT DOWN
                updateConveyorPlacing(worldIn, pos.relative(direction).below(), state, false);
            }
            worldIn.sendBlockUpdated(pos, state, state, 3);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray) {
        BlockEntity tileEntity = worldIn.getBlockEntity(pos);
        ItemStack handStack = player.getItemInHand(hand);
        if (tileEntity instanceof ConveyorTile) {
            Direction facing = getFacingUpgradeHit(state, worldIn, pos, player);
            if (player.isCrouching()) {
                if (facing != null) {
                    ((ConveyorTile) tileEntity).removeUpgrade(facing, true);
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.PASS;
            } else {
                if (facing == null) {
                    if (handStack.getItem().equals(Items.GLOWSTONE_DUST) && !((ConveyorTile) tileEntity).getConveyorType().isFast()) {
                        ((ConveyorTile) tileEntity).setType(((ConveyorTile) tileEntity).getConveyorType().getFast());
                        handStack.shrink(1);
                        return InteractionResult.SUCCESS;
                    }
                    if (handStack.getItem().equals(ModuleCore.PLASTIC) && !((ConveyorTile) tileEntity).isSticky()) {
                        ((ConveyorTile) tileEntity).setSticky(true);
                        handStack.shrink(1);
                        return InteractionResult.SUCCESS;
                    }
                    if (handStack.getItem() instanceof DyeItem) {
                        ((ConveyorTile) tileEntity).setColor(((DyeItem) handStack.getItem()).getDyeColor());
                        return InteractionResult.SUCCESS;
                    }
                } else {
                    if (((ConveyorTile) tileEntity).hasUpgrade(facing)) {
                        ConveyorUpgrade upgrade = ((ConveyorTile) tileEntity).getUpgradeMap().get(facing);
                        if (upgrade.onUpgradeActivated(player, hand)) {
                            return InteractionResult.SUCCESS;
                        } else if (upgrade.hasGui()) {
                            ((ConveyorTile) tileEntity).openGui(player, facing);
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
                return InteractionResult.PASS;
            }
        }
        return super.use(state, worldIn, pos, player, hand, ray);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext p_220053_4_) {
        if (state.getValue(TYPE).isVertical()) {
            return Shapes.box(0, 0, 0, 1, 0.40, 1);
        } else {
            VoxelShape shape = Shapes.box(0, 0, 0, 1, 1 / 16D, 1);
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof ConveyorTile) {
                for (ConveyorUpgrade upgrade : ((ConveyorTile) entity).getUpgradeMap().values())
                    if (upgrade != null)
                        shape = Shapes.or(shape, Shapes.create(upgrade.getBoundingBox().bounds()));
            }
            return shape;
        }
    }

    public Direction getFacingUpgradeHit(BlockState state, Level worldIn, BlockPos pos, Player player) {
        BlockEntity tileEntity = worldIn.getBlockEntity(pos);
        HitResult result = RayTraceUtils.rayTraceSimple(worldIn, player, 32, 0);
        if (result instanceof BlockHitResult) {
            VoxelShape hit = RayTraceUtils.rayTraceVoxelShape((BlockHitResult) result, worldIn, player, 32, 0);
            if (hit != null && tileEntity instanceof ConveyorTile) {
                for (Direction direction : ((ConveyorTile) tileEntity).getUpgradeMap().keySet()) {
                    if (Shapes.joinIsNotEmpty(((ConveyorTile) tileEntity).getUpgradeMap().get(direction).getBoundingBox(), hit, BooleanOp.AND)) {
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
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        ConveyorTile tile = new ConveyorTile(p_153215_, p_153216_);
        return tile;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState ifluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(FACING, context.getPlayer().getDirection()).setValue(WATERLOGGED, Boolean.valueOf(ifluidstate.getType() == Fluids.WATER));
    }

    @Override
    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.MODEL;
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        super.entityInside(state, worldIn, pos, entityIn);
        BlockEntity entity = worldIn.getBlockEntity(pos);
        if (entity instanceof ConveyorTile) {
            ((ConveyorTile) entity).handleEntityMovement(entityIn);
        }
    }

    @Override
    public NonNullList<ItemStack> getDynamicDrops(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        NonNullList<ItemStack> drops = NonNullList.create();
        Optional<ConveyorTile> entity = TileUtil.getTileEntity(worldIn, pos, ConveyorTile.class);
        entity.ifPresent(tileEntityConveyor -> {
            for (Direction value : Direction.values()) {
                if (tileEntityConveyor.getUpgradeMap().containsKey(value)) {
                    ConveyorUpgrade upgrade = tileEntityConveyor.getUpgradeMap().get(value);
                    drops.addAll(upgrade.getDrops());
                }
            }
            if (tileEntityConveyor.isSticky()) drops.add(new ItemStack(ModuleCore.PLASTIC.get()));
            if (tileEntityConveyor.getConveyorType().isFast()) drops.add(new ItemStack(Items.GLOWSTONE_DUST));
        });
        return drops;
    }
    
    @Override
    public boolean isValidSpawn(BlockState state, BlockGetter world, BlockPos pos, SpawnPlacements.Type type, EntityType<?> entityType) {
        return true;
    }

    @Override
    public boolean isPossibleToRespawnInThis() {
        return true;
    }

    public ConveyorItem getItem() {
        return item;
    }

    @Override
    public void registerRecipe(Consumer<FinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this, 6)
                .pattern("ppp").pattern("iri").pattern("ppp")
                .define('p', IndustrialTags.Items.PLASTIC)
                .define('i', Tags.Items.INGOTS_IRON)
                .define('r', Items.REDSTONE)
                .save(consumer);
    }

    public enum EnumType implements StringRepresentable {

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
            return getSerializedName();
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
        public String getSerializedName() { //getName
            return this.toString().toLowerCase();
        }
    }

    public enum EnumSides implements StringRepresentable {
        NONE, LEFT, RIGHT, BOTH;

        public String getName() {
            return getSerializedName();
        }

        @Override
        public String getSerializedName() { //getName
            return this.toString().toLowerCase();
        }
    }

    private class ConveyorItem extends BlockItem {

        public ConveyorItem(Block block, CreativeModeTab group) {
            super(block, new Item.Properties().tab(group));
            //this.setRegistryName(Reference.MOD_ID, "conveyor");
        }

        @Nullable
        @Override
        public String getCreatorModId(ItemStack itemStack) {
            return new TranslatableComponent("itemGroup." + this.category.getRecipeFolderName()).getString();
        }
    }

}
