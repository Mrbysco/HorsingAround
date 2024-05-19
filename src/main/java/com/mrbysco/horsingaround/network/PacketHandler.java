package com.mrbysco.horsingaround.network;

import com.mrbysco.horsingaround.HorsingAround;
import com.mrbysco.horsingaround.network.handler.ClientPayloadHandler;
import com.mrbysco.horsingaround.network.handler.ServerPayloadHandler;
import com.mrbysco.horsingaround.network.message.SummonPayload;
import com.mrbysco.horsingaround.network.message.SyncPayload;
import com.mrbysco.horsingaround.network.message.UnlinkPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class PacketHandler {

	public static void setupPackets(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar(HorsingAround.MOD_ID);

		registrar.playToClient(SyncPayload.ID, SyncPayload.CODEC, ClientPayloadHandler.getInstance()::handleSync);
		registrar.playToServer(SummonPayload.ID, SummonPayload.CODEC, ServerPayloadHandler.getInstance()::handleSummon);
		registrar.playToServer(UnlinkPayload.ID, UnlinkPayload.CODEC, ServerPayloadHandler.getInstance()::handleUnlink);
	}
}
