package com.chailotl.spleef_toys;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer
{
	public static final String MOD_ID = "spleef_toys";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final BlockSoundGroup PIXEL = new BlockSoundGroup(
		1.0F,
		1.5F,
		SoundEvents.BLOCK_METAL_BREAK,
		SoundEvents.BLOCK_AMETHYST_BLOCK_STEP,
		SoundEvents.BLOCK_METAL_PLACE,
		SoundEvents.BLOCK_AMETHYST_BLOCK_HIT,
		SoundEvents.BLOCK_METAL_FALL
	);

	public static SpleefShovelItem SPLEEF_SHOVEL = register("spleef_shovel", new SpleefShovelItem(new Item.Settings().maxCount(1)));

	public static PixelBlock WHITE_PIXEL = registerPixel("white_pixel");
	public static PixelBlock LIGHT_GRAY_PIXEL = registerPixel("light_gray_pixel");
	public static PixelBlock GRAY_PIXEL = registerPixel("gray_pixel");
	public static PixelBlock BLACK_PIXEL = registerPixel("black_pixel");
	public static PixelBlock BROWN_PIXEL = registerPixel("brown_pixel");
	public static PixelBlock RED_PIXEL = registerPixel("red_pixel");
	public static PixelBlock ORANGE_PIXEL = registerPixel("orange_pixel");
	public static PixelBlock YELLOW_PIXEL = registerPixel("yellow_pixel");
	public static PixelBlock LIME_PIXEL = registerPixel("lime_pixel");
	public static PixelBlock GREEN_PIXEL = registerPixel("green_pixel");
	public static PixelBlock CYAN_PIXEL = registerPixel("cyan_pixel");
	public static PixelBlock LIGHT_BLUE_PIXEL = registerPixel("light_blue_pixel");
	public static PixelBlock BLUE_PIXEL = registerPixel("blue_pixel");
	public static PixelBlock PURPLE_PIXEL = registerPixel("purple_pixel");
	public static PixelBlock MAGENTA_PIXEL = registerPixel("magenta_pixel");
	public static PixelBlock PINK_PIXEL = registerPixel("pink_pixel");

	public static SpleefControllerBlock SPLEEF_CONTROLLER = register("spleef_controller", new SpleefControllerBlock(AbstractBlock.Settings.create()
		.mapColor(MapColor.OAK_TAN)
		.instrument(NoteBlockInstrument.BASS)
		.sounds(BlockSoundGroup.WOOD)
		.strength(0.8f)
	));
	public static SpawnPointBlock SPAWN_POINT = register("spawn_point", new SpawnPointBlock(AbstractBlock.Settings.create()
		.nonOpaque()
	));

	public static BlockEntityType<SpleefControllerEntity> SPLEEF_CONTROLLER_ENTITY = register("spleef_controller", BlockEntityType.Builder.create(SpleefControllerEntity::new, SPLEEF_CONTROLLER).build());

	public static final AttachmentType<SpleefControllerEntity> SPLEEF_GAME_ENTITY = AttachmentRegistry.<SpleefControllerEntity>builder()
		.buildAndRegister(id("spleef_game_entity"));
	public static final AttachmentType<SpleefGameState> SPLEEF_GAME_STATE = AttachmentRegistry.<SpleefGameState>builder()
		.initializer(() -> SpleefGameState.INACTIVE)
		.buildAndRegister(id("spleef_game_state"));

	public static final GameRules.Key<GameRules.BooleanRule> DEBUG_SOLO_SPLEF = GameRuleRegistry.register("debugSoloSpleef", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(false));

	@Override
	public void onInitialize()
	{
		//LOGGER.info("Hello Fabric world!");

		PayloadTypeRegistry.playS2C().register(SpleefGameStatePayload.ID, SpleefGameStatePayload.CODEC);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
			content.add(SPLEEF_SHOVEL);
		});
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> {
			content.add(SPLEEF_CONTROLLER);
			content.add(SPAWN_POINT);
		});
	}

	public static Identifier id(String path)
	{
		return Identifier.of(MOD_ID, path);
	}

	public static <T extends Item> T register(String name, T item)
	{
		return Registry.register(Registries.ITEM, id(name), item);
	}

	public static <T extends Block> T register(String name, T block)
	{
		Registry.register(Registries.BLOCK, id(name), block);
		BlockItem blockItem = new BlockItem(block, new Item.Settings());
		Registry.register(Registries.ITEM, id(name), blockItem);
		return block;
	}

	public static <T extends BlockEntityType<?>> T register(String name, T blockEntityType)
	{
		return Registry.register(Registries.BLOCK_ENTITY_TYPE, id(name), blockEntityType);
	}

	public static PixelBlock registerPixel(String name)
	{
		PixelBlock block = register(name, new PixelBlock(AbstractBlock.Settings.create()
			.strength(0.3f)
			.sounds(PIXEL)
			.allowsSpawning(Blocks::never)
		));
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COLORED_BLOCKS).register(content -> content.add(block));
		return block;
	}
}