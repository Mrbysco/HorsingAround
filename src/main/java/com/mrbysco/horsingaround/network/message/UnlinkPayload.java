package com.mrbysco.horsingaround.network.message;

import com.mrbysco.horsingaround.HorsingAround;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record UnlinkPayload(UUID mobUUID) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(HorsingAround.MOD_ID, "unlink");

	public UnlinkPayload(final FriendlyByteBuf buf) {
		this(buf.readUUID());
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeUUID(this.mobUUID);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}
}
