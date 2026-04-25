package com.mrbysco.horsingaround.client.gui.radial_menu;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface DrawCallback<T> {
	void accept(T objectToBeDrawn, GuiGraphicsExtractor guiGraphics, int positionX, int positionY, int size, boolean renderTransparent);
}
