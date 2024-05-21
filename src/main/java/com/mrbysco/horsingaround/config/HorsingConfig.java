package com.mrbysco.horsingaround.config;

import com.mrbysco.horsingaround.HorsingAround;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

public class HorsingConfig {
	public static class Common {
		public final ModConfigSpec.BooleanValue addOnMount;
		public final ModConfigSpec.BooleanValue addOnTame;

		Common(ModConfigSpec.Builder builder) {
			builder.comment("General settings")
					.push("General");

			addOnMount = builder
					.comment("Add tamed mountable entities to the call list upon mounting [Default: false]")
					.define("addOnMount", false);
			addOnTame = builder
					.comment("Add mountable entities to the call list upon being tamed [Default: false]")
					.define("addOnTame", false);

			builder.pop();
		}
	}

	public static final ModConfigSpec commonSpec;
	public static final Common COMMON;

	static {
		final Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
		commonSpec = specPair.getRight();
		COMMON = specPair.getLeft();
	}

	@SubscribeEvent
	public static void onLoad(final ModConfigEvent.Loading configEvent) {
		HorsingAround.LOGGER.debug("Loaded Horsing Around's config file {}", configEvent.getConfig().getFileName());
	}

	@SubscribeEvent
	public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
		HorsingAround.LOGGER.debug("Horsing Around's config just got changed on the file system!");
	}
}
