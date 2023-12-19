package com.mrbysco.horsingaround.datagen.client;

import com.mrbysco.horsingaround.HorsingAround;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class HorsingLanguageProvider extends LanguageProvider {
	public HorsingLanguageProvider(PackOutput packOutput) {
		super(packOutput, HorsingAround.MOD_ID, "en_us");
	}

	@Override
	protected void addTranslations() {
		add("key.horsingaround.category", "Horsing Around");
		add("key.horsingaround.open_menu", "Open Call Menu");

		add("message.horsingaround.no_tamed_entities", "You don't have any tamed entities added to your list");
	}
}
