package com.mrbysco.horsingaround.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrbysco.horsingaround.client.gui.radial_menu.ClientData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;

public class RenderHelper {
	public static void drawTamedEntities(ClientData data, GuiGraphics guiGraphics, int positionX, int positionY, int size, boolean renderTransparent) {
		LivingEntity tamedEntity = data.livingEntity();
		if (tamedEntity.level() == null) return;

		PoseStack poseStack = guiGraphics.pose();
		poseStack.pushPose();
		poseStack.translate(positionX, positionY, 100F);
		poseStack.translate(8.0D, 24.0D, 0.0D);
		poseStack.scale(size, size, size);
		poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
		// Rotate entity
		poseStack.mulPose(Axis.XP.rotationDegrees(((float) Math.atan((-40 / 40.0F))) * 10.0F));
		tamedEntity.yHeadRot = 45;
		tamedEntity.yHeadRotO = 45;
		tamedEntity.yBodyRot = 45;

		poseStack.translate(0.0F, tamedEntity.getMyRidingOffset(tamedEntity), 0.0F);
		EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
		entityRenderDispatcher.overrideCameraOrientation(new Quaternionf(0.0F, 0.0F, 0.0F, 1.0F));
		entityRenderDispatcher.setRenderShadow(false);
		final MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
		RenderSystem.runAsFancy(() -> {
			entityRenderDispatcher.render(tamedEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, poseStack, bufferSource, 15728880);
		});
		bufferSource.endBatch();
		entityRenderDispatcher.setRenderShadow(true);
		poseStack.popPose();
	}
}
