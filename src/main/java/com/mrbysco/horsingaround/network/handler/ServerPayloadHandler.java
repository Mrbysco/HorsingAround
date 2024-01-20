package com.mrbysco.horsingaround.network.handler;

import com.mrbysco.horsingaround.data.CallData;
import com.mrbysco.horsingaround.network.message.SummonPayload;
import com.mrbysco.horsingaround.network.message.UnlinkPayload;
import com.mrbysco.horsingaround.registry.HorsingRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.List;
import java.util.UUID;

public class ServerPayloadHandler {
	public static final ServerPayloadHandler INSTANCE = new ServerPayloadHandler();

	public static ServerPayloadHandler getInstance() {
		return INSTANCE;
	}

	public void handleSummon(final SummonPayload summonData, final PlayPayloadContext context) {
		// Do something with the data, on the main thread
		context.workHandler().submitAsync(() -> {
					//Sync big Player Statue data
					if (context.player().isPresent()) {
						if (context.player().get() instanceof ServerPlayer player) {
							ServerLevel level = player.serverLevel();
							UUID mobUUID = summonData.mobUUID();

							Entity mob = level.getEntity(mobUUID);
							CallData callData = CallData.get(player.level());
							if (mob == null) {
								List<CallData.TamedData> tamedList = callData.getTamedData(player.getUUID());
								CallData.TamedData matchingData = null;
								for (CallData.TamedData data : tamedList) {
									if (data.uuid().equals(mobUUID)) {
										matchingData = data;
										break;
									}
								}

								if (matchingData != null) {
									Entity entity = matchingData.createEntity(player.level());
									entity.setPos(player.position());
									level.addFreshEntity(entity);
								}
							} else {
								mob.teleportTo(player.getX(), player.getY(), player.getZ());
								callData.updateData(mob.getUUID(), mob);
							}
							callData.syncData(player.getUUID());

							level.playSound((Player) null, player.blockPosition(), HorsingRegistry.CALL.get(), player.getSoundSource(),
									1.0F, 1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);
						}
					}
				})
				.exceptionally(e -> {
					// Handle exception
					context.packetHandler().disconnect(Component.translatable("horsingaround.networking.summon.failed", e.getMessage()));
					return null;
				});
	}

	public void handleUnlink(final UnlinkPayload unlinkData, final PlayPayloadContext context) {
		// Do something with the data, on the main thread
		context.workHandler().submitAsync(() -> {
					//Execute craft if button is pressed
					if (context.player().isPresent()) {
						Player player = context.player().get();
						CallData callData = CallData.get(player.level());
						callData.removeTamedData(player.getUUID(), unlinkData.mobUUID());
					}
				})
				.exceptionally(e -> {
					// Handle exception
					context.packetHandler().disconnect(Component.translatable("horsingaround.networking.unlink.failed", e.getMessage()));
					return null;
				});
	}
}
