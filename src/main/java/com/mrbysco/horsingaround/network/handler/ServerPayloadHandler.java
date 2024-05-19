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
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.UUID;

public class ServerPayloadHandler {
	public static final ServerPayloadHandler INSTANCE = new ServerPayloadHandler();

	public static ServerPayloadHandler getInstance() {
		return INSTANCE;
	}

	public void handleSummon(final SummonPayload summonData, final IPayloadContext context) {
		context.enqueueWork(() -> {
					Player player = context.player();
					if (player instanceof ServerPlayer serverPlayer) {
						ServerLevel level = serverPlayer.serverLevel();
						UUID mobUUID = summonData.mobUUID();

						Entity mob = level.getEntity(mobUUID);
						CallData callData = CallData.get(serverPlayer.level());
						if (mob == null) {
							List<CallData.TamedData> tamedList = callData.getTamedData(serverPlayer.getUUID());
							CallData.TamedData matchingData = null;
							for (CallData.TamedData data : tamedList) {
								if (data.uuid().equals(mobUUID)) {
									matchingData = data;
									break;
								}
							}

							if (matchingData != null) {
								Entity entity = matchingData.createEntity(serverPlayer.level());
								entity.setPos(serverPlayer.position());
								level.addFreshEntity(entity);
							}
						} else {
							mob.teleportTo(serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ());
							callData.updateData(mob.getUUID(), mob);
						}
						callData.syncData(serverPlayer.getUUID());

						level.playSound((Player) null, serverPlayer.blockPosition(), HorsingRegistry.CALL.get(), serverPlayer.getSoundSource(),
								1.0F, 1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);
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
