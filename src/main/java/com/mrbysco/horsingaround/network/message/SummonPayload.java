package com.mrbysco.horsingaround.network.message;

import com.mrbysco.horsingaround.HorsingAround;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record SummonPayload(UUID mobUUID) implements CustomPacketPayload {

	public static final StreamCodec<FriendlyByteBuf, SummonPayload> CODEC = CustomPacketPayload.codec(
			SummonPayload::write,
			SummonPayload::new);
	public static final Type<SummonPayload> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(HorsingAround.MOD_ID, "summon"));

	public SummonPayload(final FriendlyByteBuf buf) {
		this(buf.readUUID());
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeUUID(this.mobUUID);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
