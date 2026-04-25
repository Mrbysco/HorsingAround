package com.mrbysco.horsingaround.datagen.client;

import com.mrbysco.horsingaround.HorsingAround;
import com.mrbysco.horsingaround.registry.HorsingRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class HorsingSoundProvider extends SoundDefinitionsProvider {
	public HorsingSoundProvider(PackOutput packOutput) {
		super(packOutput, HorsingAround.MOD_ID);
	}

	@Override
	public void registerSounds() {
		this.add(HorsingRegistry.CALL, definition()
				.subtitle(modSubtitle(HorsingRegistry.CALL.getId()))
				.with(sound(modLoc("whistle"))));
	}

	public String modSubtitle(Identifier id) {
		return HorsingAround.MOD_ID + ".subtitle." + id.getPath();
	}

	public Identifier modLoc(String name) {
		return Identifier.fromNamespaceAndPath(HorsingAround.MOD_ID, name);
	}
}
