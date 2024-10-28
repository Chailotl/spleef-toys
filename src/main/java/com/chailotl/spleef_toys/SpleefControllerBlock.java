package com.chailotl.spleef_toys;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SpleefControllerBlock extends BlockWithEntity
{
	public static final MapCodec<SpleefControllerBlock> CODEC = createCodec(SpleefControllerBlock::new);

	@Override
	protected MapCodec<SpleefControllerBlock> getCodec()
	{
		return CODEC;
	}

	protected SpleefControllerBlock(Settings settings)
	{
		super(settings);
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit)
	{
		if (!world.isClient && world.getBlockEntity(pos) instanceof SpleefControllerEntity blockEntity)
		{
			blockEntity.addPlayer((ServerPlayerEntity) player);
		}

		return ActionResult.SUCCESS;
	}

	@Override
	public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player)
	{
		if (!world.isClient && world.getBlockEntity(pos) instanceof SpleefControllerEntity blockEntity)
		{
			blockEntity.endGame();
		}
		return super.onBreak(world, pos, state, player);
	}

	@Override
	public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new SpleefControllerEntity(pos, state);
	}

	@Override
	protected BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.MODEL;
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
	{
		return validateTicker(type, Main.SPLEEF_CONTROLLER_ENTITY, SpleefControllerEntity::tick);
	}
}