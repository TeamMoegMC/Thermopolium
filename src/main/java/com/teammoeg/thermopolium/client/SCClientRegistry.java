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
public class SCClientRegistry {
	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void onClientSetupEvent(FMLClientSetupEvent event) {
		ScreenManager.registerFactory(Contents.SCGui.STEWPOT.get(), StewPotScreen::new);
		RenderTypeLookup.setRenderLayer(Contents.SCBlocks.stew_pot, RenderType.getCutoutMipped());
		ClientRegistry.bindTileEntityRenderer(Contents.SCTileTypes.STEW_POT.get(), StewPotRenderer::new);
	}
	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particles.registerFactory(Particles.STEAM.get(), SteamParticle.Factory::new);
    }
}