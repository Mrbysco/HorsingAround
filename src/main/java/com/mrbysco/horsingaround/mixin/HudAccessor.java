package com.mrbysco.horsingaround.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Hud.class)
public interface HudAccessor {
	@Invoker("extractFood")
	void invokeExtractFood(GuiGraphicsExtractor pGuiGraphics, Player pPlayer, int pY, int pX);
}
