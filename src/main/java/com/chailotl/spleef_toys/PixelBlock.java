package com.chailotl.spleef_toys;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PixelBlock extends Block
{
	public static final MapCodec<PixelBlock> CODEC = createCodec(PixelBlock::new);
	public static final BooleanProperty LIT = Properties.LIT;

	@Override
	public MapCodec<PixelBlock> getCodec()
	{
		return CODEC;
	}

	public PixelBlock(Settings settings)
	{
		super(settings);
		setDefaultState(stateManager.getDefaultState().with(LIT, true));
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return state.get(LIT) ? VoxelShapes.fullCube() : VoxelShapes.empty();
	}

	@Override
	protected boolean canPathfindThrough(BlockState state, NavigationType type)
	{
		return !state.get(LIT);
	}

	@Override
	protected float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos)
	{
		return player.getMainHandStack().getItem() instanceof SpleefShovelItem ? 1f : super.calcBlockBreakingDelta(state, player, world, pos);
	}

	@Override
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool)
	{
		if (tool.isOf(Main.SPLEEF_SHOVEL))
		{
			if (player.getAttached(Main.SPLEEF_GAME_ENTITY) instanceof SpleefControllerEntity spleefControllerEntity
			&& spleefControllerEntity.isGameActive())
			{
				player.incrementStat(Stats.MINED.getOrCreateStat(this));
				world.setBlockState(pos, state.with(LIT, false));
				spleefControllerEntity.addBrokenBlock(pos);
			}
			else
			{
				world.setBlockState(pos, state);
			}
		}
		else
		{
			super.afterBreak(world, player, pos, state, blockEntity, tool);
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(LIT);
	}
}