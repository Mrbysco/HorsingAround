package com.mrbysco.horsingaround.client;

import com.mrbysco.horsingaround.client.gui.radial_menu.GuiRadialMenu;
import com.mrbysco.horsingaround.data.CallData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.RenderGuiOverlayEvent;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;

import java.util.ArrayList;
import java.util.List;

public class ClientHandler {
	public static final List<CallData.TamedData> tamedList = new ArrayList<>();

	public static void onRenderOverlayPre(RenderGuiOverlayEvent.Pre event) {
		if (Minecraft.getInstance().screen instanceof GuiRadialMenu<?> && event.getOverlay() == VanillaGuiOverlay.CROSSHAIR.type()) {
			event.setCanceled(true);
		}
	}

	public static void onRenderOverlayPost(RenderGuiOverlayEvent.Post event) {
		if (!event.getOverlay().id().equals(new ResourceLocation("mount_health"))) return;
		Minecraft mc = Minecraft.getInstance();
		if (!(mc.gui instanceof ExtendedGui gui)) return;
		Player player = mc.player;
		if (player == null) return;
		GuiGraphics guiGraphics = event.getGuiGraphics();

		Entity vehicle = player.getVehicle();
		boolean isMounted = vehicle != null && vehicle.showVehicleHealth();
		if (isMounted && !mc.options.hideGui && gui.shouldDrawSurvivalElements()) {
			int height = mc.getWindow().getGuiScaledHeight();
			int width = mc.getWindow().getGuiScaledWidth();

			gui.setupOverlayRenderState(true, false);
			gui.renderFood(width, height, guiGraphics);
		}
	}
}
