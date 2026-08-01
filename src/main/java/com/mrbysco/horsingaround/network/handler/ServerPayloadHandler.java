package com.mrbysco.horsingaround.network.handler;

import com.mrbysco.horsingaround.data.CallData;
import com.mrbysco.horsingaround.network.message.SummonPayload;
import com.mrbysco.horsingaround.network.message.UnlinkPayload;
import com.mrbysco.horsingaround.util.CallUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {
	public static final ServerPayloadHandler INSTANCE = new ServerPayloadHandler();

	public static ServerPayloadHandler getInstance() {
		return INSTANCE;
	}

	public void handleSummon(final SummonPayload summonData, final IPayloadContext context) {
		context.enqueueWork(() -> {
					Player player = context.player();
					if (player instanceof ServerPlayer serverPlayer) {
						CallUtil.callAnimal(serverPlayer, summonData.mobUUID());
					}
				})
				.exceptionally(e -> {
					// Handle exception
					context.disconnect(Component.translatable("horsingaround.networking.summon.failed", e.getMessage()));
					return null;
				});
	}

	public void handleUnlink(final UnlinkPayload unlinkData, final IPayloadContext context) {
		context.enqueueWork(() -> {
					Player player = context.player();
					if (player != null) {
						CallData callData = CallData.get(player.level());
						callData.removeTamedData(player.getUUID(), unlinkData.mobUUID());
					}
				})
				.exceptionally(e -> {
					// Handle exception
					context.disconnect(Component.translatable("horsingaround.networking.unlink.failed", e.getMessage()));
					return null;
				});
	}
}
