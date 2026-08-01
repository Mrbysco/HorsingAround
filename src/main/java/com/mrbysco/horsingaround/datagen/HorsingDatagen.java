package com.mrbysco.horsingaround.datagen;

import com.mrbysco.horsingaround.datagen.client.HorsingLanguageProvider;
import com.mrbysco.horsingaround.datagen.client.HorsingSoundProvider;
import com.mrbysco.horsingaround.datagen.server.HorsingEntityTypeTagProvider;
import com.mrbysco.horsingaround.datagen.server.HorsingItemTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public class HorsingDatagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Client event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		generator.addProvider(true, new HorsingItemTagProvider(packOutput, lookupProvider));
		generator.addProvider(true, new HorsingEntityTypeTagProvider(packOutput, lookupProvider));

		generator.addProvider(true, new HorsingLanguageProvider(packOutput));
		generator.addProvider(true, new HorsingSoundProvider(packOutput));
	}
}
