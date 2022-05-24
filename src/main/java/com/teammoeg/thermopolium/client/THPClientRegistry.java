/*
 * Copyright (c) 2022 TeamMoeg
 *
 * This file is part of Thermopolium.
 *
 * Thermopolium is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Thermopolium is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Thermopolium. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.thermopolium.client;

import com.teammoeg.thermopolium.Contents;
import com.teammoeg.thermopolium.Main;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class THPClientRegistry {
	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void onClientSetupEvent(FMLClientSetupEvent event) {
		ScreenManager.register(Contents.THPGui.STEWPOT.get(), StewPotScreen::new);
		ScreenManager.register(Contents.THPGui.STOVE.get(),KitchenStoveScreen::new);
		RenderTypeLookup.setRenderLayer(Contents.THPBlocks.stew_pot, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(Contents.THPBlocks.stove1, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(Contents.THPBlocks.stove2, RenderType.cutout());
		ClientRegistry.bindTileEntityRenderer(Contents.THPTileTypes.STEW_POT.get(), StewPotRenderer::new);
	}

	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
		Minecraft.getInstance().particleEngine.register(Particles.STEAM.get(), SteamParticle.Factory::new);
	}
}