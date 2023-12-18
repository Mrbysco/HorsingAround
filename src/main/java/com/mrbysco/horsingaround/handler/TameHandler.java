package com.mrbysco.horsingaround.handler;

import com.mrbysco.horsingaround.HorsingAround;
import com.mrbysco.horsingaround.config.HorsingConfig;
import com.mrbysco.horsingaround.data.CallData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

public class TameHandler {

	@SubscribeEvent
	public void onMount(EntityMountEvent event) {
		if (event.isMounting() && HorsingConfig.COMMON.addOnMount.get()) {
			if (event.getEntityMounting() instanceof Player player && !player.level().isClientSide) {
				Entity mountedEntity = event.getEntityBeingMounted();
				if (mountedEntity instanceof OwnableEntity ownableEntity &&
						ownableEntity.getOwnerUUID() != null && ownableEntity.getOwnerUUID().equals(player.getUUID())) {
					CallData callData = CallData.get(player.level());
					callData.addTamedData(player.getUUID(), mountedEntity);
				}
			}
		}
	}


	@SubscribeEvent
	public void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
		Player player = event.getEntity();
		if (!player.level().isClientSide) {
			Entity targetEntity = event.getTarget();
			ItemStack stack = event.getItemStack();
			if (stack.is(HorsingAround.LINKING) && event.getHand() == InteractionHand.MAIN_HAND &&
					targetEntity instanceof OwnableEntity ownableEntity &&
					ownableEntity.getOwnerUUID() != null && ownableEntity.getOwnerUUID().equals(player.getUUID())) {
				CallData callData = CallData.get(player.level());
				callData.addTamedData(player.getUUID(), targetEntity);

				if (!player.getAbilities().instabuild)
					stack.shrink(1);
			}
		}
	}

	@SubscribeEvent
	public void onDeath(LivingDeathEvent event) {
		LivingEntity livingEntity = event.getEntity();
		if (!livingEntity.level().isClientSide) {
			if (event.getEntity() instanceof OwnableEntity ownableEntity) {
				UUID ownerUUID = ownableEntity.getOwnerUUID();
				if (ownerUUID != null) {
					CallData callData = CallData.get(livingEntity.level());
					callData.removeTamedData(ownerUUID, livingEntity);
				}
			}
		}
	}
}
