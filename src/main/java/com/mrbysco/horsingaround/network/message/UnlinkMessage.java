package com.mrbysco.horsingaround.network.message;

import com.mrbysco.horsingaround.data.CallData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.UUID;
import java.util.function.Supplier;

public class UnlinkMessage {
	private final UUID mobUUID;

	public UnlinkMessage(UUID mobUUID) {
		this.mobUUID = mobUUID;
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeUUID(this.mobUUID);
	}

	public static UnlinkMessage decode(final FriendlyByteBuf buf) {
		return new UnlinkMessage(buf.readUUID());
	}

	public void handle(Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide().isServer() && ctx.getSender() != null) {
				ServerPlayer player = ctx.getSender();
				CallData callData = CallData.get(player.level());
				callData.removeTamedData(player.getUUID(), mobUUID);
			}
		});
		ctx.setPacketHandled(true);
	}
}
