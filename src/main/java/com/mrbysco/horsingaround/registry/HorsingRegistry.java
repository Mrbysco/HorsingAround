package com.mrbysco.horsingaround.registry;

import com.mrbysco.horsingaround.HorsingAround;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class HorsingRegistry {
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, HorsingAround.MOD_ID);

	public static final RegistryObject<SoundEvent> CALL = SOUND_EVENTS.register("call", () ->
			SoundEvent.createVariableRangeEvent(new ResourceLocation(HorsingAround.MOD_ID, "call")));

}
