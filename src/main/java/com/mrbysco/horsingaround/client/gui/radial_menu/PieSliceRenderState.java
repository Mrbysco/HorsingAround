package com.mrbysco.horsingaround.client.gui.radial_menu;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.util.ARGB;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.Nullable;

public class PieSliceRenderState implements GuiElementRenderState {
	private final RenderPipeline pipeline;
	private final TextureSetup textureSetup;
	private final Matrix3x2f pose;
	private final @Nullable ScreenRectangle scissorArea;
	private final @Nullable ScreenRectangle bounds;

	private final float centerX;
	private final float centerY;
	private final float radiusIn;
	private final float radiusOut;
	private final float startAngle;
	private final float endAngle;
	private final int color;
	private final int sections;

	public PieSliceRenderState(RenderPipeline pipeline, Matrix3x2f pose,
	                           float centerX, float centerY,
	                           float radiusIn, float radiusOut,
	                           float startAngle, float endAngle,
	                           int color, int sections,
	                           @Nullable ScreenRectangle scissorArea) {
		this.pipeline = pipeline;
		this.textureSetup = TextureSetup.noTexture();
		this.pose = new Matrix3x2f(pose);
		this.scissorArea = scissorArea;

		this.centerX = centerX;
		this.centerY = centerY;
		this.radiusIn = radiusIn;
		this.radiusOut = radiusOut;
		this.startAngle = startAngle;
		this.endAngle = endAngle;
		this.color = color;
		this.sections = sections;

		// Calculate bounds for the ScreenArea interface
		int left = (int) (centerX - radiusOut);
		int top = (int) (centerY - radiusOut);
		int width = (int) (radiusOut * 2);
		int height = (int) (radiusOut * 2);
		this.bounds = new ScreenRectangle(left, top, width, height);
	}

	@Override
	public void buildVertices(VertexConsumer consumer) {
		float angle = endAngle - startAngle;

		int alpha = ARGB.alpha(color);
		int red = ARGB.red(color);
		int green = ARGB.green(color);
		int blue = ARGB.blue(color);

		for (int i = 0; i < sections; i++) {
			float angle1 = startAngle + (i / (float) sections) * angle;
			float angle2 = startAngle + ((i + 1) / (float) sections) * angle;

			float x1In = centerX + radiusIn * (float) Math.cos(angle1);
			float y1In = centerY + radiusIn * (float) Math.sin(angle1);
			float x1Out = centerX + radiusOut * (float) Math.cos(angle1);
			float y1Out = centerY + radiusOut * (float) Math.sin(angle1);
			float x2Out = centerX + radiusOut * (float) Math.cos(angle2);
			float y2Out = centerY + radiusOut * (float) Math.sin(angle2);
			float x2In = centerX + radiusIn * (float) Math.cos(angle2);
			float y2In = centerY + radiusIn * (float) Math.sin(angle2);

			consumer.addVertex(x1Out, y1Out, 0).setColor(red, green, blue, alpha);
			consumer.addVertex(x1In, y1In, 0).setColor(red, green, blue, alpha);
			consumer.addVertex(x2In, y2In, 0).setColor(red, green, blue, alpha);
			consumer.addVertex(x2Out, y2Out, 0).setColor(red, green, blue, alpha);
		}
	}

	@Override
	public RenderPipeline pipeline() {
		return pipeline;
	}

	@Override
	public TextureSetup textureSetup() {
		return textureSetup;
	}

	@Override
	public @Nullable ScreenRectangle scissorArea() {
		return scissorArea;
	}

	@Override
	public @Nullable ScreenRectangle bounds() {
		return bounds;
	}
}