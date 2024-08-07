package com.mrbysco.horsingaround.registry;

import com.mrbysco.horsingaround.HorsingAround;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HorsingRegistry {
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, HorsingAround.MOD_ID);

	public static final DeferredHolder<SoundEvent, SoundEvent> CALL = SOUND_EVENTS.register("call", () ->
			SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(HorsingAround.MOD_ID, "call")));

}
