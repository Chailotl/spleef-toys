package com.chailotl.spleef_toys;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.render.RenderLayer;

public class ClientMain implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		BlockRenderLayerMap.INSTANCE.putBlock(Main.SPAWN_POINT, RenderLayer.getCutout());

		ClientPlayNetworking.registerGlobalReceiver(SpleefGameStatePayload.ID, (payload, context) -> {
			context.client().execute(() -> {
				context.player().setAttached(Main.SPLEEF_GAME_STATE, payload.gameState());
			});
		});
	}
}