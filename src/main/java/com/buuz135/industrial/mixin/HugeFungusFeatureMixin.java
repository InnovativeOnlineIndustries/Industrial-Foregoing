package com.buuz135.industrial.mixin;

import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.HugeFungusFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HugeFungusFeature.class)
public class HugeFungusFeatureMixin {
    @WrapOperation(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z", ordinal = 0))
    private boolean allowPlacementOnHydroponicBed(BlockState instance, Block block, Operation<Boolean> original) {
        return original.call(instance, block) || instance.is(ModuleAgricultureHusbandry.HYDROPONIC_BED.getBlock());
    }
}
