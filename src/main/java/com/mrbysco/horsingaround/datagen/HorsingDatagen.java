package com.mrbysco.horsingaround.datagen;

import com.mrbysco.horsingaround.HorsingAround;
import com.mrbysco.horsingaround.datagen.client.HorsingLanguageProvider;
import com.mrbysco.horsingaround.datagen.client.HorsingSoundProvider;
import com.mrbysco.horsingaround.datagen.server.HorsingItemTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class HorsingDatagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Client event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		BlockTagsProvider tagProvider = new BlockTagsProvider(packOutput, lookupProvider, HorsingAround.MOD_ID) {
			@Override
			protected void addTags(HolderLookup.Provider provider) {

			}
		};
		generator.addProvider(true, tagProvider);
		generator.addProvider(true, new HorsingItemTagProvider(packOutput, lookupProvider, tagProvider));

		generator.addProvider(true, new HorsingLanguageProvider(packOutput));
		generator.addProvider(true, new HorsingSoundProvider(packOutput));
	}
}
