package com.mrbysco.horsingaround.config;

import com.mrbysco.horsingaround.HorsingAround;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

public class HorsingConfig {
	public static class Common {
		public final ForgeConfigSpec.BooleanValue addOnMount;

		Common(ForgeConfigSpec.Builder builder) {
			builder.comment("General settings")
					.push("General");

			addOnMount = builder
					.comment("Add tamed mountable entities to the call list [Default: false]")
					.define("addOnMount", false);

			builder.pop();
		}
	}

	public static final ForgeConfigSpec commonSpec;
	public static final Common COMMON;

	static {
		final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
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
