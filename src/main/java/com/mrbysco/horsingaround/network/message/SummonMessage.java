package com.mrbysco.horsingaround.network.message;

import com.mrbysco.horsingaround.data.CallData;
import com.mrbysco.horsingaround.registry.HorsingRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class SummonMessage {
	private final UUID mobUUID;

	public SummonMessage(UUID mobUUID) {
		this.mobUUID = mobUUID;
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeUUID(this.mobUUID);
	}

	public static SummonMessage decode(final FriendlyByteBuf buf) {
		return new SummonMessage(buf.readUUID());
	}

	public void handle(Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide().isServer() && ctx.getSender() != null) {
				ServerPlayer player = ctx.getSender();
				ServerLevel level = player.serverLevel();

				Entity mob = level.getEntity(mobUUID);
				CallData callData = CallData.get(player.level());
				if (mob == null) {
					List<CallData.TamedData> tamedList = callData.getTamedData(player.getUUID());
					CallData.TamedData matchingData = null;
					for (CallData.TamedData data : tamedList) {
						if (data.uuid().equals(mobUUID)) {
							matchingData = data;
							break;
						}
					}

					if (matchingData != null) {
						Entity entity = matchingData.createEntity(player.level());
						entity.setPos(player.position());
						level.addFreshEntity(entity);
					}
				} else {
					mob.teleportTo(player.getX(), player.getY(), player.getZ());
					callData.updateData(mob.getUUID(), mob);
				}
				callData.syncData(player.getUUID());

				level.playSound((Player) null, player.blockPosition(), HorsingRegistry.CALL.get(), player.getSoundSource(),
						1.0F, 1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);
			}
		});
		ctx.setPacketHandled(true);
	}
}
