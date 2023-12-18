package com.mrbysco.horsingaround.network.message;

import com.mrbysco.horsingaround.client.ClientHandler;
import com.mrbysco.horsingaround.data.CallData;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent.Context;

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
			if (ctx.getDirection().getReceptionSide().isClient() && FMLEnvironment.dist.isClient()) {
				Player player = Minecraft.getInstance().player;
				if (player != null && player.getUUID().equals(this.playerUUID)) {
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
		});
		ctx.setPacketHandled(true);
	}
}
