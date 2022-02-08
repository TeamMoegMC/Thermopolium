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

package com.teammoeg.thermopolium.datagen;

import com.teammoeg.thermopolium.Main;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class THDataGenerator {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		ExistingFileHelper exHelper = event.getExistingFileHelper();

		if (event.includeServer()) {
			gen.addProvider(new THPItemModelProvider(gen, Main.MODID, exHelper));
			gen.addProvider(new THPRecipeProvider(gen));
			gen.addProvider(new THPItemTagGenerator(gen, Main.MODID, exHelper));
			gen.addProvider(new THPFluidTagGenerator(gen, Main.MODID, exHelper));
			gen.addProvider(new THPLootGenerator(gen));
			gen.addProvider(new THPStatesProvider(gen, Main.MODID, exHelper));
			gen.addProvider(new THPBookGenerator(gen,exHelper));
			gen.addProvider(new THPFHHeatGenerator(gen,exHelper));
		}
	}
}
