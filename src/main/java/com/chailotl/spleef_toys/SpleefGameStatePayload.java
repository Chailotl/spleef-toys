package com.chailotl.spleef_toys;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record SpleefGameStatePayload(SpleefGameState gameState) implements CustomPayload
{
	public static final CustomPayload.Id<SpleefGameStatePayload> ID = new CustomPayload.Id<>(Main.id("spleef_game_state"));
	public static final PacketCodec<RegistryByteBuf, SpleefGameStatePayload> CODEC = PacketCodec.tuple(
		SpleefGameState.PACKET_CODEC, SpleefGameStatePayload::gameState,
		SpleefGameStatePayload::new
	);

	@Override
	public Id<? extends CustomPayload> getId()
	{
		return ID;
	}
}