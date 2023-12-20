package com.mrbysco.horsingaround;

import com.mojang.logging.LogUtils;
import com.mrbysco.horsingaround.client.ClientHandler;
import com.mrbysco.horsingaround.client.KeybindHandler;
import com.mrbysco.horsingaround.handler.SyncHandler;
import com.mrbysco.horsingaround.handler.TameHandler;
import com.mrbysco.horsingaround.network.PacketHandler;
import com.mrbysco.horsingaround.registry.HorsingRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(HorsingAround.MOD_ID)
public class HorsingAround {
	public static final String MOD_ID = "horsingaround";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static final TagKey<Item> LINKING = ItemTags.create(new ResourceLocation(MOD_ID, "linking"));

	public HorsingAround() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

		HorsingRegistry.SOUND_EVENTS.register(eventBus);

		eventBus.addListener(this::commonSetup);

		MinecraftForge.EVENT_BUS.register(new SyncHandler());
		MinecraftForge.EVENT_BUS.register(new TameHandler());

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			MinecraftForge.EVENT_BUS.addListener(ClientHandler::onRenderOverlayPre);
			MinecraftForge.EVENT_BUS.addListener(ClientHandler::onRenderOverlayPost);
			eventBus.addListener(KeybindHandler::registerKeymapping);
			MinecraftForge.EVENT_BUS.addListener(KeybindHandler::keyEvent);
		});
	}

	public void commonSetup(FMLCommonSetupEvent event) {
		PacketHandler.init();
	}
}
