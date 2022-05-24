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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.teammoeg.thermopolium.Contents.THPItems;
import com.teammoeg.thermopolium.Main;
import com.teammoeg.thermopolium.THPFluids;
import com.teammoeg.thermopolium.data.TranslationProvider;
import com.teammoeg.thermopolium.data.recipes.CookingRecipe;
import com.teammoeg.thermopolium.data.recipes.SerializeUtil;
import com.teammoeg.thermopolium.data.recipes.StewBaseCondition;
import com.teammoeg.thermopolium.data.recipes.baseconditions.FluidTag;
import com.teammoeg.thermopolium.data.recipes.baseconditions.FluidType;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.Item;
import net.minecraft.resources.IResource;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.data.ExistingFileHelper;

public class THPFHHeatGenerator implements IDataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
	protected final DataGenerator generator;
	private Path main;
	private ExistingFileHelper helper;

	public THPFHHeatGenerator(DataGenerator generatorIn,ExistingFileHelper efh) {
		this.generator = generatorIn;
		this.helper=efh;
	}

	@Override
	public void run(DirectoryCache cache) throws IOException {
		main=this.generator.getOutputFolder().resolve("data/frostedheart/temperature/");
		for(String s:THPFluids.getSoupfluids())
			if(s.equals("nail_soup")) {
				saveJson(cache,createData(s,0.125f,-1,1),main.resolve("food/"+s+".json"));
				saveJson(cache,createData(s,0.25f),main.resolve("drink/"+s+".json"));
			}else {
				saveJson(cache,createData(s,0.4f,-1,1),main.resolve("food/"+s+".json"));
				saveJson(cache,createData(s,0.4f),main.resolve("drink/"+s+".json"));
			}
	}
	@Override
	public String getName() {
		return Main.MODID+" fh heat generator";
	}
	private JsonObject createData(String name,float val) {
		JsonObject jo=new JsonObject();
		jo.addProperty("id",Main.MODID+":"+name);
		jo.addProperty("heat",val);
		return jo;
	}
	private JsonObject createData(String name,float val,float min,float max) {
		JsonObject jo=new JsonObject();
		jo.addProperty("id",Main.MODID+":"+name);
		jo.addProperty("min",min);
		jo.addProperty("max",max);
		jo.addProperty("heat",val);
		return jo;
	}
	private static void saveJson(DirectoryCache cache, JsonObject recipeJson, Path path) {
		try {
			String s = GSON.toJson(recipeJson);
			String s1 = SHA1.hashUnencodedChars(s).toString();
			if (!Objects.equals(cache.getHash(path), s1) || !Files.exists(path)) {
				Files.createDirectories(path.getParent());

				try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path)) {
					bufferedwriter.write(s);
				}
			}

			cache.putNew(path, s1);
		} catch (IOException ioexception) {
			LOGGER.error("Couldn't save data json {}", path, ioexception);
		}

	}
}
