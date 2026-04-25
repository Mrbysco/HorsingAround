package com.mrbysco.horsingaround.client.gui.radial_menu;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec2;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;

import java.util.List;

@EventBusSubscriber(Dist.CLIENT)
public class GuiRadialMenu<T> extends Screen {
	private static final float PRECISION_SMOOTH = 5.0f;
	private static final float PRECISION_PIXELATED = 45.0F;
	private static final int MAX_SLOTS = 20;

	private boolean closing;
	private RadialMenu<T> radialMenu;
	private List<RadialMenuSlot<T>> radialMenuSlots;
	final float OPEN_ANIMATION_LENGTH = 0.40f;
	private float totalTime;
	private float prevTick;
	private float extraTick;
	/**
	 * Zero-Based index
	 */
	private int selectedItem;
	private final boolean pixelatedMode;
	private final int hoverColor;

	public GuiRadialMenu(RadialMenu<T> radialMenu, boolean pixelatedMode, int hoverColor) {
		super(Component.literal(""));
		this.radialMenu = radialMenu;
		this.radialMenuSlots = this.radialMenu.getRadialMenuSlots();
		this.closing = false;
		this.selectedItem = -1;
		this.pixelatedMode = pixelatedMode;
		this.hoverColor = hoverColor;
	}

	public GuiRadialMenu(RadialMenu<T> radialMenu) {
		this(radialMenu, false, 0x3FA1BF);
	}

	@SubscribeEvent
	public static void updateInputEvent(MovementInputUpdateEvent event) {
		if (Minecraft.getInstance().screen instanceof GuiRadialMenu) { //TODO: REDO THIS!!!!
			Options settings = Minecraft.getInstance().options;

			Window window = Minecraft.getInstance().getWindow();
			boolean up = InputConstants.isKeyDown(window, settings.keyUp.getKey().getValue());
			boolean down = InputConstants.isKeyDown(window, settings.keyDown.getKey().getValue());
			boolean left = InputConstants.isKeyDown(window, settings.keyLeft.getKey().getValue());
			boolean right = InputConstants.isKeyDown(window, settings.keyRight.getKey().getValue());
			boolean jumping = InputConstants.isKeyDown(window, settings.keyJump.getKey().getValue());
			boolean shift = InputConstants.isKeyDown(window, settings.keyShift.getKey().getValue());
			boolean sprint = InputConstants.isKeyDown(window, settings.keySprint.getKey().getValue());

			ClientInput clientInput = event.getInput();
			clientInput.keyPresses = new Input(
					up, down, left, right,
					jumping, shift, sprint
			);
			Input keyPresses = clientInput.keyPresses;

			float f = KeyboardInput.calculateImpulse(keyPresses.forward(), keyPresses.backward());
			float f1 = KeyboardInput.calculateImpulse(keyPresses.left(), keyPresses.right());
			clientInput.moveVector = new Vec2(f1, f).normalized();
		}
	}

	@Override
	public void tick() {
		if (totalTime != OPEN_ANIMATION_LENGTH) {
			extraTick++;
		}
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractRenderState(graphics, mouseX, mouseY, a);

		Matrix3x2fStack poseStack = graphics.pose();
		float openAnimation = closing ? 1.0f - totalTime / OPEN_ANIMATION_LENGTH : totalTime / OPEN_ANIMATION_LENGTH;
		assert minecraft != null;
		float currTick = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
		totalTime += (currTick + extraTick - prevTick) / 20f;
		extraTick = 0;
		prevTick = currTick;


		float animProgress = Mth.clamp(openAnimation, 0, 1);
		animProgress = (float) (1 - Math.pow(1 - animProgress, 3));
		float radiusIn = Math.max(0.1f, 45 * animProgress);
		float radiusOut = radiusIn * 2;
		float itemRadius = (radiusIn + radiusOut) * 0.5f;

		int centerOfScreenX = width / 2;
		int centerOfScreenY = height / 2;
		int numberOfSlices = Math.min(MAX_SLOTS, radialMenuSlots.size());

		double mousePositionInDegreesInRelationToCenterOfScreen = Math.toDegrees(Math.atan2(mouseY - centerOfScreenY, mouseX - centerOfScreenX));
		double mouseDistanceToCenterOfScreen = Math.sqrt(Math.pow(mouseX - centerOfScreenX, 2) + Math.pow(mouseY - centerOfScreenY, 2));
		float slot0 = (((0 - 0.5f) / (float) numberOfSlices) + 0.25f) * 360;
		if (mousePositionInDegreesInRelationToCenterOfScreen < slot0) {
			mousePositionInDegreesInRelationToCenterOfScreen += 360;
		}

		poseStack.pushMatrix();
		boolean hasMouseOver = false;
		int mousedOverSlot = -1;

		if (!closing) {
			selectedItem = -1;
			for (int i = 0; i < numberOfSlices; i++) {
				float sliceBorderLeft = (((i - 0.5f) / (float) numberOfSlices) + 0.25f) * 360;
				float sliceBorderRight = (((i + 0.5f) / (float) numberOfSlices) + 0.25f) * 360;
				if (mousePositionInDegreesInRelationToCenterOfScreen >= sliceBorderLeft && mousePositionInDegreesInRelationToCenterOfScreen < sliceBorderRight && mouseDistanceToCenterOfScreen >= radiusIn && mouseDistanceToCenterOfScreen < radiusOut) {
					selectedItem = i;
					break;
				}
			}
		}


		for (int i = 0; i < numberOfSlices; i++) {
			float sliceBorderLeft = (((i - 0.5f) / (float) numberOfSlices) + 0.25f) * 360;
			float sliceBorderRight = (((i + 0.5f) / (float) numberOfSlices) + 0.25f) * 360;
			if (selectedItem == i) {
				int r = (hoverColor >> 16) & 0xFF;
				int g = (hoverColor >> 8) & 0xFF;
				int b = hoverColor & 0xFF;
				drawSlice(graphics, centerOfScreenX, centerOfScreenY, 10, radiusIn, radiusOut, sliceBorderLeft, sliceBorderRight, r, g, b, 60);
				hasMouseOver = true;
				mousedOverSlot = selectedItem;
			} else
				drawSlice(graphics, centerOfScreenX, centerOfScreenY, 10, radiusIn, radiusOut, sliceBorderLeft, sliceBorderRight, 0, 0, 0, 64);
		}

		if (hasMouseOver && mousedOverSlot != -1) {
			int adjusted = ((mousedOverSlot + (numberOfSlices / 2 + 1)) % numberOfSlices) - 1;
			adjusted = adjusted == -1 ? numberOfSlices - 1 : adjusted;
			graphics.centeredText(font, radialMenuSlots.get(adjusted).slotName(), width / 2, (height - font.lineHeight) / 2, 16777215);
		}

		poseStack.popMatrix();
		for (int i = 0; i < numberOfSlices; i++) {
			ItemStack stack = new ItemStack(Blocks.DIRT);
			float angle1 = ((i / (float) numberOfSlices) - 0.25f) * 2 * (float) Math.PI;
			if (numberOfSlices % 2 != 0) {
				angle1 += (float) (Math.PI / numberOfSlices);
			}
			float posX = centerOfScreenX - 8 + itemRadius * (float) Math.cos(angle1);
			float posY = centerOfScreenY - 8 + itemRadius * (float) Math.sin(angle1);

			T primarySlotIcon = radialMenuSlots.get(i).primarySlotIcon();
			List<T> secondarySlotIcons = radialMenuSlots.get(i).secondarySlotIcons();
			if (primarySlotIcon != null) {
				radialMenu.drawIcon(primarySlotIcon, graphics, (int) posX, (int) posY, 16);
				if (secondarySlotIcons != null && !secondarySlotIcons.isEmpty()) {
					drawSecondaryIcons(graphics, (int) posX, (int) posY, secondarySlotIcons);
				}
			}
			poseStack.pushMatrix();
			poseStack.translate(0, 0);
			drawSliceName(graphics, String.valueOf(i + 1), stack, (int) posX, (int) posY);
			poseStack.popMatrix();
		}

		if (mousedOverSlot != -1) {
			int adjusted = ((mousedOverSlot + (numberOfSlices / 2 + 1)) % numberOfSlices) - 1;
			adjusted = adjusted == -1 ? numberOfSlices - 1 : adjusted;
			selectedItem = adjusted;
		}
	}

	public void drawSecondaryIcons(GuiGraphicsExtractor graphics, int positionXOfPrimaryIcon, int positionYOfPrimaryIcon, List<T> secondarySlotIcons) {
		if (!radialMenu.isShowMoreSecondaryItems()) {
			drawSecondaryIcon(graphics, secondarySlotIcons.get(0), positionXOfPrimaryIcon, positionYOfPrimaryIcon, radialMenu.getSecondaryIconStartingPosition());
		} else {
			SecondaryIconPosition currentSecondaryIconPosition = radialMenu.getSecondaryIconStartingPosition();
			for (T secondarySlotIcon : secondarySlotIcons) {
				drawSecondaryIcon(graphics, secondarySlotIcon, positionXOfPrimaryIcon, positionYOfPrimaryIcon, currentSecondaryIconPosition);
				currentSecondaryIconPosition = SecondaryIconPosition.getNextPosition(currentSecondaryIconPosition);
			}
		}
	}

	public void drawSecondaryIcon(GuiGraphicsExtractor graphics, T item, int positionXOfPrimaryIcon, int positionYOfPrimaryIcon, SecondaryIconPosition secondaryIconPosition) {
		int offset = radialMenu.getOffset();
		switch (secondaryIconPosition) {
			case NORTH ->
					radialMenu.drawIcon(item, graphics, positionXOfPrimaryIcon + offset, positionYOfPrimaryIcon - 14 + offset, 10);
			case EAST ->
					radialMenu.drawIcon(item, graphics, positionXOfPrimaryIcon + 14 + offset, positionYOfPrimaryIcon + offset, 10);
			case SOUTH ->
					radialMenu.drawIcon(item, graphics, positionXOfPrimaryIcon + offset, positionYOfPrimaryIcon + 14 + offset, 10);
			case WEST ->
					radialMenu.drawIcon(item, graphics, positionXOfPrimaryIcon - 14 + offset, positionYOfPrimaryIcon + offset, 10);
		}
	}

	public void drawSliceName(GuiGraphicsExtractor graphics, String sliceName, ItemStack stack, int posX, int posY) {
		if (!radialMenu.isShowMoreSecondaryItems()) {
			graphics.itemDecorations(font, stack, posX + 5, posY, sliceName);
		} else {
			graphics.itemDecorations(font, stack, posX + 5, posY + 5, sliceName);
		}
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		int adjustedKey = event.key() - 48;
		if (adjustedKey >= 0 && adjustedKey < radialMenuSlots.size()) {
			selectedItem = adjustedKey == 0 ? radialMenuSlots.size() : adjustedKey;
			selectedItem = selectedItem - 1; // Offset by 1 because 0 based indexing but users see 1 indexed
			mouseClicked(new MouseButtonEvent(0, 0, new MouseButtonInfo(0, 0)), false);
			return true;
		}
		return super.keyPressed(event);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent buttonEvent, boolean doubleClicked) {
		if (this.selectedItem != -1) {
			radialMenu.setCurrentSlot(selectedItem);
			minecraft.player.closeContainer();
		}
		return super.mouseClicked(buttonEvent, doubleClicked);
	}

	public void drawSlice(GuiGraphicsExtractor graphics, float x, float y, float z,
	                      float radiusIn, float radiusOut, float startAngle, float endAngle,
	                      int r, int g, int b, int a) {
		float angle = endAngle - startAngle;
		float precision = pixelatedMode ? PRECISION_PIXELATED : PRECISION_SMOOTH;
		int sections = Math.max(1, Mth.ceil(angle / precision));

		startAngle = (float) Math.toRadians(startAngle);
		endAngle = (float) Math.toRadians(endAngle);

		PieSliceRenderState sliceState = new PieSliceRenderState(
				RenderPipelines.GUI,
				new Matrix3x2f(graphics.pose()),
				x, y,
				radiusIn, radiusOut,
				startAngle, endAngle,
				ARGB.color(a, r, g, b),
				sections,
				graphics.peekScissorStack()
		);

		graphics.submitGuiElementRenderState(sliceState);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}

/*
Note: This code has been modified from David Quintana's solution.
Below is the required copyright notice.
Copyright (c) 2015, David Quintana <gigaherz@gmail.com>
All rights reserved.
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the author nor the
      names of the contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/