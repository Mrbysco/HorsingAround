package com.mrbysco.horsingaround.network.message;

import com.mrbysco.horsingaround.HorsingAround;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record SyncPayload(UUID playerUUID, CompoundTag data) implements CustomPacketPayload {

	public static final StreamCodec<FriendlyByteBuf, SyncPayload> CODEC = CustomPacketPayload.codec(
			SyncPayload::write,
			SyncPayload::new);
	public static final Type<SyncPayload> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(HorsingAround.MOD_ID, "sync"));

	public SyncPayload(final FriendlyByteBuf buf) {
		this(buf.readUUID(), buf.readNbt());
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeUUID(this.playerUUID);
		buf.writeNbt(this.data);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
