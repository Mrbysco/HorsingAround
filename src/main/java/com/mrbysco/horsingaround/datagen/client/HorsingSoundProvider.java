package com.mrbysco.horsingaround.datagen.client;

import com.mrbysco.horsingaround.HorsingAround;
import com.mrbysco.horsingaround.registry.HorsingRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class HorsingSoundProvider extends SoundDefinitionsProvider {
	public HorsingSoundProvider(PackOutput packOutput, ExistingFileHelper helper) {
		super(packOutput, HorsingAround.MOD_ID, helper);
	}

	@Override
	public void registerSounds() {
		this.add(HorsingRegistry.CALL, definition()
				.subtitle(modSubtitle(HorsingRegistry.CALL.getId()))
				.with(sound(modLoc("whistle"))));
	}

	public String modSubtitle(ResourceLocation id) {
		return HorsingAround.MOD_ID + ".subtitle." + id.getPath();
	}

	public ResourceLocation modLoc(String name) {
		return ResourceLocation.fromNamespaceAndPath(HorsingAround.MOD_ID, name);
	}
}
