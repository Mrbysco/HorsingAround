package com.mrbysco.horsingaround.util;

import com.mrbysco.horsingaround.data.CallData;
import com.mrbysco.horsingaround.registry.HorsingRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

public class CallUtil {
	/**
	 * Calls an animal to the specified player.
	 *
	 * @param serverPlayer The player to call the animal to.
	 * @param mobUUID      The UUID of the animal to call.
	 */
	public static void callAnimal(ServerPlayer serverPlayer, UUID mobUUID) {
		ServerLevel targetLevel = serverPlayer.serverLevel();
		CallData callData = CallData.get(targetLevel);

		Entity mob = findEntity(targetLevel.getServer(), mobUUID);

		if (mob == null) {
			List<CallData.TamedData> tamedList = callData.getTamedData(serverPlayer.getUUID());
			CallData.TamedData matchingData = null;
			for (CallData.TamedData data : tamedList) {
				if (data.uuid().equals(mobUUID)) {
					matchingData = data;
					break;
				}
			}

			if (matchingData != null) {
				Entity entity = matchingData.createEntity(targetLevel);
				entity.setPos(serverPlayer.position());
				targetLevel.addFreshEntity(entity);
				callData.updateData(entity.getUUID(), entity);
			}
		} else {
			if (mob.level() != targetLevel) {
				Entity moved = mob.changeDimension(
						new DimensionTransition(
								targetLevel, serverPlayer.position(), Vec3.ZERO,
								mob.getYRot(), mob.getXRot(), false, DimensionTransition.DO_NOTHING
						)
				);
				if (moved != null) {
					moved.teleportTo(serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ());
					callData.updateData(moved.getUUID(), moved);
				}
			} else {
				mob.teleportTo(serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ());
				callData.updateData(mob.getUUID(), mob);
			}
		}

		callData.syncData(serverPlayer.getUUID());

		targetLevel.playSound((Player) null, serverPlayer.blockPosition(), HorsingRegistry.CALL.get(), serverPlayer.getSoundSource(),
				1.0F, 1.0F + (targetLevel.random.nextFloat() - targetLevel.random.nextFloat()) * 0.4F);
	}

	/**
	 * Finds an entity by its UUID across all server levels.
	 *
	 * @param server  The Minecraft server instance.
	 * @param mobUUID The UUID of the entity to find.
	 * @return The entity if found, otherwise null.
	 */
	public static Entity findEntity(MinecraftServer server, UUID mobUUID) {
		for (ServerLevel level : server.getAllLevels()) {
			Entity entity = level.getEntity(mobUUID);
			if (entity != null) {
				return entity;
			}
		}
		return null;
	}
}
