package com.mrbysco.horsingaround.client.gui.radial_menu;

import com.mrbysco.horsingaround.data.CallData;
import net.minecraft.world.entity.LivingEntity;

public record ClientData(CallData.TamedData data, LivingEntity livingEntity) {
}
