package com.mrbysco.horsingaround.handler;

import com.mrbysco.horsingaround.data.CallData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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
	public void onEntityTick(LivingEvent.LivingTickEvent event) {
		LivingEntity livingEntity = event.getEntity();
		if (!livingEntity.level().isClientSide && livingEntity.tickCount % 80 == 0 &&
				livingEntity instanceof OwnableEntity ownableEntity && ownableEntity.getOwnerUUID() != null) {
			CallData callData = CallData.get(livingEntity.level());
			if (callData.isKnown(livingEntity.getUUID())) {
				callData.updateData(livingEntity.getUUID(), livingEntity);
			}
		}
	}
}
