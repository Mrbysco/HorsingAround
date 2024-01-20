package com.mrbysco.horsingaround.datagen.client;

import com.mrbysco.horsingaround.HorsingAround;
import com.mrbysco.horsingaround.registry.HorsingRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredHolder;

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

		add("horsingaround.networking.summon.failed", "Failed to summon tamed entity %s");
		add("horsingaround.networking.unlink.failed", "Failed to unlink tamed entity %s");
		add("horsingaround.networking.sync.failed", "Failed to sync tamed entities %s");
	}

	/**
	 * Add a subtitle to a sound event
	 *
	 * @param sound The sound event registry object
	 * @param text  The subtitle text
	 */
	public void addSubtitle(DeferredHolder<SoundEvent, SoundEvent> sound, String text) {
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
