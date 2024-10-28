package com.chailotl.spleef_toys;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class SpleefControllerEntity extends BlockEntity
{
	private static final int MIN_PLAYERS_TO_START = 2;
	private static final int MAX_PLAYERS_TO_END = 1;

	private SpleefGameState gameState = SpleefGameState.INACTIVE;
	private int timer = 0;
	private final List<ServerPlayerEntity> players = new ArrayList<>();
	private final List<BlockPos> brokenBlocks = new ArrayList<>();

	public SpleefControllerEntity(BlockPos pos, BlockState state)
	{
		super(Main.SPLEEF_CONTROLLER_ENTITY, pos, state);
	}

	public void addPlayer(ServerPlayerEntity player)
	{
		if (gameState != SpleefGameState.INACTIVE)
		{
			player.sendMessage(Text.translatable("gui.spleef_toys.game_already_started"), true);
		}
		else if (players.contains(player))
		{
			timer = 0;
			players.remove(player);
			player.setAttached(Main.SPLEEF_GAME_ENTITY, null);
			player.sendMessage(Text.translatable("gui.spleef_toys.left_game"), true);
			player.playSoundToPlayer(SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.BLOCKS, 0.75f, 0.707107f);
		}
		else if (player.getAttached(Main.SPLEEF_GAME_ENTITY) != null)
		{
			player.sendMessage(Text.translatable("gui.spleef_toys.already_in_game"), true);
		}
		else
		{
			timer = 0;
			players.add(player);
			player.setAttached(Main.SPLEEF_GAME_ENTITY, this);
			player.sendMessage(Text.translatable("gui.spleef_toys.joined_game"), true);
			player.playSoundToPlayer(SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.BLOCKS, 1f, 1f);
		}
	}

	public void addBrokenBlock(BlockPos pos)
	{
		brokenBlocks.add(pos);
	}

	public void startGame()
	{
		gameState = SpleefGameState.COUNTDOWN;
		timer = 80;
		players.forEach(player -> setPlayerState(player, SpleefGameState.COUNTDOWN));
	}

	public void endGame()
	{
		if (world == null) { return; }

		players.forEach(SpleefControllerEntity::clearPlayerState);

		if (players.size() == 1)
		{
			ServerPlayerEntity player = players.getFirst();
			sendTitle(player, Text.translatable("gui.spleef_toys.win_message"));
		}

		for (BlockPos pos: brokenBlocks)
		{
			BlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof PixelBlock)
			{
				world.setBlockState(pos, state.with(PixelBlock.LIT, true));
			}
		}

		gameState = SpleefGameState.INACTIVE;
		players.clear();
		brokenBlocks.clear();
	}

	public boolean isGameActive()
	{
		return gameState == SpleefGameState.ACTIVE;
	}

	public static void tick(World world, BlockPos pos, BlockState state, SpleefControllerEntity blockEntity)
	{
		List<ServerPlayerEntity> players = blockEntity.players;
		players.removeIf(player -> !player.isAlive());

		switch (blockEntity.gameState)
		{
			case INACTIVE -> {
				if (!players.isEmpty())
				{
					if (++blockEntity.timer >= 20 * 30)
					{
						players.forEach(player -> {
							clearPlayerState(player);
							player.sendMessage(Text.translatable("gui.spleef_toys.cancelled_game"), true);
						});
						players.clear();
					}
				}

				// Check if all players are standing on spawn points
				if (players.size() >= MIN_PLAYERS_TO_START && players.stream().allMatch(player -> world.getBlockState(player.getBlockPos()).isOf(Main.SPAWN_POINT)))
				{
					blockEntity.startGame();
				}
			}
			case COUNTDOWN -> {
				String string = switch (--blockEntity.timer) {
					case 20 -> "gui.spleef_toys.countdown.1";
					case 40 -> "gui.spleef_toys.countdown.2";
					case 60 -> "gui.spleef_toys.countdown.3";
					default -> null;
				};

				if (string != null)
				{
					players.forEach(player -> {
						player.sendMessage(Text.translatable(string), true);
						player.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), SoundCategory.BLOCKS, 1f, 1f);
					});
				}

				if (blockEntity.timer == 0)
				{
					blockEntity.gameState = SpleefGameState.ACTIVE;
					players.forEach(player -> {
						player.sendMessage(Text.translatable("gui.spleef_toys.countdown.0"), true);
						player.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), SoundCategory.BLOCKS, 1f, 2f);
						setPlayerState(player, SpleefGameState.ACTIVE);
					});
				}
			}
			case ACTIVE -> {
				List<ServerPlayerEntity> toRemove = new ArrayList<>();

				for (ServerPlayerEntity player : players)
				{
					BlockState blockState = player.getSteppingBlockState();
					boolean isPixel = blockState.getBlock() instanceof PixelBlock;
					boolean isSolid = blockState.getCollisionShape(world, player.getSteppingPos()) != VoxelShapes.empty();

					if (!isPixel && isSolid)
					{
						toRemove.add(player);
					}
				}

				for (ServerPlayerEntity player : toRemove)
				{
					players.remove(player);
					clearPlayerState(player);
					sendTitle(player, Text.translatable("gui.spleef_toys.lose_message"));
				}

				if (players.size() <= MAX_PLAYERS_TO_END)
				{
					blockEntity.endGame();
				}
			}
		}
	}

	private static void sendTitle(ServerPlayerEntity player, Text title)
	{
		player.networkHandler.sendPacket(new TitleS2CPacket(title));
	}

	private static void setPlayerState(ServerPlayerEntity player, SpleefGameState gameState)
	{
		player.setAttached(Main.SPLEEF_GAME_STATE, gameState);
		ServerPlayNetworking.send(player, new SpleefGameStatePayload(gameState));
	}

	private static void clearPlayerState(ServerPlayerEntity player)
	{
		player.setAttached(Main.SPLEEF_GAME_ENTITY, null);
		player.setAttached(Main.SPLEEF_GAME_STATE, SpleefGameState.INACTIVE);
		ServerPlayNetworking.send(player, new SpleefGameStatePayload(SpleefGameState.INACTIVE));
	}
}