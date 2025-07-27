package com.mrbysco.horsingaround.network.handler;

import com.mrbysco.horsingaround.client.ClientHandler;
import com.mrbysco.horsingaround.data.CallData;
import com.mrbysco.horsingaround.data.CallData.TamedData;
import com.mrbysco.horsingaround.network.message.SyncPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.UUID;

public class ClientPayloadHandler {
	private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

	public static ClientPayloadHandler getInstance() {
		return INSTANCE;
	}

	public void handleSync(final SyncPayload syncData, final IPayloadContext context) {
		context.enqueueWork(() -> {
					Player player = context.player();
					if (player != null) {
						UUID playerUUID = syncData.playerUUID();
						if (player.getUUID().equals(playerUUID)) {
							ClientHandler.tamedList.clear();
							CompoundTag data = syncData.data();

							List<CallData.TamedData> dataList = TamedData.CODEC.listOf()
									.decode(player.registryAccess().createSerializationContext(NbtOps.INSTANCE), data).getOrThrow().getFirst();
							ClientHandler.tamedList.addAll(dataList);
						}
					}
				})
				.exceptionally(e -> {
					// Handle exception
					context.disconnect(Component.translatable("horsingaround.networking.sync.failed", e.getMessage()));
					return null;
				});
	}
}
