package com.chailotl.spleef_toys.mixin;

import com.chailotl.spleef_toys.Main;
import com.chailotl.spleef_toys.SpleefShovelItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin
{
	@Shadow @Final private MinecraftClient client;

	@WrapOperation(
		method = "attackBlock",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/GameMode;isCreative()Z"
		)
	)
	private boolean ignoreCreativeMode(GameMode instance, Operation<Boolean> original)
	{
		if (client.player instanceof ClientPlayerEntity player
			&& player.getMainHandStack().getItem() instanceof SpleefShovelItem)
		{
			return false;
		}

		return original.call(instance);
	}

	@WrapOperation(
		method = "updateBlockBreakingProgress",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/GameMode;isCreative()Z"
		)
	)
	private boolean ignoreCreativeMode2(GameMode instance, Operation<Boolean> original)
	{
		if (client.player instanceof ClientPlayerEntity player
			&& player.getMainHandStack().getItem() instanceof SpleefShovelItem)
		{
			return false;
		}

		return original.call(instance);
	}
}