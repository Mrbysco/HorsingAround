package com.mrbysco.horsingaround.network.message;

import com.mrbysco.horsingaround.HorsingAround;
import com.mrbysco.horsingaround.data.CallData.TamedData;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.UUID;

public record SyncPayload(UUID playerUUID, List<TamedData> tamedDataList) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, SyncPayload> CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC,
			SyncPayload::playerUUID,
			TamedData.STREAM_CODEC.apply(ByteBufCodecs.list()),
			SyncPayload::tamedDataList,
			SyncPayload::new);
	public static final Type<SyncPayload> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(HorsingAround.MOD_ID, "sync"));

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
