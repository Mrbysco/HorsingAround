package com.mrbysco.horsingaround.data;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.mrbysco.horsingaround.HorsingAround;
import com.mrbysco.horsingaround.network.PacketHandler;
import com.mrbysco.horsingaround.network.message.SyncDataMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CallData extends SavedData {
	private final ListMultimap<UUID, TamedData> playerTamedMap = ArrayListMultimap.create();

	private static final String DATA_NAME = HorsingAround.MOD_ID + "_data";

	public CallData(ListMultimap<UUID, TamedData> tamedMap) {
		this.playerTamedMap.clear();
		if (!tamedMap.isEmpty()) {
			this.playerTamedMap.putAll(tamedMap);
		}
	}

	public CallData() {
		this(ArrayListMultimap.create());
	}

	public void addTamedData(UUID playerUUID, Entity entity) {
		List<TamedData> dataList = getTamedData(playerUUID);
		boolean known = dataList.stream().anyMatch(tamedData -> tamedData.uuid().equals(entity.getUUID()));
		if (!known) {
			TamedData tamedData = TamedData.createData(entity.getUUID(), entity);
			playerTamedMap.put(playerUUID, tamedData);
		} else {
			//Update data if it already exists
			dataList.stream().filter(tamedData -> tamedData.uuid().equals(entity.getUUID())).findFirst().ifPresent(tamedData -> {
				CompoundTag data = entity.saveWithoutId(tamedData.tag());
				data.putString("id", EntityType.getKey(entity.getType()).toString());
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
		return playerTamedMap.get(playerUUID);
	}

	public boolean hasTamedData(UUID playerUUID) {
		return playerTamedMap.containsKey(playerUUID);
	}

	public boolean isKnown(UUID uuid) {
		return playerTamedMap.values().stream().anyMatch(tamedData -> tamedData.uuid().equals(uuid));
	}

	public void updateData(UUID uuid, Entity entity) {
		playerTamedMap.values().stream().filter(tamedData -> tamedData.uuid().equals(uuid)).findFirst().ifPresent(tamedData -> {
			CompoundTag data = entity.saveWithoutId(tamedData.tag());
			data.putString("id", EntityType.getKey(entity.getType()).toString());
		});
	}

	public void syncData(UUID playerUUID) {
		List<TamedData> tamedDataList = playerTamedMap.get(playerUUID);
		CompoundTag tag = new CompoundTag();
		saveList(tag, playerUUID, tamedDataList);

		PacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new SyncDataMessage(playerUUID, tag));
	}

	public static CallData load(CompoundTag tag) {
		ListMultimap<UUID, TamedData> tamedMap = ArrayListMultimap.create();
		for (String uuid : tag.getAllKeys()) {
			ListTag dataListTag = new ListTag();
			if (tag.getTagType(uuid) == 9) {
				Tag nbt = tag.get(uuid);
				if (nbt instanceof ListTag listNBT) {
					if (!listNBT.isEmpty() && listNBT.getElementType() != CompoundTag.TAG_COMPOUND) {
						continue;
					}

					dataListTag = listNBT;
				}
			}
			if (!dataListTag.isEmpty()) {
				List<TamedData> dataList = new ArrayList<>();
				for (int i = 0; i < dataListTag.size(); ++i) {
					CompoundTag dataTag = dataListTag.getCompound(i);
					TamedData data = TamedData.load(dataTag);
					if (data != null) {
						dataList.add(data);
					}
				}
				tamedMap.putAll(UUID.fromString(uuid), dataList);
			}
		}
		return new CallData(tamedMap);
	}

	@Override
	public CompoundTag save(CompoundTag compound) {
		for (UUID playerUUID : playerTamedMap.keySet()) {
			List<TamedData> tamedDataList = playerTamedMap.get(playerUUID);

			saveList(compound, playerUUID, tamedDataList);
		}
		return compound;
	}

	public void saveList(CompoundTag tag, UUID playerUUID, List<TamedData> dataList) {
		ListTag dataListTag = new ListTag();
		for (TamedData tamedData : dataList) {
			CompoundTag data = new CompoundTag();
			tamedData.save(data);
			dataListTag.add(data);
		}
		tag.put(playerUUID.toString(), dataListTag);
	}

	public static CallData get(Level world) {
		if (!(world instanceof ServerLevel)) {
			throw new RuntimeException("Attempted to get the data from a client world. This is wrong.");
		}
		ServerLevel overworld = world.getServer().getLevel(Level.OVERWORLD);

		DimensionDataStorage storage = overworld.getDataStorage();
		return storage.computeIfAbsent(CallData::load, CallData::new, DATA_NAME);
	}

	public record TamedData(UUID uuid, CompoundTag tag, String name) {

		public static TamedData createData(UUID uuid, Entity entity) {
			CompoundTag data = entity.saveWithoutId(new CompoundTag());
			data.putString("id", EntityType.getKey(entity.getType()).toString());
			return new TamedData(uuid, data, entity.getDisplayName().getString());
		}

		public void save(CompoundTag compound) {
			compound.putUUID("UUID", uuid);
			compound.put("Tag", tag);
			compound.putString("Name", name);
		}

		public Entity createEntity(Level level) {
			return EntityType.loadEntityRecursive(tag, level, (entity) -> {
				entity.setUUID(uuid);
				return entity;
			});
		}

		public static TamedData load(CompoundTag compound) {
			if (!compound.contains("UUID") || !compound.contains("Tag")) {
				return null;
			}
			UUID uuid = compound.getUUID("UUID");
			CompoundTag tag = compound.getCompound("Tag");
			String name = compound.getString("Name");
			return new TamedData(uuid, tag, name);
		}
	}
}
