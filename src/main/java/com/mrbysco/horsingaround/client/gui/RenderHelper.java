package com.mrbysco.horsingaround.client.gui;

import com.mrbysco.horsingaround.client.ClientHandler;
import com.mrbysco.horsingaround.client.gui.radial_menu.ClientData;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;

public class RenderHelper {
	public static void extractTamedEntities(ClientData data, GuiGraphicsExtractor guiGraphics,
	                                        int positionX, int positionY, int size,
	                                        boolean renderTransparent) {
		LivingEntity tamedEntity = data.livingEntity();
		if (tamedEntity.level() == null) return;

		int halfWidth = 32;
		int halfHeight = 64;
		int startX = positionX - halfWidth;
		int startY = positionY - halfHeight;
		int endX = positionX + halfWidth;
		int endY = positionY + halfHeight;

		float xAngle = (float) Math.toRadians(120);
		float yAngle = (float) Math.toRadians(-15);

		size += 4;
		tamedEntity.tickCount += 1;

		if (ClientHandler.checkMagicName(tamedEntity, "Gegy")) {
			long time = System.currentTimeMillis();
			xAngle = (float) Math.toRadians(120 + ((time / 50L) * 32) % 11520);
		}

		InventoryScreen.renderEntityInInventoryFollowsAngle(
				guiGraphics,
				startX, startY, endX, endY,
				size,
				0.25F,
				xAngle,
				yAngle,
				tamedEntity
		);
	}
}
