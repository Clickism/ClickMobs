/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickmobs.mixin;

import de.clickism.clickmobs.mob.PickupHandler;
import de.clickism.clickmobs.util.VersionHelper;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
//? if >1.20.1 {
import net.minecraft.core.dispenser.BlockSource;
//?} else {
/*import net.minecraft.core.BlockSource;
*///?}
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.clickism.clickmobs.ClickMobsConfig.CONFIG;
import static de.clickism.clickmobs.ClickMobsConfig.ENABLE_DISPENSERS;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin extends BaseEntityBlock {
    protected DispenserBlockMixin(Properties settings) {
        super(settings);
    }

    @Inject(
            method = "getDispenseMethod",
            at = @At("HEAD"),
            cancellable = true
    )
    protected void getBehaviorForItem(
            //? if >=1.21.1
            Level world,
            ItemStack itemStack,
            CallbackInfoReturnable<DispenseItemBehavior> cir
    ) {
        if (!PickupHandler.isMob(itemStack)) return;
        if (!ENABLE_DISPENSERS.get()) return;
        cir.setReturnValue((pointer, stack) -> {
            //? if <1.21.1
            //Level world = world(pointer);
            Direction direction = state(pointer).getValue(DispenserBlock.FACING);
            BlockPos blockPos = pos(pointer).relative(direction);
            Entity entity = PickupHandler.readEntityFromItemStack(world, stack);
            if (entity == null) return stack;
            VersionHelper.moveEntity(entity, blockPos);
            world.addFreshEntity(entity);
            stack.shrink(1);
            return stack;
        });
    }
    
    private static ServerLevel world(BlockSource pointer) {
        //? if >=1.21.1 {
        return pointer.level();
        //?} else
        //return pointer.getLevel();
    }
    
    private static BlockPos pos(BlockSource pointer) {
        //? if >=1.21.1 {
        return pointer.pos();
        //?} else
        //return pointer.getPos();
    }
    
    private static BlockState state(BlockSource pointer) {
        //? if >=1.21.1 {
        return pointer.state();
        //?} else
        //return pointer.getBlockState();
    }
}