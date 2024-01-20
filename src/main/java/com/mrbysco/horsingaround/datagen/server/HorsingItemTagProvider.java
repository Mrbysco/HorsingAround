package com.mrbysco.horsingaround.datagen.server;

import com.mrbysco.horsingaround.HorsingAround;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class HorsingItemTagProvider extends ItemTagsProvider {
	public HorsingItemTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider,
								  TagsProvider<Block> blockTagProvider, ExistingFileHelper existingFileHelper) {
		super(packOutput, lookupProvider, blockTagProvider.contentsGetter(), HorsingAround.MOD_ID, existingFileHelper);
	}

	@Override
	public void addTags(HolderLookup.Provider lookupProvider) {
		this.tag(HorsingAround.LINKING).add(Items.GOLDEN_APPLE);
	}
}
