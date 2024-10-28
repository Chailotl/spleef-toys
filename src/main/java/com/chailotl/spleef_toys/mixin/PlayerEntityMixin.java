package com.chailotl.spleef_toys.mixin;

import com.chailotl.spleef_toys.Main;
import com.chailotl.spleef_toys.SpleefGameState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity
{
	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world)
	{
		super(entityType, world);
	}

	@Inject(method = "shouldDamagePlayer", at = @At("RETURN"), cancellable = true)
	private void noDamage(PlayerEntity player, CallbackInfoReturnable<Boolean> cir)
	{
		if (getAttached(Main.SPLEEF_GAME_STATE) != SpleefGameState.INACTIVE
			&& player.getAttached(Main.SPLEEF_GAME_STATE) != SpleefGameState.INACTIVE)
		{
			cir.setReturnValue(false);
		}
	}

	@Override
	public boolean isSprinting()
	{
		return super.isSprinting() && !isCountdown();
	}

	@Inject(method = "getMovementSpeed", at = @At("RETURN"), cancellable = true)
	private void disableMovement(CallbackInfoReturnable<Float> cir)
	{
		if (isCountdown())
		{
			cir.setReturnValue(0f);
		}
	}

	@Inject(method = "getOffGroundSpeed", at = @At("RETURN"), cancellable = true)
	private void disableAirSpeed(CallbackInfoReturnable<Float> cir)
	{
		if (isCountdown())
		{
			Main.LOGGER.info("hello");
			cir.setReturnValue(0f);
		}
	}

	@Unique
	private boolean isCountdown()
	{
		return getAttached(Main.SPLEEF_GAME_STATE) == SpleefGameState.COUNTDOWN;
	}
}