package com.mrbysco.horsingaround.client;

import com.mrbysco.horsingaround.client.gui.radial_menu.GuiRadialMenu;
import com.mrbysco.horsingaround.data.CallData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;

import java.util.ArrayList;
import java.util.List;

public class ClientHandler {
	public static final List<CallData.TamedData> tamedList = new ArrayList<>();

	public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
		if (!event.getOverlay().id().equals(new ResourceLocation("mount_health"))) return;
		Minecraft mc = Minecraft.getInstance();
		if (!(mc.gui instanceof ForgeGui gui)) return;
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

	public static void overlayEvent(RenderGuiOverlayEvent.Pre event) {
		if (Minecraft.getInstance().screen instanceof GuiRadialMenu<?> && event.getOverlay() == VanillaGuiOverlay.CROSSHAIR.type()) {
			event.setCanceled(true);
		}
	}
}
