package com.chailotl.spleef_toys;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class SpawnPointBlock extends Block
{
	public static final MapCodec<SpawnPointBlock> CODEC = createCodec(SpawnPointBlock::new);
	protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);

	@Override
	public MapCodec<SpawnPointBlock> getCodec()
	{
		return CODEC;
	}

	public SpawnPointBlock(Settings settings)
	{
		super(settings);
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return context.isHolding(Main.SPLEEF_SHOVEL) ? VoxelShapes.empty() : SHAPE;
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return VoxelShapes.empty();
	}
}