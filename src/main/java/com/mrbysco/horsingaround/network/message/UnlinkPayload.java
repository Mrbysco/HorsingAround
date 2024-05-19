package com.mrbysco.horsingaround.network.message;

import com.mrbysco.horsingaround.HorsingAround;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record UnlinkPayload(UUID mobUUID) implements CustomPacketPayload {

	public static final StreamCodec<FriendlyByteBuf, UnlinkPayload> CODEC = CustomPacketPayload.codec(
			UnlinkPayload::write,
			UnlinkPayload::new);
	public static final Type<UnlinkPayload> ID = CustomPacketPayload.createType(new ResourceLocation(HorsingAround.MOD_ID, "unlink").toString());

	public UnlinkPayload(final FriendlyByteBuf buf) {
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
