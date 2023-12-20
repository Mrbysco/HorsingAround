package com.mrbysco.horsingaround.network.message;

import com.mrbysco.horsingaround.client.ClientHandler;
import com.mrbysco.horsingaround.data.CallData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.network.NetworkEvent.Context;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class SyncDataMessage {
	private final UUID playerUUID;
	private final CompoundTag data;

	public SyncDataMessage(UUID playerUUID, CompoundTag data) {
		this.playerUUID = playerUUID;
		this.data = data;
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeUUID(this.playerUUID);
		buf.writeNbt(this.data);
	}

	public static SyncDataMessage decode(final FriendlyByteBuf buf) {
		return new SyncDataMessage(buf.readUUID(), buf.readNbt());
	}

	public void handle(Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide().isClient()) {
				SyncData.update(this.playerUUID, this.data).run();
			}
		});
		ctx.setPacketHandled(true);
	}

	private static class SyncData {
		private static SafeRunnable update(UUID playerUUID, CompoundTag data) {
			return new DistExecutor.SafeRunnable() {
				@Serial
				private static final long serialVersionUID = 1L;

				@Override
				public void run() {
					net.minecraft.world.entity.player.Player player = net.minecraft.client.Minecraft.getInstance().player;
					if (player != null && player.getUUID().equals(playerUUID)) {
						ClientHandler.tamedList.clear();

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
								CallData.TamedData data = CallData.TamedData.load(dataTag);
								if (data != null) {
									dataList.add(data);
								}
							}
							ClientHandler.tamedList.addAll(dataList);
						}
					}
				}
			};
		}
	}
}
