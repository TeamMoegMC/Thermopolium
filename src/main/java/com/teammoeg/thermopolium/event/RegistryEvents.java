/*
 * Copyright (c) 2021 TeamMoeg
 *
 * This file is part of Frosted Heart.
 *
 * Frosted Heart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Frosted Heart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Frosted Heart. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.thermopolium.event;

import static com.teammoeg.thermopolium.Contents.*;

import com.teammoeg.thermopolium.Main;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryEvents {
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        for (Block block : registeredBlocks) {
            try {
                event.getRegistry().register(block);
            } catch (Throwable e) {
                Main.logger.error("Failed to register a block. ({})", block);
                throw e;
            }
        }
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        for (Item item : registeredItems) {
            try {
                event.getRegistry().register(item);
            } catch (Throwable e) {
            	Main.logger.error("Failed to register an item. ({}, {})", item, item.getRegistryName());
                throw e;
            }
        }
    }

    @SubscribeEvent
    public static void registerFluids(RegistryEvent.Register<Fluid> event) {
        for (Fluid fluid : registeredFluids) {
            try {
                event.getRegistry().register(fluid);
            } catch (Throwable e) {
            	Main.logger.error("Failed to register a fluid. ({}, {})", fluid, fluid.getRegistryName());
                throw e;
            }
        }
    }

    @SubscribeEvent
    public static void registerEffects(final RegistryEvent.Register<Effect> event) {
      
    }

}
