package com.mrbysco.horsingaround.client;

import com.mrbysco.horsingaround.HorsingAround;
import com.mrbysco.horsingaround.client.gui.RenderHelper;
import com.mrbysco.horsingaround.client.gui.radial_menu.ClientData;
import com.mrbysco.horsingaround.client.gui.radial_menu.GuiRadialMenu;
import com.mrbysco.horsingaround.client.gui.radial_menu.RadialMenu;
import com.mrbysco.horsingaround.client.gui.radial_menu.RadialMenuSlot;
import com.mrbysco.horsingaround.data.CallData;
import com.mrbysco.horsingaround.network.PacketHandler;
import com.mrbysco.horsingaround.network.message.SummonMessage;
import com.mrbysco.horsingaround.network.message.UnlinkMessage;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class KeybindHandler {
	public static KeyMapping KEY_OPEN_MENU = new KeyMapping(getKey("open_menu"), GLFW.GLFW_KEY_X, getKey("category"));

	private static String getKey(String name) {
		return String.join(".", "key", HorsingAround.MOD_ID, name);
	}


	public static void registerKeymapping(final RegisterKeyMappingsEvent event) {
		event.register(KEY_OPEN_MENU);
	}

	public static void keyEvent(final InputEvent.Key event) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || event.getAction() != 1)
			return;
		if (mc.screen == null || mc.screen instanceof GuiRadialMenu<?>) {
			if (event.getKey() == KEY_OPEN_MENU.getKey().getValue() && !KEY_OPEN_MENU.isUnbound()) {
				List<RadialMenuSlot<ClientData>> slots = new ArrayList<>();
				List<ClientData> stackList = new ArrayList<>();
				for (CallData.TamedData data : ClientHandler.tamedList) {
					Entity entity = data.createEntity(mc.level);
					if (entity instanceof LivingEntity livingEntity) {
						ClientData clientData = new ClientData(data, livingEntity);
						slots.add(new RadialMenuSlot<>(clientData.data().name(), clientData, new ArrayList<>()));
						stackList.add(clientData);
					}
				}
				if (slots.isEmpty()) {
					mc.player.sendSystemMessage(Component.translatable("message.horsingaround.no_tamed_entities"));
					return;
				}
				Minecraft.getInstance().setScreen(new GuiRadialMenu<>(new RadialMenu<>((id) -> {
					ClientData data = stackList.get(id);
					if (Screen.hasShiftDown()) {
						//Remove entity from list
						PacketHandler.CHANNEL.sendToServer(new UnlinkMessage(data.data().uuid()));
					} else {
						PacketHandler.CHANNEL.sendToServer(new SummonMessage(data.data().uuid()));
					}
				}, slots, RenderHelper::drawTamedEntities, 0)));
			}
		}
	}
}
