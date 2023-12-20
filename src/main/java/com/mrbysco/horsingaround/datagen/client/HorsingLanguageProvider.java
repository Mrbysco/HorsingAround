package com.mrbysco.horsingaround.datagen.client;

import com.mrbysco.horsingaround.HorsingAround;
import com.mrbysco.horsingaround.registry.HorsingRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;

public class HorsingLanguageProvider extends LanguageProvider {
	public HorsingLanguageProvider(PackOutput packOutput) {
		super(packOutput, HorsingAround.MOD_ID, "en_us");
	}

	@Override
	protected void addTranslations() {
		add("key.horsingaround.category", "Horsing Around");
		add("key.horsingaround.open_menu", "Open Call Menu");

		addSubtitle(HorsingRegistry.CALL, "Calling companion");

		add("message.horsingaround.no_tamed_entities", "You don't have any tamed entities added to your list");
	}

	/**
	 * Add a subtitle to a sound event
	 *
	 * @param sound The sound event registry object
	 * @param text  The subtitle text
	 */
	public void addSubtitle(RegistryObject<SoundEvent> sound, String text) {
		this.addSubtitle(sound.get(), text);
	}

	/**
	 * Add a subtitle to a sound event
	 *
	 * @param sound The sound event
	 * @param text  The subtitle text
	 */
	public void addSubtitle(SoundEvent sound, String text) {
		String path = HorsingAround.MOD_ID + ".subtitle." + sound.getLocation().getPath();
		this.add(path, text);
	}
}
