package com.mrbysco.horsingaround.handler;

import com.mrbysco.horsingaround.data.CallData;
import net.minecraft.world.entity.player.Player;
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
}
