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

import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Fluids {
    public static final ResourceLocation STILL_FLUID_TEXTURE = new ResourceLocation("block/water_still");
    public static final ResourceLocation FLOWING_FLUID_TEXTURE =new ResourceLocation("block/water_flow");
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, Main.MODID);


    public static RegistryObject<FlowingFluid> HOT_WATER = FLUIDS.register("hot_water", () -> {
        return new ForgeFlowingFluid.Source(Fluids.HOT_WATER_PROPERTIES);
    });
    public static RegistryObject<FlowingFluid> STEW = FLUIDS.register("stew", () -> {
        return new ForgeFlowingFluid.Source(Fluids.STW_PROPERTIES);
    });

    public static ForgeFlowingFluid.Properties HOT_WATER_PROPERTIES =
            new ForgeFlowingFluid.Properties(HOT_WATER, HOT_WATER, FluidAttributes.builder(STILL_FLUID_TEXTURE, FLOWING_FLUID_TEXTURE)
                    .color(0xFF3ABDFF).temperature(333)).block(null)
                    .slopeFindDistance(3).explosionResistance(100F);
    public static ForgeFlowingFluid.Properties STW_PROPERTIES =
            new ForgeFlowingFluid.Properties(STEW, STEW, FluidAttributes.builder(STILL_FLUID_TEXTURE, FLOWING_FLUID_TEXTURE)
                    .color(0xFFFFFFFF).viscosity(-1))
                    .slopeFindDistance(1).explosionResistance(100F);
}
