package com.mrbysco.horsingaround.network.handler;

import com.mrbysco.horsingaround.client.ClientHandler;
import com.mrbysco.horsingaround.data.CallData;
import com.mrbysco.horsingaround.network.message.SyncPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
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
						ClientHandler.tamedList.clear();
						UUID playerUUID = syncData.playerUUID();
						CompoundTag data = syncData.data();

						ListTag dataListTag = new ListTag();
						String uuid = playerUUID.toString();
						if (data.getTagType(uuid) == 9) {
							Tag nbt = data.get(uuid);
							if (nbt instanceof ListTag listNBT) {
								if (!listNBT.isEmpty() && listNBT.getElementType() != CompoundTag.TAG_COMPOUND) {
									return;
								}

								dataListTag = listNBT;
							}
						}
						if (!dataListTag.isEmpty()) {
							List<CallData.TamedData> dataList = new ArrayList<>();
							for (int i = 0; i < dataListTag.size(); ++i) {
								CompoundTag dataTag = dataListTag.getCompound(i);
								CallData.TamedData tamedData = CallData.TamedData.load(dataTag);
								if (tamedData != null) {
									dataList.add(tamedData);
								}
							}
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
