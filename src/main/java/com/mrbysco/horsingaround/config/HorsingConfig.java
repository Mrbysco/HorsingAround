package com.mrbysco.horsingaround.config;

import com.mrbysco.horsingaround.HorsingAround;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class HorsingConfig {

	public static class Client {
		public final ModConfigSpec.IntValue hoverColor;
		public final ModConfigSpec.IntValue slotsVisible;

		Client(ModConfigSpec.Builder builder) {
			builder.comment("Client settings")
					.push("Client");

			hoverColor = builder
					.comment("The integer color of the radial menu when hovering a slice [Default: 4170175]")
					.defineInRange("hoverColor", 0x3FA1BF, 0x000000, 0xFFFFFF);

			slotsVisible = builder
					.comment("The max number of tamed entities visible in the radial menu [Default: 20]")
					.defineInRange("slotsVisible", 20, 1, 100);

			builder.pop();
		}
	}

	public static final ModConfigSpec clientSpec;
	public static final Client CLIENT;

	static {
		final Pair<Client, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Client::new);
		clientSpec = specPair.getRight();
		CLIENT = specPair.getLeft();
	}

	public static class Common {
		public final ModConfigSpec.BooleanValue addOnMount;
		public final ModConfigSpec.BooleanValue addOnTame;
		public final ModConfigSpec.ConfigValue<List<? extends String>> entityBlacklist;

		Common(ModConfigSpec.Builder builder) {
			builder.comment("General settings")
					.push("General");

			addOnMount = builder
					.comment("Add tamed mountable entities to the call list upon mounting [Default: false]")
					.define("addOnMount", false);
			addOnTame = builder
					.comment("Add mountable entities to the call list upon being tamed [Default: false]")
					.define("addOnTame", false);
			entityBlacklist = builder
					.comment("A list of entity ids that are prohibited from being called")
					.defineListAllowEmpty("entityBlacklist", List::of, () -> "", o -> (o instanceof String));

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
