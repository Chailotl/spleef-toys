package com.chailotl.spleef_toys.mixin;

import com.chailotl.spleef_toys.SpleefShovelItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin
{
	@Shadow @Final protected ServerPlayerEntity player;

	@WrapOperation(
		method = "tryBreakBlock",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/network/ServerPlayerInteractionManager;isCreative()Z"
		)
	)
	private boolean ignoreCreativeMode(ServerPlayerInteractionManager instance, Operation<Boolean> original)
	{
		if (player.getMainHandStack().getItem() instanceof SpleefShovelItem)
		{
			return false;
		}

		return original.call(instance);
	}
}