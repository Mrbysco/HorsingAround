package com.mrbysco.horsingaround.datagen;

import com.mrbysco.horsingaround.HorsingAround;
import com.mrbysco.horsingaround.datagen.client.HorsingLanguageProvider;
import com.mrbysco.horsingaround.datagen.client.HorsingSoundProvider;
import com.mrbysco.horsingaround.datagen.server.HorsingItemTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class HorsingDatagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		ExistingFileHelper helper = event.getExistingFileHelper();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		if (event.includeServer()) {
			BlockTagsProvider tagProvider = new BlockTagsProvider(packOutput, lookupProvider, HorsingAround.MOD_ID, helper) {
				@Override
				protected void addTags(HolderLookup.Provider provider) {

				}
			};
			generator.addProvider(true, tagProvider);
			generator.addProvider(true, new HorsingItemTagProvider(packOutput, lookupProvider, tagProvider, helper));
		}
		if (event.includeClient()) {
			generator.addProvider(true, new HorsingLanguageProvider(packOutput));
			generator.addProvider(true, new HorsingSoundProvider(packOutput, helper));
		}
	}
}
