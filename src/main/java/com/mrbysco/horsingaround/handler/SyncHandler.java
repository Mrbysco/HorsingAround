package com.mrbysco.horsingaround.handler;

import com.mrbysco.horsingaround.data.CallData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

public class SyncHandler {
	@SubscribeEvent
	public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
		Player player = event.getEntity();
		if (!player.level().isClientSide) {
			CallData callData = CallData.get(player.level());
			callData.syncData(player.getUUID());
		}
	}

	@SubscribeEvent
	public void onEntityTick(EntityTickEvent.Post event) {
		Entity entity = event.getEntity();
		if (!entity.level().isClientSide && entity.tickCount % 80 == 0 &&
				entity instanceof OwnableEntity ownableEntity && ownableEntity.getOwnerUUID() != null) {
			CallData callData = CallData.get(entity.level());
			if (callData.isKnown(entity.getUUID())) {
				callData.updateData(entity.getUUID(), entity);
			}
		}
	}
}
