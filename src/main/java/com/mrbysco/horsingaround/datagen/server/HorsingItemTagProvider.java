package com.mrbysco.horsingaround.datagen.server;

import com.mrbysco.horsingaround.HorsingAround;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class HorsingItemTagProvider extends ItemTagsProvider {
	public HorsingItemTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(packOutput, lookupProvider, HorsingAround.MOD_ID);
	}

	@Override
	public void addTags(HolderLookup.Provider lookupProvider) {
		this.tag(HorsingAround.LINKING).add(Items.GOLDEN_APPLE);
	}
}
