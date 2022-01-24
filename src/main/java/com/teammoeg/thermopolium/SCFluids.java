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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.teammoeg.thermopolium.fluid.SoupFluid;
import com.teammoeg.thermopolium.fluid.SoupFluid.SoupAttributes;

import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SCFluids {
    public static final ResourceLocation STILL_FLUID_TEXTURE = new ResourceLocation("block/water_still");
    public static final ResourceLocation FLOWING_FLUID_TEXTURE =new ResourceLocation("block/water_flow");
    public static final ResourceLocation STILL_SOUP_TEXTURE = new ResourceLocation(Main.MODID,"fluid/soup_fluid");
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, Main.MODID);
    public static final Map<String,Integer> soupfluids=new HashMap<>();
    public static void init() {
    	soupfluids.put("acquacotta",0xffdcb259);
    	soupfluids.put("bisque",0xffb87246);
    	soupfluids.put("bone_gelatin",0xffe3a14a);
    	soupfluids.put("borscht",0xff802629);
    	soupfluids.put("borscht_cream",0xffcf938e);
    	soupfluids.put("congee",0xffd6cbb3);
    	soupfluids.put("cream_of_meat_soup",0xffb98c60);
    	soupfluids.put("cream_of_mushroom_soup",0xffa7815f);
    	soupfluids.put("custard",0xffecda6e);
    	soupfluids.put("dilute_soup",0xffc2b598);
    	soupfluids.put("egg_drop_soup",0xffd9b773);
    	soupfluids.put("egg_tongsui",0xffc9b885);
    	soupfluids.put("fish_chowder",0xffd7c68e);
    	soupfluids.put("fish_soup",0xffa18441);
    	soupfluids.put("fricassee",0xffd2a85f);
    	soupfluids.put("goji_tongsui",0xffa97744);
    	soupfluids.put("goulash",0xff9e4a2a);
    	soupfluids.put("gruel",0xffd3ba9a);
    	soupfluids.put("hodgepodge",0xffb59d64);
    	soupfluids.put("meat_soup",0xff895e2d);
    	soupfluids.put("mushroom_soup",0xff97664c);
    	soupfluids.put("nail_soup",0xff375c8c);
    	soupfluids.put("nettle_soup",0xff467b32);
    	soupfluids.put("okroshka",0xffd0c776);
    	soupfluids.put("plain_milk",0xffffffff);
    	soupfluids.put("plain_water",0xff374780);
    	soupfluids.put("porridge",0xffc6b177);
    	soupfluids.put("poultry_soup",0xffbc9857);
    	soupfluids.put("pumpkin_soup",0xffd88f31);
    	soupfluids.put("pumpkin_soup_cream",0xffe5c58b);
    	soupfluids.put("rice_pudding",0xffd8d2bc);
    	soupfluids.put("scalded_milk",0xfff3f0e3);
    	soupfluids.put("seaweed_soup",0xff576835);
    	soupfluids.put("stock",0xffc1a242);
    	soupfluids.put("stracciatella",0xffbfbe5c);
    	soupfluids.put("ukha",0xffb78533);
    	soupfluids.put("vegetable_chowder",0xffa39a42);
    	soupfluids.put("vegetable_soup",0xff848929);
    	soupfluids.put("walnut_soup",0xffdcb072);
    	for(Entry<String, Integer> i:soupfluids.entrySet()) {
    		FLUIDS.register(i.getKey(), () -> new SoupFluid(new ForgeFlowingFluid.Properties(null,null,SoupAttributes.builder(STILL_SOUP_TEXTURE, STILL_SOUP_TEXTURE)
                .viscosity(1200).color(i.getValue()))
                .slopeFindDistance(1).explosionResistance(100F)));
    	}
    }
    public static RegistryObject<FlowingFluid> HOT_WATER = FLUIDS.register("hot_water", () -> {
        return new ForgeFlowingFluid.Source(SCFluids.HOT_WATER_PROPERTIES);
    });

    public static ForgeFlowingFluid.Properties HOT_WATER_PROPERTIES =
            new ForgeFlowingFluid.Properties(HOT_WATER, HOT_WATER, FluidAttributes.builder(STILL_FLUID_TEXTURE, FLOWING_FLUID_TEXTURE)
                    .color(0xFF3ABDFF).temperature(333)).block(null);
}
