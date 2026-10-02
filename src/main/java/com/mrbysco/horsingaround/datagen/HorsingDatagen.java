package com.mrbysco.horsingaround.datagen;

import com.mrbysco.horsingaround.datagen.client.HorsingLanguageProvider;
import com.mrbysco.horsingaround.datagen.client.HorsingSoundProvider;
import com.mrbysco.horsingaround.datagen.server.HorsingEntityTypeTagProvider;
import com.mrbysco.horsingaround.datagen.server.HorsingItemTagProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber
public class HorsingDatagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Client event) {
		event.createProvider(HorsingItemTagProvider::new);
		event.createProvider(HorsingEntityTypeTagProvider::new);

		event.createProvider(HorsingLanguageProvider::new);
		event.createProvider(HorsingSoundProvider::new);
	}
}
