package com.mrbysco.horsingaround.network;

import com.mrbysco.horsingaround.HorsingAround;
import com.mrbysco.horsingaround.network.message.SummonMessage;
import com.mrbysco.horsingaround.network.message.SyncDataMessage;
import com.mrbysco.horsingaround.network.message.UnlinkMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {

	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(HorsingAround.MOD_ID, "main"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);

	private static int id = 0;

	public static void init() {
		CHANNEL.registerMessage(id++, SyncDataMessage.class, SyncDataMessage::encode, SyncDataMessage::decode, SyncDataMessage::handle);
		CHANNEL.registerMessage(id++, SummonMessage.class, SummonMessage::encode, SummonMessage::decode, SummonMessage::handle);
		CHANNEL.registerMessage(id++, UnlinkMessage.class, UnlinkMessage::encode, UnlinkMessage::decode, UnlinkMessage::handle);
	}
}
