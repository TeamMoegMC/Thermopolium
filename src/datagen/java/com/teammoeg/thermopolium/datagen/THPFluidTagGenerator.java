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

import java.nio.file.Path;
import java.util.stream.Collectors;

import com.teammoeg.thermopolium.Main;
import com.teammoeg.thermopolium.THPFluids;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.TagsProvider;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.Tags.IOptionalNamedTag;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

public class THPFluidTagGenerator extends TagsProvider<Fluid> {

	public THPFluidTagGenerator(DataGenerator dataGenerator, String modId, ExistingFileHelper existingFileHelper) {
		super(dataGenerator, Registry.FLUID, modId, existingFileHelper);
	}



	@Override
	protected void registerTags() {

		tag("stews").add(THPFluids.getAll().collect(Collectors.toList()).toArray(new Fluid[0]));
		tag(new ResourceLocation("frostedheart","drink")).addTag(otag("stews"));
		tag(new ResourceLocation("frostedheart","hot_drink")).addTag(otag("stews"));
		tag(new ResourceLocation("frostedheart","hidden_drink")).addTag(otag("stews"));
		tag(new ResourceLocation("watersource","drink")).add(ForgeRegistries.FLUIDS.getValue(mrl("nail_soup")));
	}

	private Builder<Fluid> tag(String s) {
		return this.getOrCreateBuilder(FluidTags.createOptional(mrl(s)));
	}

	private Builder<Fluid> tag(ResourceLocation s) {
		return this.getOrCreateBuilder(FluidTags.createOptional(s));
	}

	private ResourceLocation rl(RegistryObject<Fluid> it) {
		return it.getId();
	}

	private ResourceLocation rl(String r) {
		return new ResourceLocation(r);
	}

	private IOptionalNamedTag<Fluid> otag(String s) {
		return FluidTags.createOptional(mrl(s));
	}

	private IOptionalNamedTag<Fluid> atag(ResourceLocation s) {
		return FluidTags.createOptional(s);
	}

	private ResourceLocation mrl(String s) {
		return new ResourceLocation(Main.MODID, s);
	}

	private ResourceLocation frl(String s) {
		return new ResourceLocation("forge", s);
	}


	private ResourceLocation mcrl(String s) {
		return new ResourceLocation(s);
	}

	@Override
	public String getName() {
		return Main.MODID + " fluid tags";
	}

	@Override
	protected Path makePath(ResourceLocation id) {
		return this.generator.getOutputFolder()
				.resolve("data/" + id.getNamespace() + "/tags/fluids/" + id.getPath() + ".json");
	}
}
