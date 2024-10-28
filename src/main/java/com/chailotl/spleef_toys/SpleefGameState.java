package com.chailotl.spleef_toys;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public enum SpleefGameState
{
	INACTIVE,
	COUNTDOWN,
	ACTIVE;

	public static final PacketCodec<ByteBuf, SpleefGameState> PACKET_CODEC = PacketCodecs.INTEGER.xmap(i -> SpleefGameState.values()[i], SpleefGameState::ordinal);
}