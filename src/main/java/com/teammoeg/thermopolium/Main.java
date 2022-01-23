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

package com.teammoeg.thermopolium;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.teammoeg.thermopolium.Contents.SCBlocks;
import com.teammoeg.thermopolium.network.PacketHandler;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Main.MODID)
public class Main {

    public static final String MODID = "thermopolium";
    public static final String MODNAME = "Thermopolium";
    public static final Logger logger = LogManager.getLogger(MODNAME);
    public static final ItemGroup itemGroup = new ItemGroup(MODID) {
        @Override
        @Nonnull
        public ItemStack createIcon() {
            return new ItemStack(SCBlocks.stew_pot);
        }
    };

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MODID, path);
    }

    public Main() {
        IEventBus mod = FMLJavaModLoadingContext.get().getModEventBus();

        mod.addListener(this::setup);
        mod.addListener(this::processIMC);
        mod.addListener(this::enqueueIMC);
        Config.register();
        PacketHandler.register();

        Contents.SCItems.init();
        Contents.SCBlocks.init();
        Fluids.init();
        Contents.SCTileTypes.REGISTER.register(mod);
        Contents.SCGui.CONTAINERS.register(mod);
        Fluids.FLUIDS.register(mod);
        Contents.SCRecipes.RECIPE_SERIALIZERS.register(mod);
        DeferredWorkQueue.runLater(Contents.SCRecipes::registerRecipeTypes);
    }

    public void setup(final FMLCommonSetupEvent event) {
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
    }
    private void processIMC(final InterModProcessEvent event) {

    }
}
