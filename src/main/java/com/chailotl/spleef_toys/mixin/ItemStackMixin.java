package com.chailotl.spleef_toys.mixin;

import com.chailotl.spleef_toys.Main;
import com.chailotl.spleef_toys.PixelBlock;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin
{
	@Shadow public abstract Item getItem();

	@Shadow public abstract boolean isOf(Item item);

	@Inject(method = "canBreak", at = @At("HEAD"), cancellable = true)
	private void spleefShovelCanBreakPixels(CachedBlockPosition pos, CallbackInfoReturnable<Boolean> cir)
	{
		if (isOf(Main.SPLEEF_SHOVEL) && pos.getBlockState().getBlock() instanceof PixelBlock)
		{
			cir.setReturnValue(true);
		}
	}
}