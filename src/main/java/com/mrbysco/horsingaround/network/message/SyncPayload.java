package com.mrbysco.horsingaround.network.message;

import com.mrbysco.horsingaround.HorsingAround;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record SyncPayload(UUID playerUUID, CompoundTag data) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(HorsingAround.MOD_ID, "sync");

	public SyncPayload(final FriendlyByteBuf buf) {
		this(buf.readUUID(), buf.readNbt());
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeUUID(this.playerUUID);
		buf.writeNbt(this.data);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}
}