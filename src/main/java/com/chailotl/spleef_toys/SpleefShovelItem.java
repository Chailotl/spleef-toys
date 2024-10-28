package com.chailotl.spleef_toys;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpleefShovelItem extends Item
{
	public SpleefShovelItem(Settings settings)
	{
		super(settings);
	}

	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner)
	{
		return state.getBlock() instanceof PixelBlock && miner.getAttached(Main.SPLEEF_GAME_STATE) == SpleefGameState.ACTIVE;
	}

	@Override
	public float getMiningSpeed(ItemStack stack, BlockState state)
	{
		return state.getBlock() instanceof PixelBlock ? 1f : 0f;
	}
}