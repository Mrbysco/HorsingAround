package com.mrbysco.horsingaround.datagen.server;

import com.mrbysco.horsingaround.HorsingAround;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class HorsingEntityTypeTagProvider extends EntityTypeTagsProvider {
	public HorsingEntityTypeTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider,
	                                    ExistingFileHelper existingFileHelper) {
		super(packOutput, lookupProvider, HorsingAround.MOD_ID, existingFileHelper);
	}

	@Override
	public void addTags(HolderLookup.Provider lookupProvider) {
		this.tag(HorsingAround.BLACKLIST);
	}
}
