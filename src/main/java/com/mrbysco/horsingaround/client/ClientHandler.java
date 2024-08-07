package com.mrbysco.horsingaround.client;

import com.mrbysco.horsingaround.client.gui.radial_menu.GuiRadialMenu;
import com.mrbysco.horsingaround.data.CallData;
import com.mrbysco.horsingaround.mixin.GuiAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import java.util.ArrayList;
import java.util.List;

public class ClientHandler {
	public static final List<CallData.TamedData> tamedList = new ArrayList<>();

	public static void onRenderOverlayPre(RenderGuiLayerEvent.Pre event) {
		if (Minecraft.getInstance().screen instanceof GuiRadialMenu<?> && event.getName().equals(VanillaGuiLayers.CROSSHAIR)) {
			event.setCanceled(true);
		}
	}

	public static void onRenderOverlayPost(RenderGuiLayerEvent.Post event) {
		if (!event.getName().equals(VanillaGuiLayers.VEHICLE_HEALTH)) return;
		Minecraft mc = Minecraft.getInstance();
		Gui gui = mc.gui;
		if (gui == null) return;
		Player player = mc.player;
		if (player == null) return;
		GuiGraphics guiGraphics = event.getGuiGraphics();
		Entity vehicle = player.getVehicle();
		boolean isMounted = vehicle != null && vehicle.showVehicleHealth();
		if (isMounted && !mc.options.hideGui) {
			int i1 = guiGraphics.guiWidth() / 2 + 91;
			int j1 = guiGraphics.guiHeight() - gui.rightHeight;
			((GuiAccessor) gui).invokeRenderFoodLevel(guiGraphics, player, j1, i1);
		}
	}
}
