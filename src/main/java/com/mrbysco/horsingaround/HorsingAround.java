package com.mrbysco.horsingaround;

import com.mojang.logging.LogUtils;
import com.mrbysco.horsingaround.client.ClientHandler;
import com.mrbysco.horsingaround.client.KeybindHandler;
import com.mrbysco.horsingaround.config.HorsingConfig;
import com.mrbysco.horsingaround.handler.SyncHandler;
import com.mrbysco.horsingaround.handler.TameHandler;
import com.mrbysco.horsingaround.network.PacketHandler;
import com.mrbysco.horsingaround.registry.HorsingRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(HorsingAround.MOD_ID)
public class HorsingAround {
	public static final String MOD_ID = "horsingaround";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static final TagKey<Item> LINKING = ItemTags.create(new ResourceLocation(MOD_ID, "linking"));

	public HorsingAround(IEventBus eventBus, Dist dist, ModContainer container) {
		container.registerConfig(ModConfig.Type.COMMON, HorsingConfig.commonSpec);
		eventBus.register(HorsingConfig.class);

		HorsingRegistry.SOUND_EVENTS.register(eventBus);

		eventBus.addListener(PacketHandler::setupPackets);

		NeoForge.EVENT_BUS.register(new SyncHandler());
		NeoForge.EVENT_BUS.register(new TameHandler());

		if (dist.isClient()) {
			NeoForge.EVENT_BUS.addListener(ClientHandler::onRenderOverlayPre);
			NeoForge.EVENT_BUS.addListener(ClientHandler::onRenderOverlayPost);
			eventBus.addListener(KeybindHandler::registerKeymapping);
			NeoForge.EVENT_BUS.addListener(KeybindHandler::keyEvent);
		}
	}
}
