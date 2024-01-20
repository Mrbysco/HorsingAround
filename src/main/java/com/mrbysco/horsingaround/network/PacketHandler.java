package com.mrbysco.horsingaround.network;

import com.mrbysco.horsingaround.HorsingAround;
import com.mrbysco.horsingaround.network.handler.ClientPayloadHandler;
import com.mrbysco.horsingaround.network.handler.ServerPayloadHandler;
import com.mrbysco.horsingaround.network.message.SummonPayload;
import com.mrbysco.horsingaround.network.message.SyncPayload;
import com.mrbysco.horsingaround.network.message.UnlinkPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class PacketHandler {

	public static void setupPackets(final RegisterPayloadHandlerEvent event) {
		final IPayloadRegistrar registrar = event.registrar(HorsingAround.MOD_ID);

		registrar.play(SyncPayload.ID, SyncPayload::new, handler -> handler
				.client(ClientPayloadHandler.getInstance()::handleSync));
		registrar.play(SummonPayload.ID, SummonPayload::new, handler -> handler
				.server(ServerPayloadHandler.getInstance()::handleSummon));
		registrar.play(UnlinkPayload.ID, UnlinkPayload::new, handler -> handler
				.server(ServerPayloadHandler.getInstance()::handleUnlink));
	}
}
