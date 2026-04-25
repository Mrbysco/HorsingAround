package com.mrbysco.horsingaround.data;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrbysco.horsingaround.HorsingAround;
import com.mrbysco.horsingaround.network.message.SyncPayload;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.SavedDataStorage;
import net.minecraft.world.level.storage.TagValueOutput;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class CallData extends SavedData {
	private static final Identifier DATA_NAME = Identifier.fromNamespaceAndPath(HorsingAround.MOD_ID, "call_data");

	public static final Codec<CallData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
					Codec.unboundedMap(UUIDUtil.STRING_CODEC, TamedData.CODEC.listOf()).fieldOf("playerTamedMap").forGetter(data -> data.playerTamedMap))
			.apply(inst, CallData::new));

	private final Map<UUID, List<TamedData>> playerTamedMap;

	public CallData() {
		this(Maps.newHashMap());
	}

	public CallData(Map<UUID, List<TamedData>> infoMap) {
		this.playerTamedMap = Maps.newHashMap(infoMap);
	}

	public void addTamedData(UUID playerUUID, Entity entity) {
		List<TamedData> dataList = getTamedData(playerUUID);
		dataList = new ArrayList<>(dataList); // Create a mutable copy
		boolean known = dataList.stream().anyMatch(tamedData -> tamedData.uuid().equals(entity.getUUID()));
		if (!known) {
			TamedData tamedData = TamedData.createData(entity.getUUID(), entity);
			List<TamedData> tameList = playerTamedMap.getOrDefault(playerUUID, new ArrayList<>());
			tameList = new ArrayList<>(tameList); // Create a mutable copy
			tameList.add(tamedData);
			playerTamedMap.put(playerUUID, tameList);
		} else {
			//Update data if it already exists
			dataList.stream().filter(tamedData -> tamedData.uuid().equals(entity.getUUID())).findFirst().ifPresent(tamedData -> {
				try (ProblemReporter.ScopedCollector problemreporter$scopedcollector = new ProblemReporter.ScopedCollector(HorsingAround.LOGGER)) {
					TagValueOutput output = TagValueOutput.createWithContext(problemreporter$scopedcollector, entity.registryAccess());
					entity.saveAsPassenger(output);
					tamedData.tag().merge(output.buildResult());
				}
			});
		}

		//Sync data to client
		syncData(playerUUID);

		setDirty();
	}

	public void removeTamedData(UUID playerUUID, Entity entity) {
		removeTamedData(playerUUID, entity.getUUID());
	}

	public void removeTamedData(UUID playerUUID, UUID entityUUID) {
		List<TamedData> tamedDataList = playerTamedMap.get(playerUUID);
		tamedDataList.removeIf(tamedData -> tamedData.uuid().equals(entityUUID));

		//Sync data to client
		syncData(playerUUID);

		setDirty();
	}

	public List<TamedData> getTamedData(UUID playerUUID) {
		return playerTamedMap.getOrDefault(playerUUID, new ArrayList<>());
	}

	public boolean hasTamedData(UUID playerUUID) {
		return playerTamedMap.containsKey(playerUUID);
	}

	public boolean isKnown(UUID uuid) {
		for (List<TamedData> tamedDataList : playerTamedMap.values()) {
			if (tamedDataList.stream().anyMatch(tamedData -> tamedData.uuid().equals(uuid))) {
				return true;
			}
		}
		return false;
	}

	public void updateData(UUID uuid, Entity entity) {
		for (List<TamedData> tamedDataList : playerTamedMap.values()) {
			tamedDataList.stream().filter(tamedData -> tamedData.uuid().equals(uuid)).findFirst().ifPresent(tamedData -> {
				try (ProblemReporter.ScopedCollector problemreporter$scopedcollector = new ProblemReporter.ScopedCollector(HorsingAround.LOGGER)) {
					TagValueOutput output = TagValueOutput.createWithContext(problemreporter$scopedcollector, entity.registryAccess());
					entity.saveAsPassenger(output);
					tamedData.tag().merge(output.buildResult());
				}
			});
		}
		setDirty();
	}

	public void syncData(UUID playerUUID) {
		MinecraftServer server = Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer(), "Cannot send clientbound payloads on the client");
		ServerPlayer serverPlayer = server.getPlayerList().getPlayer(playerUUID);
		if (serverPlayer == null) {
			HorsingAround.LOGGER.warn("Tried to sync tamed data for player {} but they are not online", playerUUID);
			return;
		}
		List<TamedData> tamedDataList = playerTamedMap.getOrDefault(playerUUID, new ArrayList<>());
		PacketDistributor.sendToPlayer(serverPlayer, new SyncPayload(playerUUID, tamedDataList));
	}

	public static SavedDataType<CallData> type() {
		return new SavedDataType<>(DATA_NAME, CallData::new, CODEC, null);
	}

	public static CallData get(Level world) {
		if (!(world instanceof ServerLevel)) {
			throw new RuntimeException("Attempted to get the data from a client world. This is wrong.");
		}
		ServerLevel overworld = world.getServer().getLevel(Level.OVERWORLD);

		assert overworld != null;
		SavedDataStorage storage = overworld.getDataStorage();
		return storage.computeIfAbsent(type());
	}

	public record TamedData(UUID uuid, CompoundTag tag, String name) {
		public static final Codec<TamedData> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
								UUIDUtil.CODEC.fieldOf("uuid").forGetter(data -> data.uuid),
								CompoundTag.CODEC.fieldOf("tag").forGetter(data -> data.tag),
								Codec.STRING.optionalFieldOf("name", "").forGetter(data -> data.name)
						)
						.apply(instance, TamedData::new)
		);
		public static final StreamCodec<FriendlyByteBuf, TamedData> STREAM_CODEC = StreamCodec.composite(
				UUIDUtil.STREAM_CODEC,
				data -> data.uuid,
				ByteBufCodecs.COMPOUND_TAG,
				data -> data.tag,
				ByteBufCodecs.STRING_UTF8,
				data -> data.name,
				TamedData::new
		);

		public static TamedData createData(UUID uuid, Entity entity) {
			try (ProblemReporter.ScopedCollector problemreporter$scopedcollector = new ProblemReporter.ScopedCollector(HorsingAround.LOGGER)) {
				TagValueOutput output = TagValueOutput.createWithContext(problemreporter$scopedcollector, entity.registryAccess());
				entity.saveAsPassenger(output);
				return new TamedData(uuid, output.buildResult(), entity.getDisplayName().getString());
			}
		}

		public Entity createEntity(Level level) {
			return EntityType.loadEntityRecursive(tag, level, EntitySpawnReason.MOB_SUMMONED, (entity) -> {
				entity.setUUID(uuid);
				return entity;
			});
		}
	}
}
