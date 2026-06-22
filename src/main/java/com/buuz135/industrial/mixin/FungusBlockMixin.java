package com.buuz135.industrial.mixin;

import com.buuz135.industrial.module.ModuleAgricultureHusbandry;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FungusBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FungusBlock.class)
public class FungusBlockMixin {
    @WrapOperation(method = "isValidBonemealTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
    private boolean allowBonemealOnHydroponicBed(BlockState instance, Block block, Operation<Boolean> original, @Local(name = "blockstate") BlockState blockstate) {
        return original.call(instance, block) || blockstate.is(ModuleAgricultureHusbandry.HYDROPONIC_BED.getBlock());
    }
}
