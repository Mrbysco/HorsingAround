package com.mrbysco.horsingaround.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrbysco.horsingaround.client.ClientHandler;
import net.minecraft.client.color.ColorLerper;
import net.minecraft.client.model.animal.equine.HorseModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HorseRenderState;
import net.minecraft.resources.Identifier;

public class SpecialHorseLayer extends RenderLayer<HorseRenderState, HorseModel> {

	private final Identifier HORSE_TEXTURE = Identifier.withDefaultNamespace("textures/entity/horse/horse_white.png");
	private final Identifier HORSE_BABY_TEXTURE = Identifier.withDefaultNamespace("textures/entity/horse/horse_white_baby.png");

	public SpecialHorseLayer(RenderLayerParent<HorseRenderState, HorseModel> renderer) {
		super(renderer);
	}

	@Override
	public void submit(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, HorseRenderState state, float yRot, float xRot) {
		if (!state.isInvisible && state.getRenderDataOrDefault(ClientHandler.GEGY_HORSE, false)) {
			HorseModel model = getParentModel();
			Identifier texture = state.isBaby ? HORSE_BABY_TEXTURE : HORSE_TEXTURE;
			int color = ColorLerper.getLerpedColor(ColorLerper.Type.SHEEP, state.ageInTicks);

			coloredCutoutModelCopyLayerRender(
					model, texture, poseStack, collector, 15728880, state, color, 1
			);
		}
	}
}
